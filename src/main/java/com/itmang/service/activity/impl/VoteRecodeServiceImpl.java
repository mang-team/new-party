package com.itmang.service.activity.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.PageConstant;
import com.itmang.exception.BaseException;

import com.itmang.mapper.activity.VoteInformationMapper;
import com.itmang.mapper.activity.VoteRecordMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.AddVoteRecordDTO;
import com.itmang.pojo.dto.FindVoteSignDTO;
import com.itmang.pojo.dto.UpdateVoteRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.entity.VoteInformation;
import com.itmang.pojo.entity.VoteRecord;
import com.itmang.pojo.vo.ChoiceVO;
import com.itmang.pojo.vo.VoteInformationPageVO;
import com.itmang.pojo.vo.VoteRecordPageVO;
import com.itmang.pojo.vo.VoteRecordVO;
import com.itmang.service.activity.VoteRecodeService;
import com.itmang.utils.IdGenerate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VoteRecodeServiceImpl extends ServiceImpl<VoteRecordMapper, VoteRecord>
        implements VoteRecodeService {

    @Resource
    private VoteRecordMapper voteRecordMapper;
    @Resource
    private VoteInformationMapper voteInformationMapper;
    @Resource
    private IdGenerate idGenerate;
    @Resource
    private UserMapper userMapper;


    /**
     * 新增投票记录信息
     * @param addVoteRecordDTO
     * @param userId
     */
    public void addVoteRecodeInformation(AddVoteRecordDTO addVoteRecordDTO, String userId) {
        //判断传进来的数据是否存在
        if (addVoteRecordDTO == null || StringUtils.isBlank(addVoteRecordDTO.getVoteInformationId())
            || StringUtils.isBlank(addVoteRecordDTO.getUserIds())) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //查询投票信息
        VoteInformation voteInformation =
                voteInformationMapper.selectById(addVoteRecordDTO.getVoteInformationId());
        if (voteInformation == null || voteInformation.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.VOTE_NOT_EXIST);
        }
        //判断当前时间是否是在投票开始时间之前的3分钟之内
        if (LocalDateTime.now().isBefore(voteInformation.getStartTime().minusMinutes(3))) {
            throw new BaseException(MessageConstant.VOTE_TIME_ERROR);
        }
        //满足要求，可以批量新增投票记录
        //1.先验证参与投票人员是否存在
        List<String> newVoterIds = new ArrayList<>();
        String[] userIds = addVoteRecordDTO.getUserIds().split(",");
        for (String id : userIds) {
            //判断当前用户是否存在
            if (id == null || id.equals("")) {
                continue;
            }
            //判断当前用户是否已经存在
            VoteRecord voteRecord = voteRecordMapper.selectOne(new LambdaQueryWrapper<VoteRecord>()
                    .eq(VoteRecord::getVoteInformationId, addVoteRecordDTO.getVoteInformationId())
                    .eq(VoteRecord::getUserId, id));
            if(voteRecord != null){
                continue;
            }
            newVoterIds.add(id);
        }
        //2.批量新增投票记录
        List<VoteRecord> voteRecordList = new ArrayList<>();
        if(newVoterIds.size() > 0){
            for (String id : newVoterIds) {
                VoteRecord voteRecord = new VoteRecord();
                voteRecord.setId(idGenerate.nextUUID(VoteRecord.class));
                voteRecord.setVoteInformationId(addVoteRecordDTO.getVoteInformationId());
                voteRecord.setUserId(id);
                voteRecord.setIsDelete(DeleteConstant.NO);
                voteRecord.setCreateBy(userId);
                voteRecord.setCreateTime(LocalDateTime.now());
            }
            this.saveBatch(voteRecordList);
        }
        //判断逻辑
        if (voteRecordList.size() == 0) {
            throw new BaseException(MessageConstant.ADD_FAIL);
        }

    }

    /**
     * 删除投票记录信息
     * @param ids
     * @param userId
     */
    public void deleteVoteRecodeInformation(String ids, String userId) {
        // 1. 入参校验
        if (StringUtils.isBlank(ids) || StringUtils.isBlank(userId)) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        List<String> idList = Arrays.stream(ids.split(","))
                .map(String::trim) // 去除空格，避免无效ID
                .filter(id -> !StringUtils.isBlank(id)) // 过滤空字符串ID
                .collect(Collectors.toList());
        if (idList.isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        // 2. 批量查询投票记录
        List<VoteRecord> voteRecordList = this.listByIds(idList);
        if (voteRecordList.isEmpty()) {
            throw new BaseException(MessageConstant.VOTE_RECORD_NOT_EXIST);
        }
        // 校验：传入的ID是否都存在（避免部分ID无效导致删除不完整）
        Set<String> existIds = voteRecordList.stream()
                .map(VoteRecord::getId)
                .collect(Collectors.toSet());
        List<String> invalidIds = idList.stream()
                .filter(id -> !existIds.contains(id))
                .collect(Collectors.toList());
        if (!invalidIds.isEmpty()) {
            throw new BaseException(MessageConstant.VOTE_RECORD_NOT_EXIST + "：" + String.join(",", invalidIds));
        }

        // 3. 分组处理：按投票信息ID分组（减少VoteInformation查询次数）
        Map<String, List<VoteRecord>> voteInfoIdToRecords = voteRecordList.stream()
                .collect(Collectors.groupingBy(VoteRecord::getVoteInformationId));

        // 4. 事务内同步更新投票信息（保证数据一致性）
        try {
            // 4.1 批量更新投票信息的参与用户ID（核心优化：修复原逻辑漏洞）
            for (Map.Entry<String, List<VoteRecord>> entry : voteInfoIdToRecords.entrySet()) {
                String voteInfoId = entry.getKey();
                List<String> voterIdsToDelete = entry.getValue().stream()
                        .map(VoteRecord::getUserId)
                        .distinct() // 去重，避免重复删除同一用户ID
                        .collect(Collectors.toList());

                // 查询投票信息（唯一查询，避免原逻辑中in条件的错误）
                VoteInformation voteInformation = voteInformationMapper.selectById(voteInfoId);
                if (voteInformation == null) {
                    throw new BaseException("投票信息不存在：" + voteInfoId);
                }

                // 处理用户ID集合（修复原逻辑：replace可能误删包含目标ID的字符串）
                String userIds = voteInformation.getUserIds();
                if (StringUtils.isNotBlank(userIds)) {
                    // 分割为列表 → 过滤要删除的ID → 重新拼接
                    List<String> userList = new ArrayList<>(Arrays.asList(userIds.split(",")));
                    userList.removeAll(voterIdsToDelete); // 批量删除目标用户ID
                    String newUserIds = String.join(",", userList);
                    voteInformation.setUserIds(newUserIds);
                }
                // 设置更新信息
                voteInformation.setUpdateBy(userId);
                voteInformation.setUpdateTime(LocalDateTime.now());
                voteInformationMapper.updateById(voteInformation);
            }

            // 4.2 批量删除投票记录
            List<String> deleteIds = voteRecordList.stream()
                    .map(VoteRecord::getId)
                    .collect(Collectors.toList());
            voteRecordMapper.deleteVoteIds(deleteIds);
        } catch (Exception e) {
            // 异常回滚，保证数据一致性
            throw new BaseException(MessageConstant.DELETE_VOTE_RECORD_FAILED);
        }
    }

    /**
     * 编辑投票记录信息
     * @param updateVoteRecordDTO
     * @param userId
     */
    public void updateVoteRecodeInformation(UpdateVoteRecordDTO updateVoteRecordDTO, String userId) {
        // 1. 对传进来的数据进行验证
        if (updateVoteRecordDTO == null || updateVoteRecordDTO.getVoteId() == null
            || updateVoteRecordDTO.getVoteId() == "" || updateVoteRecordDTO.getChoose() == null
            || updateVoteRecordDTO.getChoose() == ""    ) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        // 2. 根据ID查询投票记录信息
        VoteRecord Vote = this.baseMapper.selectOne(new QueryWrapper<VoteRecord>()
                        .eq("vote_information_id", updateVoteRecordDTO.getVoteId())
                        .eq("is_delete", DeleteConstant.NO)
                        .eq("user_id", userId)
        );
        if (Vote == null) {
            throw new BaseException(MessageConstant.VOTE_RECORD_NOT_EXIST);
        }
        // 2. 将 DTO 的字段更新到实体类中
        if (updateVoteRecordDTO.getChoose() != null && updateVoteRecordDTO.getChoose() != "") {
            Vote.setChoose(updateVoteRecordDTO.getChoose());
        }
        if (StringUtils.isNotBlank(updateVoteRecordDTO.getNotes())) {
            Vote.setNotes(updateVoteRecordDTO.getNotes());
        }
        // 3. 设置更新信息
        Vote.setUpdateBy(userId); // 或者从登录用户获取
        Vote.setUpdateTime(LocalDateTime.now());
        // 4. 执行更新
        boolean isUpdated = this.updateById(Vote);
        if (!isUpdated) {
            throw new BaseException("修改投票信息失败");
        }
    }

    /**
     * 分页查询投票记录信息
     * @param findVoteSignDTO
     * @return
     */
    public PageResult queryVoteRecodeList(FindVoteSignDTO findVoteSignDTO) {
        //配置默认页码1和分页大小5
        if(findVoteSignDTO.getPageNum() == null || findVoteSignDTO.getPageNum() < 1){
            findVoteSignDTO.setPageNum(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(findVoteSignDTO.getPageSize() == null || findVoteSignDTO.getPageSize() < 1){
            findVoteSignDTO.setPageSize(PageConstant.PAGE_SIZE);
        }
        PageHelper.startPage(findVoteSignDTO.getPageNum(), findVoteSignDTO.getPageSize());
        List<VoteRecordPageVO> votePageVOList = voteRecordMapper.queryVoteRecordList(findVoteSignDTO);
        PageInfo<VoteRecordPageVO> pageInfo = new PageInfo<>(votePageVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }




    /**
     * 查看投票记录信息详情
     * @param id 投票记录ID
     * @return 投票记录详情VO
     */
    public VoteRecordVO searchVoteRecordInformation(String id) {
        // 1. 校验入参：ID不能为空
        if (StringUtils.isBlank(id)) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        // 2. 查询投票记录（过滤已删除的记录）
        VoteRecord voteRecord = this.getById(id);
        if (voteRecord == null || DeleteConstant.YES.equals(voteRecord.getIsDelete())) {
            throw new BaseException(MessageConstant.NOT_FOUND_VOTE_RECORD);
        }

        // 3. 查询对应的投票信息
        String voteInfoId = voteRecord.getVoteInformationId();
        VoteInformation voteInformation = voteInformationMapper.selectById(voteInfoId);
        if (voteInformation == null) {
            throw new BaseException(MessageConstant.VOTE_NOT_EXIST);
        }

        // 4. 处理投票选项：先判断options是否为null，避免空指针异常
        List<?> options = voteInformation.getOptions();
        int voteCount = options == null ? 0 : options.size();
        List<VoteRecordVO.ChoiceVO> voteList = new ArrayList<>();

        if (voteCount > 0) {
            // 5. 批量查询该投票下所有选项的投票记录（过滤已删除的）
            QueryWrapper<VoteRecord> wrapper = new QueryWrapper<VoteRecord>()
                    .eq("vote_information_id", voteInfoId)
                    .eq("is_delete", DeleteConstant.NO) // 排除已删除的投票记录
                    .inSql("choose", buildChooseSql(voteCount)); // 只查询1到voteCount的选项
            List<VoteRecord> allVoteRecords = voteRecordMapper.selectList(wrapper);

            // 6. 提取所有用户ID，批量查询用户信息（优化性能：一次查询所有用户，避免循环查库）
            Set<String> userIds = allVoteRecords.stream()
                    .map(VoteRecord::getUserId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            Map<String, String> userMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(userIds)) {
                List<User> userList = userMapper.selectBatchIds(userIds);
                userMap = userList.stream()
                        .collect(Collectors.toMap(User::getId, User::getUserName, (k1, k2) -> k1)); // 处理重复ID，保留第一个
            }

            // 7. 按选项分组，构建ChoiceVO列表（适配新的VO结构）
            Map<String, List<VoteRecord>> chooseGroup = allVoteRecords.stream()
                    .collect(Collectors.groupingBy(VoteRecord::getChoose));

            for (int i = 1; i <= voteCount; i++) {
                String choose = String.valueOf(i);
                List<VoteRecord> records = chooseGroup.getOrDefault(choose, new ArrayList<>());
                if (CollectionUtils.isEmpty(records)) {
                    continue;
                }

                // 构建用户ID->姓名的映射（对应ChoiceVO的names字段）
                Map<String, String> names = new HashMap<>();
                for (VoteRecord record : records) {
                    String userId = record.getUserId();
                    // 处理用户不存在的情况，默认显示“未知用户”
                    names.put(userId, userMap.getOrDefault(userId, "未知用户"));
                }

                // 构建单个ChoiceVO（使用新的VO结构）
                VoteRecordVO.ChoiceVO choiceVO = VoteRecordVO.ChoiceVO.builder()
                        .names(names) // 对应Map<String, String> names
                        .count(records.size()) // 选项的投票人数
                        .build();
                voteList.add(choiceVO);
            }
        }

        // 8. 构建并返回最终的VoteRecordVO
        String userName = userMapper.selectById(voteRecord.getUserId()).getUserName();
        return VoteRecordVO.builder()
                .id(voteRecord.getId())
                .voteInformationId(voteInfoId)
                .userId(voteRecord.getUserId())
                .userName(userName)
                .voteChoose(voteRecord.getChoose())
                .voteTime(voteRecord.getUpdateTime()) // 注意：如果有createTime，建议用createTime作为投票时间
                .voteList(voteList)
                .build();
    }

    /**
     * 构建选项编号的SQL片段（用于inSql查询）
     * @param voteCount 选项数量
     * @return 如：SELECT 1 num UNION ALL SELECT 2 UNION ALL SELECT 3
     */
    private String buildChooseSql(int voteCount) {
        StringBuilder sql = new StringBuilder("SELECT 1 num");
        for (int i = 2; i <= voteCount; i++) {
            sql.append(" UNION ALL SELECT ").append(String.valueOf(i));
        }
//        sql.append(")");
        return sql.toString();
    }


}
