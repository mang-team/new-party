package com.itmang.service.party.Impl;



import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;

import com.itmang.mapper.party.MemberMapper;
import com.itmang.pojo.dto.MemberAddDTO;
import com.itmang.pojo.dto.MemberQueryDTO;
import com.itmang.pojo.dto.MemberUpdateDTO;

import com.itmang.pojo.entity.*;

import com.itmang.pojo.vo.MemberBriefVO;
import com.itmang.pojo.vo.MemberVO;
import com.itmang.service.party.MemberService;
import com.itmang.utils.IdGenerate;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;




@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;



    /**
     * 分页查询成员列表
     * @param memberQueryDTO
     * @return
     */
    public PageResult queryMemberList(MemberQueryDTO memberQueryDTO) {
        PageHelper.startPage(memberQueryDTO.getPage(), memberQueryDTO.getPageSize());
        List<MemberBriefVO> memberBriefVOList = memberMapper.selectMemberList(memberQueryDTO);
        PageInfo<MemberBriefVO> pageInfo = new PageInfo<>(memberBriefVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
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
            throw new BaseException("成员不存在");
        }

        validateMemberDTO(memberUpdateDTO);

        Member member = new Member();
        BeanUtils.copyProperties(memberUpdateDTO, member);


        // 处理特殊字段映射
        member.setClassInfo(memberUpdateDTO.getClassInfo());

        member.setUpdateBy(BaseContext.getCurrentId());
        member.setUpdateTime(LocalDateTime.now());

        int result = memberMapper.updateMember(member);
        if (result <= 0) {
            throw new BaseException("更新成员信息失败");
        }
    }



    /**
     * 删除成员信息（可批量）
     * @param ids
     */

    public void deleteMember(String[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BaseException(MessageConstant.MEMBER_NOT_EXISTS);
        }

        List<String> canDeleteIds = new ArrayList<>();
        List<String> invalidIds = new ArrayList<>();

        // 检查成员状态
        for (String id : ids) {
            MemberVO member = memberMapper.selectById(id);
            if (member == null || member.getIsDelete().intValue()==DeleteConstant.YES) {
                invalidIds.add(id);
            } else {
                canDeleteIds.add(id);
            }
        }

        // 所有ID都无效
        if (canDeleteIds.isEmpty()) {
            throw new BaseException(MessageConstant.MEMBER_INFORMATION_ALREADY_DELETED);
        }

        // 执行删除逻辑
        if(!canDeleteIds.isEmpty()) {
            memberMapper.removeBatchByIds(canDeleteIds.toArray(new String[0]));
        }
        if (!invalidIds.isEmpty()) {
            if(canDeleteIds.isEmpty()){
                throw new BaseException(MessageConstant.MEMBER_INFORMATION_FAIL_DELETED);
            }else{
        throw new BaseException(MessageConstant.MEMBER_INFORMATION_PART_DELETED);

            }
        }

    }



    /**
     * 查询详细成员信息
=======
    @Transactional  // 添加事务注解
    public void deleteMember(String[] ids) {
        List<String> canDeleteIds = new ArrayList<>();


        //判断成员是否存在
        for (String id : ids) {
            MemberVO member = memberMapper.selectById(id);
            if (member == null || member.getIsDelete().equals(DeleteConstant.YES)) {


                continue;
            }

            //将满足条件的存入canDeleteIds集合中
            canDeleteIds.add(id);
        }
        // 执行删除逻辑
        if (!canDeleteIds.isEmpty()) {
            memberMapper.removeBatchByIds(canDeleteIds.toArray(new String[0]));
            // 处理删除结果
            if (canDeleteIds.size() != ids.length) {
                if (canDeleteIds.isEmpty()) {
                    throw new BaseException(MessageConstant.MEMBER_INFORMATION_PART_DELETED);
                } else {
                    throw new BaseException(MessageConstant.MEMBER_INFORMATION_FAIL_DELETED);
                }

            }
        }
    }


    /**
     * 查询详细成员信息（可批量）
>>>>>>> 88d9f0c2c9bc747d02f570e2aec0417b72fc54bd
     * @param id
     */
    @Override
    public MemberVO getMemberDetailById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BaseException(MessageConstant.MEMBER_ID_CANNOT_BE_NULL);
        }

        // 查询成员信息
        MemberVO memberVO = memberMapper.selectById(id);
        if (memberVO == null ) {
            throw new BaseException(MessageConstant.MEMBER_NOT_EXISTS);
        }

        return memberVO;

    }




    /**
     * 增加成员信息（可批量）
     * @param memberDTOList
     */
    @Transactional
    @Override
    public void batchAddMembers(List<MemberAddDTO> memberDTOList) {
        log.info("开始批量新增事务，记录数: {}", memberDTOList.size());
        Set<String> phones = new HashSet<>();
        Set<String> idCards = new HashSet<>();


        List<Member> memberList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        // 校验和转换
        for (int i = 0; i < memberDTOList.size(); i++) {
            MemberAddDTO dto = memberDTOList.get(i);
            int recordNumber = i + 1;

            try {
                validateMemberDTO(dto);

                if (!phones.add(dto.getTelephone())) {
                    errorMessages.add("第" + recordNumber + "条记录：手机号重复: " + dto.getTelephone());
                    continue;
                }
                if (!idCards.add(dto.getIdCard())) {
                    errorMessages.add("第" + recordNumber + "条记录：身份证号重复: " + dto.getIdCard());
                    continue;
                }



                if (memberMapper.findExistingUserId(dto.getUserId())==null) {
                    errorMessages.add("第" + recordNumber + "条记录：UserId不存在: " + dto.getUserId());
                    continue;
                }


                if (memberMapper.findUserIdFromMember(dto.getUserId())!=null) {

                    errorMessages.add("第" + recordNumber + "条记录：该用户已经是成员: " + dto.getUserId());
                    continue;
                }

                memberList.add(convertToMember(dto));

            } catch (BaseException e) {
                errorMessages.add("第" + recordNumber + "条记录校验失败: " + e.getMessage());
            }
        }

        // 统一处理错误
        if (!errorMessages.isEmpty()) {
            throw new BaseException("批量新增校验失败: " + String.join("; ", errorMessages));
        }

        // 批量插入（只调用一次）
        if (!memberList.isEmpty()) {
            log.info("开始插入 {} 条记录", memberList.size());
            int result = memberMapper.batchInsertMembers(memberList);

            int result_update = memberMapper.batchUpdateUsers(memberList);
            if(result_update==result){
                log.info("插入完成，影响行数: {}", result);

            }

            if (result != memberList.size()||result_update!=memberList.size()) {

                throw new BaseException("插入行数不匹配");
            }
        }
    }

    /**
     * 校验信息（可批量）
     * @param dto
     */
    private void validateMemberDTO(MemberUpdateDTO dto) {



        if (StringUtils.isBlank(dto.getDepartmentId())) {
            throw new BaseException(MessageConstant.DEPARTMENT_ID_CANNOT_BE_NULL);
        }
        if (StringUtils.isBlank(dto.getName())) {
            throw new BaseException(MessageConstant.NAME_CANNOT_BE_NULL);
        }

        if (StringUtils.isBlank(dto.getUserId())) {
            throw new BaseException(MessageConstant.USER_ID_CANNOT_BE_NULL);
        }


        if (dto.getSex() == null) {
            throw new BaseException(MessageConstant.SEX_CANNOT_BE_NULL);
        }
        if (dto.getPoliticalStatus() == null) {
            throw new BaseException();
        }
        if (StringUtils.isBlank(dto.getIdCard())) {
            throw new BaseException(MessageConstant.ID_CARD_CANNOT_BE_NULL);
        }
        if (StringUtils.isBlank(dto.getTelephone())) {
            throw new BaseException(MessageConstant.TELEPHONE_CANNOT_BE_NULL);
        }


        // 身份证号格式校验
        if (!isValidIdCard(dto.getIdCard())) {
            throw new BaseException(MessageConstant.ID_CARD_FORMAT_INCORRECT);
        }

        // 枚举值校验
        if (dto.getSex() != 1 && dto.getSex() != 2) {
            throw new BaseException(MessageConstant.SEX_VALUE_MUST_BE_1_OR_2);
        }
        if (dto.getPoliticalStatus() < 1 || dto.getPoliticalStatus() > 6) {
            throw new BaseException(MessageConstant.POLITICAL_STATUS_VALUE_MUST_BE_1_TO_6);
        }
        if (dto.getIsAtSchool() != null && (dto.getIsAtSchool() < 1 || dto.getIsAtSchool() > 2)) {
            throw new BaseException(MessageConstant.IS_AT_SCHOOL_VALUE_MUST_BE_1_OR_2);
        }
    }


    /**
     * 校验信息（可批量）
     * @param dto
     */
    private void validateMemberDTO(MemberAddDTO dto) {

        if (StringUtils.isBlank(dto.getUserId())) {
            throw new BaseException(MessageConstant.USER_ID_CANNOT_BE_NULL);
        }

        if (StringUtils.isBlank(dto.getDepartmentId())) {
            throw new BaseException(MessageConstant.DEPARTMENT_ID_CANNOT_BE_NULL);
        }
        if (StringUtils.isBlank(dto.getName())) {
            throw new BaseException(MessageConstant.NAME_CANNOT_BE_NULL);
        }
        if (dto.getSex() == null) {
            throw new BaseException(MessageConstant.SEX_CANNOT_BE_NULL);
        }
        if (dto.getPoliticalStatus() == null) {
            throw new BaseException();
        }
        if (StringUtils.isBlank(dto.getIdCard())) {
            throw new BaseException(MessageConstant.ID_CARD_CANNOT_BE_NULL);
        }
        if (StringUtils.isBlank(dto.getTelephone())) {
            throw new BaseException(MessageConstant.TELEPHONE_CANNOT_BE_NULL);
        }


        // 身份证号格式校验
        if (!isValidIdCard(dto.getIdCard())) {
            throw new BaseException(MessageConstant.ID_CARD_FORMAT_INCORRECT);
        }

        // 枚举值校验
        if (dto.getSex() != 1 && dto.getSex() != 2) {
            throw new BaseException(MessageConstant.SEX_VALUE_MUST_BE_1_OR_2);
        }
        if (dto.getPoliticalStatus() < 1 || dto.getPoliticalStatus() > 6) {
            throw new BaseException(MessageConstant.POLITICAL_STATUS_VALUE_MUST_BE_1_TO_6);
        }

//        if (dto.getIsAtSchool() != null && (dto.getIsAtSchool() < 1 || dto.getIsAtSchool() > 2)) {
//            throw new BaseException(MessageConstant.IS_AT_SCHOOL_VALUE_MUST_BE_1_OR_2);
//        }

    }




    // DTO转Entity方法
    private Member convertToMember(MemberAddDTO dto) {
        Member member = new Member();

        // 设置基础信息
        IdGenerate idGenerate = new IdGenerate();
        String memberId = idGenerate.nextUUID(Member.class);
        member.setId(memberId);
        member.setUserId(dto.getUserId());
        member.setDepartmentId(dto.getDepartmentId());
        member.setName(dto.getName());
        member.setSex(dto.getSex());
        member.setMajor(dto.getMajor());
        member.setClassInfo(dto.getClassInfo());
        member.setPoliticalStatus(dto.getPoliticalStatus());
        member.setIdCard(dto.getIdCard());
        member.setTelephone(dto.getTelephone());
        member.setEducationBackground(dto.getEducationBackground());
        member.setNationality(dto.getNationality());
        member.setNativePlace(dto.getNativePlace());

        // 设置时间字段（注意处理可能的null值）
        member.setDateOfBirth(dto.getDateOfBirth());
        member.setJoinCommunistTime(dto.getJoinCommunistTime());
        member.setSubmitCommunistTime(dto.getSubmitCommunistTime());
        member.setContacts(dto.getContacts());
        member.setRecommendingTime(dto.getRecommendingTime());
        member.setConfirmedActiveMemberTime(dto.getConfirmedActiveMemberTime());
        member.setConfirmedDevelopmentTargetTime(dto.getConfirmedDevelopmentTargetTime());
        member.setConfirmedProbationaryMemberTime(dto.getConfirmedProbationaryMemberTime());
        member.setBecomeFullMemberTime(dto.getBecomeFullMemberTime());

        // 设置状态字段（使用DTO中的值或默认值）

//        member.setIsAtSchool(dto.getIsAtSchool() != null ? dto.getIsAtSchool() : 1);
//        member.setIsDelete(dto.getIsDelete() != null ? dto.getIsDelete() : 2);
          member.setIsAtSchool(1);
          member.setIsDelete(2);

        // 设置审计字段（当前时间）
        LocalDateTime now = LocalDateTime.now();
        member.setCreateTime(now);
        member.setUpdateTime(now);

        // 创建人和更新人

        String currentUser = BaseContext.getCurrentId();
        if (StringUtils.isBlank(currentUser)) {

            currentUser = "AdMIN"; // 默认系统用户
            log.warn("当前用户ID为空，使用默认值: AdMIN");

        }
        member.setCreateBy(currentUser);
        member.setUpdateBy(currentUser);

        return member;
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