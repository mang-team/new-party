package com.itmang.service.party.Impl;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.exception.BaseException;
import com.itmang.mapper.party.MemberMapper;
import com.itmang.pojo.dto.MemberAddDTO;
import com.itmang.pojo.dto.MemberQueryDTO;
import com.itmang.pojo.dto.MemberUpdateDTO;
import com.itmang.pojo.entity.Member;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.BankPageVO;
import com.itmang.pojo.vo.MemberVO;
import com.itmang.service.party.MemberService;
import com.itmang.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;


    /**
     * 分页查询题库列表
     * @param memberQueryDTO
     * @return
     */
    public PageResult queryMemberList(MemberQueryDTO memberQueryDTO) {
        PageHelper.startPage(memberQueryDTO.getPage(), memberQueryDTO.getPageSize());
        List<MemberVO> memberVOList = memberMapper.selectMemberList(memberQueryDTO);
        PageInfo<MemberVO> pageInfo = new PageInfo<>(memberVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


    /**
     * 增加成员信息
     * @param memberAddDTO
     */
    @Override
    @Transactional
    public void addMember(MemberAddDTO memberAddDTO) {
        Member member = new Member();
        BeanUtils.copyProperties(memberAddDTO, member);

        // 处理特殊字段映射
        member.setClassInfo(memberAddDTO.getClassInfo());

        // 添加重复性检查
        MemberVO exists = memberMapper.selectById(memberAddDTO.getIdCard());
        if (exists!=null) {
            throw new RuntimeException("成员已存在");
        }
        else{
        // 设置默认值
        member.setIsDelete(2); // 默认未删除
        member.setCreateBy("admin");
        member.setUpdateBy("admin");
        int result = memberMapper.insertMember(member);
        if (result <= 0) {
            throw new RuntimeException("新增成员失败");
        }
    }
    }



    /**
     * 更新成员信息
     * @param memberUpdateDTO
     */
    @Override
    @Transactional
    public void updateMember(MemberUpdateDTO memberUpdateDTO) {
        MemberVO existingMember = memberMapper.selectById(memberUpdateDTO.getId());
        if (existingMember == null) {
            throw new RuntimeException("成员不存在");
        }

        Member member = new Member();
        BeanUtils.copyProperties(memberUpdateDTO, member);

        // 处理特殊字段映射
        member.setClassInfo(memberUpdateDTO.getClassInfo());
        member.setUpdateBy("admin"); // 实际应从登录信息中获取

        int result = memberMapper.updateMember(member);
        if (result <= 0) {
            throw new RuntimeException("更新成员信息失败");
        }
    }



    /**
     * 删除成员信息（可批量）
     * @param ids
     */
    public void deleteMember(String[] ids) {
        List<String> canDeleteIds = new ArrayList<>();
        //判断成员是否存在
        for (String id : ids) {
            MemberVO member = memberMapper.selectById(id);
            if(member == null || member.getIsDelete().equals(DeleteConstant.YES)){
                continue;
            }
            //将满足条件的存入canDeleteIds集合中
            canDeleteIds.add(id);
        }
        // 执行删除逻辑
        if(!canDeleteIds.isEmpty()) {
            memberMapper.removeBatchByIds(canDeleteIds.toArray(new String[0]));
            if(canDeleteIds.size() != ids.length){
                throw new BaseException(MessageConstant.QUESTION_PART_DELETED);
            }
        }else{
            throw new BaseException(MessageConstant.QUESTION_DELETED_FAIL);
        }

    }

    @Override
    public MemberVO getMemberDetailById(String id) {
        MemberVO member = memberMapper.selectById(id);
        return Result.success(member).getData();
    }




    /**
     * 增加成员信息（可批量）
     * @param memberDTOList
     */

    @Override
    public void batchAddMembers(List<MemberAddDTO> memberDTOList) {
        if (CollectionUtils.isEmpty(memberDTOList)) {
            throw new BaseException("成员列表不能为空");
        }

        // 批量校验
        validateBatchMembers(memberDTOList);

        // 转换DTO为Entity
        List<Member> members = convertToMembers(memberDTOList);

        // 分批插入（每500条一批，避免SQL过长）
        int batchSize = 500;
        int totalBatches = (int) Math.ceil((double) members.size() / batchSize);
        int totalInserted = 0;

        for (int i = 0; i < totalBatches; i++) {
            int fromIndex = i * batchSize;
            int toIndex = Math.min(fromIndex + batchSize, members.size());
            List<Member> batchList = members.subList(fromIndex, toIndex);

            int result = memberMapper.batchInsertMembers(batchList);
            totalInserted += result;

            log.info("第{}批次插入完成，本批插入{}条，累计插入{}条",
                    i + 1, result, totalInserted);
        }

        if (totalInserted != memberDTOList.size()) {
            throw new BaseException(String.format(
                    "批量新增成员失败，预期%d条，实际成功%d条",
                    memberDTOList.size(), totalInserted));
        }

        log.info("批量新增成员完成，总共插入{}条记录", totalInserted);
    }


    private void validateBatchMembers(List<MemberAddDTO> memberDTOList) {
        Set<String> phones = new HashSet<>();
        Set<String> idCards = new HashSet<>();

        // 先批量查询已存在的手机号和身份证号
        List<String> phoneList = memberDTOList.stream()
                .map(MemberAddDTO::getTelephone)
                .collect(Collectors.toList());
        List<String> idCardList = memberDTOList.stream()
                .map(MemberAddDTO::getIdCard)
                .collect(Collectors.toList());

        Set<String> existingPhones = memberMapper.findExistingPhones(phoneList);
        Set<String> existingIdCards = memberMapper.findExistingIdCards(idCardList);

        for (int i = 0; i < memberDTOList.size(); i++) {
            MemberAddDTO dto = memberDTOList.get(i);

            try {
                // 基础校验（会触发@Valid注解的校验）
                validateMemberDTO(dto);

                // 检查批次内重复
                if (!phones.add(dto.getTelephone())) {
                    throw new BaseException("第" + (i + 1) + "条记录：手机号重复: " + dto.getTelephone());
                }
                if (!idCards.add(dto.getIdCard())) {
                    throw new BaseException("第" + (i + 1) + "条记录：身份证号重复: " + dto.getIdCard());
                }

                // 检查数据库中是否已存在
                if (existingPhones.contains(dto.getTelephone())) {
                    throw new BaseException("第" + (i + 1) + "条记录：手机号已存在: " + dto.getTelephone());
                }
                if (existingIdCards.contains(dto.getIdCard())) {
                    throw new BaseException("第" + (i + 1) + "条记录：身份证号已存在: " + dto.getIdCard());
                }

            } catch (BaseException e) {
                throw new BaseException("第" + (i + 1) + "条记录校验失败: " + e.getMessage());
            }
        }
    }

    /**
     * 校验信息（可批量）
     * @param dto
     */
    private void validateMemberDTO(MemberAddDTO dto) {

        if (StringUtils.isBlank(dto.getUserId())) {
            throw new BaseException("用户ID不能为空");
        }
        if (StringUtils.isBlank(dto.getDepartmentId())) {
            throw new BaseException("部门ID不能为空");
        }
        if (StringUtils.isBlank(dto.getName())) {
            throw new BaseException("姓名不能为空");
        }
        if (dto.getSex() == null) {
            throw new BaseException("性别不能为空");
        }
        if (dto.getPoliticalStatus() == null) {
            throw new BaseException("政治面貌不能为空");
        }
        if (StringUtils.isBlank(dto.getIdCard())) {
            throw new BaseException("身份证号不能为空");
        }
        if (StringUtils.isBlank(dto.getTelephone())) {
            throw new BaseException("手机号不能为空");
        }

        // 手机号格式校验
        if (!Pattern.matches("^1[3-9]\\d{9}$", dto.getTelephone())) {
            throw new BaseException("手机号格式不正确");
        }

        // 身份证号格式校验
        if (!isValidIdCard(dto.getIdCard())) {
            throw new BaseException("身份证号格式不正确");
        }

        // 枚举值校验
        if (dto.getSex() != 1 && dto.getSex() != 2) {
            throw new BaseException("性别值必须是1或2");
        }
        if (dto.getPoliticalStatus() < 1 || dto.getPoliticalStatus() > 6) {
            throw new BaseException("政治面貌值必须在1-6之间");
        }
        if (dto.getIsAtSchool() != null && (dto.getIsAtSchool() < 1 || dto.getIsAtSchool() > 2)) {
            throw new BaseException("是否在校值必须是1或2");
        }
    }


    // 转换DTO为Entity
    private List<Member> convertToMembers(List<MemberAddDTO> dtos) {
        String currentUser = SecurityUtil.getCurrentUsername();
        LocalDateTime now = LocalDateTime.now();

        return dtos.stream().map(dto -> {
            Member member = new Member();

            // 基础信息拷贝
            BeanUtils.copyProperties(dto, member);

            // 设置系统字段
            member.setCreateBy(currentUser);
            member.setUpdateBy(currentUser);
            member.setCreateTime(now);
            member.setUpdateTime(now);
            member.setIsDelete(2); // 未删除

            // 确保默认值
            if (member.getIsAtSchool() == null) {
                member.setIsAtSchool(1); // 默认在校
            }

            return member;
        }).collect(Collectors.toList());
    }


    // 身份证校验工具方法
    private boolean isValidIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return false;
        }
        // 简单的身份证格式校验
        String regex = "^[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
        return Pattern.matches(regex, idCard);
    }

}
