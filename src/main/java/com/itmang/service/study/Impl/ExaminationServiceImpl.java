package com.itmang.service.study.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.*;
import com.itmang.exception.BaseException;
import com.itmang.mapper.study.ExaminationInformationMapper;
import com.itmang.mapper.study.QuestionBankMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.*;
import com.itmang.pojo.entity.*;
import com.itmang.pojo.vo.*;
import com.itmang.service.study.ExaminationService;
import com.itmang.utils.IdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ExaminationServiceImpl extends ServiceImpl<ExaminationInformationMapper, ExaminationInformation> implements ExaminationService {

    @Autowired
    private ExaminationInformationMapper examinationInformationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionBankMapper questionBankMapper;


    /**
     * 通用题目ID处理函数：分割ID字符串→校验题目有效性→收集结果
     * @param questionIdsStr 逗号分隔的题目ID字符串（可能为null/空/FlagConstant.NULL）
     * @param questionType 题目类型（单选/多选/判断/填空，来自QuestionOptionConstant）
     * @return 处理结果（有效ID列表+待更新选中状态ID列表）
     */
    private QuestionProcessResult processQuestionIds(String questionIdsStr, String questionType) {
        QuestionProcessResult result = new QuestionProcessResult();
        // 跳过无效字符串（null/空/FlagConstant.NULL）
        if (questionIdsStr == null || questionIdsStr.isEmpty() || FlagConstant.NULL.equals(questionIdsStr)) {
            return result;
        }
        // 分割ID字符串为数组
        String[] questionIds = questionIdsStr.split(",");
        // 跳过空数组或第一个元素为空的情况
        if (questionIds.length == 0 || (questionIds[0] == null && questionIds[0].isEmpty())) {
            return result;
        }
        // 校验每个题目ID的有效性
        for (String questionId : questionIds) {
            if (questionId == null || questionId.isEmpty()) {
                continue; // 跳过空ID
            }
            // 查询题目信息
            QuestionBank questionBank = questionBankMapper.selectById(questionId);
            // 校验：题目存在 + 未删除 + 类型匹配
            if (questionBank != null
                    && DeleteConstant.NO.equals(questionBank.getIsDelete())
                    && Objects.equals(questionType, questionBank.getType())) {
                result.getValidQuestionIds().add(questionId);
                // 标记：需更新选中状态（isChoose=DISABLE）
                if (StatusConstant.DISABLE.equals(questionBank.getIsChoose())) {
                    result.getToUpdateSelectedIds().add(questionId);
                }
            }
        }
        return result;
    }


    /**
     * 新增考试信息
     * @param addExaminationDTO
     */
    public void addExaminationInformation(AddExaminationDTO addExaminationDTO) {
        //判断是否有传入考试时间的范围，并且结束时间要大于开始时间，而且开始时间要大于等于现在的系统时间
        if((addExaminationDTO.getStartTime() == null || addExaminationDTO.getEndTime() == null)
            || addExaminationDTO.getEndTime().isBefore(addExaminationDTO.getStartTime())
            || addExaminationDTO.getStartTime().isBefore(LocalDateTime.now())){
            throw new BaseException(MessageConstant.EXAMINATION_TIME_ERROR);
        }
        //判断是否有传入各类题型的分值
        if(addExaminationDTO.getScoreValue() == null){
            throw new BaseException(MessageConstant.EXAMINATION_SCORE_ERROR);
        }
        //后续需要保证分值的形式为"?,?,?,?"
        String[] scoreValues = addExaminationDTO.getScoreValue().split(",");
        //判断长度是否为4，如果不是4个，则返回错误信息
        if(scoreValues.length != 4){
            throw new BaseException(MessageConstant.EXAMINATION_SCORE_ERROR);
        }
        //先查询用户都是否存在
        //用户的id集合是用逗号分隔的字符串，先将数据进行分割
        List<String> participateUserIds = new ArrayList<>();
        String[] userIds = addExaminationDTO.getUserIds().split(",");
        for (String userId : userIds) {
            //判断用户是否存在
            User user = userMapper.selectById(userId);
            if ( user != null && user.getIsDelete().equals(DeleteConstant.NO)
                && user.getStatus().equals(StatusConstant.ENABLE) ) {
                //加入集合
                participateUserIds.add(userId);
            }
        }
        //进行业务判断
        if (!participateUserIds.isEmpty()) {
            // 调用通用函数处理四类题目（核心重构点）
            QuestionProcessResult singleResult = processQuestionIds(addExaminationDTO.getSingleChoiceIds(), QuestionOptionConstant.SINGLE_CHOICE);
            QuestionProcessResult multiResult = processQuestionIds(addExaminationDTO.getMultipleChoiceIds(), QuestionOptionConstant.MULTI_CHOICE);
            QuestionProcessResult judgeResult = processQuestionIds(addExaminationDTO.getJudgeIds(), QuestionOptionConstant.JUDGEMENT);
            QuestionProcessResult fillResult = processQuestionIds(addExaminationDTO.getFillBlankIds(), QuestionOptionConstant.FILL_BLANKS);

            // 提取有效题目ID列表
            List<String> singleValidIds = singleResult.getValidQuestionIds();
            List<String> multiValidIds = multiResult.getValidQuestionIds();
            List<String> judgeValidIds = judgeResult.getValidQuestionIds();
            List<String> fillValidIds = fillResult.getValidQuestionIds();

            // 合并需更新选中状态的题目ID
            List<String> toUpdateChoiceIds = new ArrayList<>();
            toUpdateChoiceIds.addAll(singleResult.getToUpdateSelectedIds());
            toUpdateChoiceIds.addAll(multiResult.getToUpdateSelectedIds());
            toUpdateChoiceIds.addAll(judgeResult.getToUpdateSelectedIds());
            toUpdateChoiceIds.addAll(fillResult.getToUpdateSelectedIds());

            // 更新题目选中状态
            if (!toUpdateChoiceIds.isEmpty()) {
                questionBankMapper.updateQuestionBankSelected(
                        toUpdateChoiceIds.toArray(new String[0]),StatusConstant.ENABLE);
            }
            // 更新题目次数
            //对新加入的题目增加次数并修改
            List<String> addQuestionIds = new ArrayList<>();
            addQuestionIds.addAll(singleValidIds);
            addQuestionIds.addAll(multiValidIds);
            addQuestionIds.addAll(judgeValidIds);
            addQuestionIds.addAll(fillValidIds);
            for (String addQuestionId : addQuestionIds) {
                questionBankMapper.updateQuestionBankTimes(addQuestionId, TimesConstant.ADD);
            }


            // 转换ID列表为字符串
            String userIdsStr = String.join(",", participateUserIds);
            String singleIdsStr = String.join(",", singleValidIds);
            String multiIdsStr = String.join(",", multiValidIds);
            String judgeIdsStr = String.join(",", judgeValidIds);
            String fillIdsStr = String.join(",", fillValidIds);

            // 生成考试ID
            IdGenerate idGenerate = new IdGenerate();
            String examinationId = idGenerate.nextUUID(ExaminationInformation.class);

            // 计算总题目数量
            Integer totalQuestionCount = singleValidIds.size() + multiValidIds.size() + judgeValidIds.size() + fillValidIds.size();
            if (totalQuestionCount == 0) {
                throw new BaseException(MessageConstant.QUESTION_ADD_FAILED);
            }

            // 构建考试信息并插入数据库
            ExaminationInformation examinationInfo = ExaminationInformation.builder()
                    .id(examinationId)
                    .type(ExaminationConstant.EXAMINATION_INFORMATION)
                    .examinationName(addExaminationDTO.getExaminationName())
                    .userIds(userIdsStr)
                    .scoreValue(addExaminationDTO.getScoreValue())
                    .singleChoiceIds(singleIdsStr)
                    .multipleChoiceIds(multiIdsStr)
                    .judgeIds(judgeIdsStr)
                    .fillBlankIds(fillIdsStr)
                    .questionQuantity(totalQuestionCount)
                    .startTime(addExaminationDTO.getStartTime())
                    .endTime(addExaminationDTO.getEndTime())
                    .isDelete(DeleteConstant.NO)
                    .build();
            examinationInformationMapper.insertExaminationInformation(examinationInfo);

            // 校验用户和题目添加结果
            if (participateUserIds.size() != userIds.length) {
                throw new BaseException(MessageConstant.USER_PART_ADD_FAILED);
            }
            int inputQuestionTotal = (singleResult.getValidQuestionIds().isEmpty() ? 0 : addExaminationDTO.getSingleChoiceIds().split(",").length)
                    + (multiResult.getValidQuestionIds().isEmpty() ? 0 : addExaminationDTO.getMultipleChoiceIds().split(",").length)
                    + (judgeResult.getValidQuestionIds().isEmpty() ? 0 : addExaminationDTO.getJudgeIds().split(",").length)
                    + (fillResult.getValidQuestionIds().isEmpty() ? 0 : addExaminationDTO.getFillBlankIds().split(",").length);
            if (totalQuestionCount != inputQuestionTotal) {
                log.info("题目总数量：{}", inputQuestionTotal);
                throw new BaseException(MessageConstant.QUESTION_PART_ADD_FAILED);
            }
        } else {
            throw new BaseException(MessageConstant.USER_ADD_FAILED);
        }
    }

    /**
     * 删除考试信息
     * @param ids
     */
    public void deleteExaminationInformation(String[] ids) {
        List<String> canDeleteIds = new ArrayList<>();
        //判断资料是否存在，并判断当前时间是否在开始考试之前
        List<String> deleteIds = new ArrayList<>();
        for (String id : ids) {
            ExaminationInformation examinationInformation = examinationInformationMapper.selectById(id);
            if(examinationInformation == null ||
                    examinationInformation.getIsDelete().equals(DeleteConstant.YES)
                    || !examinationInformation.getType().equals(ExaminationConstant.EXAMINATION_INFORMATION)){
                continue;
            }
            //不是空值，判断考试是否在考试时间之前
            if(!examinationInformation.getStartTime().isAfter(LocalDateTime.now())){
                continue;
            }
            //将满足条件的存入canDeleteIds集合中
            canDeleteIds.add(id);
            //获取考试信息中的题目ID并放入deleteIds集合中
            if(examinationInformation.getSingleChoiceIds() != null){
                deleteIds.addAll(Arrays.asList(examinationInformation.getSingleChoiceIds().split(",")));
            }
            if(examinationInformation.getMultipleChoiceIds() != null){
                deleteIds.addAll(Arrays.asList(examinationInformation.getMultipleChoiceIds().split(",")));
            }
            if(examinationInformation.getJudgeIds() != null){
                deleteIds.addAll(Arrays.asList(examinationInformation.getJudgeIds().split(",")));
            }
            if(examinationInformation.getFillBlankIds() != null){
                deleteIds.addAll(Arrays.asList(examinationInformation.getFillBlankIds().split(",")));
            }
        }
        // 执行删除逻辑
        if(!canDeleteIds.isEmpty()) {
            examinationInformationMapper.removeBatchByIds(
                    canDeleteIds.toArray(new String[0]),ExaminationConstant.EXAMINATION_INFORMATION);
            //删除对应的考试信息的题目
            for(String deleteId : deleteIds){
                questionBankMapper.updateQuestionBankTimes(deleteId, TimesConstant.REDUCE);
            }
            //检查所有题目状态
            List<QuestionBank> questionBanks = questionBankMapper.selectList(
                    new QueryWrapper<QuestionBank>().in("id", deleteIds)
                            .le("times", 0)
                            .eq("is_choose", StatusConstant.ENABLE));
            if(!questionBanks.isEmpty()){
                //将id提取出来
                String[] questionIds = questionBanks.stream().map(QuestionBank::getId).toArray(String[]::new);
                //将题目状态改为不可选
                questionBankMapper.updateQuestionBankSelected(questionIds, StatusConstant.DISABLE);
            }
        }
        // 处理删除结果
        if(canDeleteIds.size() != ids.length) {
            if(canDeleteIds.isEmpty()) {
                throw new BaseException(MessageConstant.EXAMINATION_INFORMATION_FAIL_DELETED);
            } else {
                throw new BaseException(MessageConstant.EXAMINATION_INFORMATION_PART_DELETED);
            }
        }
    }

    /**
     * 编辑考试信息
     * @param examinationUpdateDTO
     */
    public void updateExaminationInformation(ExaminationUpdateDTO examinationUpdateDTO) {
        // 参数校验：ID不能为空
        if (examinationUpdateDTO.getId() == null || examinationUpdateDTO.getId().isEmpty()) {
            throw new BaseException(MessageConstant.EXAMINATION_INFORMATION_NOT_EXIST);
        }
        // 校验是否有可更新的参数
        if ((examinationUpdateDTO.getUserIds() == null || examinationUpdateDTO.getUserIds().isEmpty())
                && (examinationUpdateDTO.getExaminationName() == null || examinationUpdateDTO.getExaminationName().isEmpty())
                && (examinationUpdateDTO.getScoreValue() == null || examinationUpdateDTO.getScoreValue().isEmpty())
                && (examinationUpdateDTO.getExaminationInstruction() == null || examinationUpdateDTO.getExaminationInstruction().isEmpty())
                && examinationUpdateDTO.getStartTime() == null
                && examinationUpdateDTO.getEndTime() == null
                && (examinationUpdateDTO.getSingleChoiceIds() == null || examinationUpdateDTO.getSingleChoiceIds().isEmpty())
                && (examinationUpdateDTO.getMultipleChoiceIds() == null || examinationUpdateDTO.getMultipleChoiceIds().isEmpty())
                && (examinationUpdateDTO.getJudgeIds() == null || examinationUpdateDTO.getJudgeIds().isEmpty())
                && (examinationUpdateDTO.getFillBlankIds() == null || examinationUpdateDTO.getFillBlankIds().isEmpty())) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        // 查询考试信息是否存在
        ExaminationInformation examinationInformation = examinationInformationMapper.selectById(examinationUpdateDTO.getId());
        if (examinationInformation == null
                || examinationInformation.getIsDelete().equals(DeleteConstant.YES)
                || !examinationInformation.getType().equals(ExaminationConstant.EXAMINATION_INFORMATION)) {
            throw new BaseException(MessageConstant.EXAMINATION_INFORMATION_NOT_EXIST);
        }

        // 校验考试时间
        if (examinationUpdateDTO.getStartTime() != null && examinationUpdateDTO.getEndTime() != null) {
            if (examinationUpdateDTO.getStartTime().isAfter(examinationUpdateDTO.getEndTime())
                    || !examinationUpdateDTO.getStartTime().isAfter(LocalDateTime.now())) {
                throw new BaseException(MessageConstant.EXAMINATION_TIME_ERROR);
            }
        } else if (examinationUpdateDTO.getStartTime() != null) {
            if (examinationUpdateDTO.getStartTime().isAfter(examinationInformation.getEndTime())
                    || !examinationUpdateDTO.getStartTime().isAfter(LocalDateTime.now())) {
                throw new BaseException(MessageConstant.EXAMINATION_TIME_ERROR);
            }
        } else if (examinationUpdateDTO.getEndTime() != null) {
            if (examinationUpdateDTO.getEndTime().isBefore(examinationInformation.getStartTime())) {
                throw new BaseException(MessageConstant.EXAMINATION_TIME_ERROR);
            }
        }

        // 校验分值格式
        if (examinationUpdateDTO.getScoreValue() != null && !examinationUpdateDTO.getScoreValue().isEmpty()) {
            String[] scoreValues = examinationUpdateDTO.getScoreValue().split(",");
            if (scoreValues.length != 4) {
                throw new BaseException(MessageConstant.EXAMINATION_SCORE_ERROR_2);
            }
        }

        // 处理用户ID
        List<String> participateUserIds = new ArrayList<>();
        String[] userIds = null;
        if (examinationUpdateDTO.getUserIds() != null && !examinationUpdateDTO.getUserIds().isEmpty()) {
            userIds = examinationUpdateDTO.getUserIds().split(",");
            for (String userId : userIds) {
                User user = userMapper.selectById(userId);
                if (user != null && user.getIsDelete().equals(DeleteConstant.NO)
                        && user.getStatus().equals(StatusConstant.ENABLE)) {
                    participateUserIds.add(userId);
                }
            }
        }
        ExaminationInformation updateInfo =
                BeanUtil.copyProperties(examinationUpdateDTO, ExaminationInformation.class);
        //用集合存储被删除的题目
        List<String> deleteQuestionIds = new ArrayList<>();
        List<String> addQuestionIds = new ArrayList<>();
        //调用判断方法
        updateInfo.setSingleChoiceIds(checkQuestionChange(
                examinationUpdateDTO.getSingleChoiceIds(),
                examinationInformation.getSingleChoiceIds(), deleteQuestionIds, addQuestionIds));
        updateInfo.setMultipleChoiceIds(checkQuestionChange(
                examinationUpdateDTO.getMultipleChoiceIds(),
                examinationInformation.getMultipleChoiceIds(), deleteQuestionIds, addQuestionIds));
        updateInfo.setJudgeIds(checkQuestionChange(
                examinationUpdateDTO.getJudgeIds(),
                examinationInformation.getJudgeIds(), deleteQuestionIds, addQuestionIds));
        updateInfo.setFillBlankIds(checkQuestionChange(
                examinationUpdateDTO.getFillBlankIds(),
                examinationInformation.getFillBlankIds(), deleteQuestionIds, addQuestionIds));
        // 调用通用函数处理四类题目（核心重构点）
        QuestionProcessResult singleResult = processQuestionIds(
                updateInfo.getSingleChoiceIds(), QuestionOptionConstant.SINGLE_CHOICE);
        QuestionProcessResult multiResult = processQuestionIds(
                updateInfo.getMultipleChoiceIds(), QuestionOptionConstant.MULTI_CHOICE);
        QuestionProcessResult judgeResult = processQuestionIds(
                updateInfo.getJudgeIds(), QuestionOptionConstant.JUDGEMENT);
        QuestionProcessResult fillResult = processQuestionIds(
                updateInfo.getFillBlankIds(), QuestionOptionConstant.FILL_BLANKS);
        // 提取有效题目ID列表
        List<String> singleValidIds = singleResult.getValidQuestionIds();
        List<String> multiValidIds = multiResult.getValidQuestionIds();
        List<String> judgeValidIds = judgeResult.getValidQuestionIds();
        List<String> fillValidIds = fillResult.getValidQuestionIds();


        // 更新题目选中状态
        if (!addQuestionIds.isEmpty()) {
            questionBankMapper.updateQuestionBankSelected(
                    addQuestionIds.toArray(new String[0]), StatusConstant.ENABLE);
            //对新加入的题目增加次数并修改
            for (String addQuestionId : addQuestionIds) {
                questionBankMapper.updateQuestionBankTimes(addQuestionId, TimesConstant.ADD);
            }
        }
        //更新题目的状态
        if (!deleteQuestionIds.isEmpty()) {
            Iterator<String> iterator = deleteQuestionIds.iterator();
            while (iterator.hasNext()) {
                String deleteQuestionId = iterator.next();
                QuestionBank questionBank = questionBankMapper.selectById(deleteQuestionId);
                if (questionBank != null) {
                    questionBank.setTimes(questionBank.getTimes() - 1);
                    if (questionBank.getTimes() > 0) {
                        //修改题目的次数
                        questionBankMapper.updateQuestionBankTimes(
                                deleteQuestionId, TimesConstant.REDUCE);
                        // 移除
                        iterator.remove();
                    }
                }
            }
            //判断是否还有修改状态的题目
            if(!deleteQuestionIds.isEmpty()){
                questionBankMapper.updateQuestionBankSelected(
                        deleteQuestionIds.toArray(new String[0]), StatusConstant.DISABLE);
            }
        }

        // 转换ID列表为字符串
        String userIdsStr = String.join(",", participateUserIds);
        String singleIdsStr = String.join(",", singleValidIds);
        String multiIdsStr = String.join(",", multiValidIds);
        String judgeIdsStr = String.join(",", judgeValidIds);
        String fillIdsStr = String.join(",", fillValidIds);

        // 计算总题目数量（保持原有逻辑）
        Integer totalQuestionCount = singleValidIds.size() + multiValidIds.size() + judgeValidIds.size() + fillValidIds.size();
        log.info("总题目数量：{}", totalQuestionCount);

        // 计算修改后的题目总数（保持原有逻辑）
        Integer oldTotalCount = examinationInformation.getQuestionQuantity();
        Integer singleOldCount = examinationInformation.getSingleChoiceIds().split(",").length;
        Integer multiOldCount = examinationInformation.getMultipleChoiceIds().split(",").length;
        Integer judgeOldCount = examinationInformation.getJudgeIds().split(",").length;
        Integer fillOldCount = examinationInformation.getFillBlankIds().split(",").length;

        Integer changeCount = 0;
        if (isNeedUpdateWithValidElement(singleResult.getValidQuestionIds().toArray(new String[0]), examinationUpdateDTO.getSingleChoiceIds())) {
            oldTotalCount -= singleOldCount;
            changeCount += singleResult.getValidQuestionIds().size();
        }
        if (isNeedUpdateWithValidElement(multiResult.getValidQuestionIds().toArray(new String[0]), examinationUpdateDTO.getMultipleChoiceIds())) {
            oldTotalCount -= multiOldCount;
            changeCount += multiResult.getValidQuestionIds().size();
        }
        if (isNeedUpdateWithValidElement(judgeResult.getValidQuestionIds().toArray(new String[0]), examinationUpdateDTO.getJudgeIds())) {
            oldTotalCount -= judgeOldCount;
            changeCount += judgeResult.getValidQuestionIds().size();
        }
        if (isNeedUpdateWithValidElement(fillResult.getValidQuestionIds().toArray(new String[0]), examinationUpdateDTO.getFillBlankIds())) {
            oldTotalCount -= fillOldCount;
            changeCount += fillResult.getValidQuestionIds().size();
        }

        // 校验题目数量合法性
        log.info("修改后的题目数量：{}", oldTotalCount + totalQuestionCount);
        if (oldTotalCount + totalQuestionCount == 0) {
            throw new BaseException(MessageConstant.QUESTION_ADD_FAILED);
        }

        // 更新考试信息到数据库
        updateInfo.setQuestionQuantity(oldTotalCount + totalQuestionCount);
        examinationInformationMapper.updateExaminationInformation(updateInfo);

        // 校验用户和题目添加结果
        if (userIds != null && participateUserIds.size() != userIds.length) {
            throw new BaseException(MessageConstant.USER_PART_ADD_FAILED);
        }
        if (totalQuestionCount != changeCount) {
            log.info("题目修改数量：{}", changeCount);
            throw new BaseException(MessageConstant.QUESTION_PART_ADD_FAILED);
        }
    }


    // 抽取工具方法：判断是否需要更新（数组有有效元素 或 原始字段为NULL）
    private boolean isNeedUpdateWithValidElement(String[] array, String originalField) {
        // 1. 先判断数组是否有"有效元素"（数组非null + 长度>0 + 第一个元素非null且非空）
        boolean hasValidElements = false;
        if (array != null && array.length > 0) {
            // 确保第一个元素非null且不是空字符串（避免[""]这种无效数据）
            String firstElement = array[0];
            hasValidElements = firstElement != null && !firstElement.isEmpty();
        }
        // 2. 判断原始字段是否标记为NULL（常量放前面避免空指针）
        boolean isOriginalNull = FlagConstant.NULL.equals(originalField);
        // 满足任一条件则需要更新
        return hasValidElements || isOriginalNull;
    }


    /**
     * 通用查询题目列表并转换为对应VO的方法
     * @param ids 题目ID数组
     * @param questionBankMapper 题目Mapper
     * @param converter 转换函数（将QuestionBank转为目标VO）
     * @param <T> 目标VO类型（如ChoiceVO、WriteVO）
     * @return 转换后的VO列表
     */
    private <T> List<T> getQuestionList(String[] ids, QuestionBankMapper questionBankMapper, Function<QuestionBank, T> converter) {
        List<T> resultList = new ArrayList<>();
        // 校验ID数组有效性（非空、非空字符串）
        if (ids == null || ids.length == 0 || ids[0].isEmpty()) {
            return resultList;
        }
        // 批量查询题目
        List<QuestionBank> questionBanks = questionBankMapper.selectBatchIds(Arrays.asList(ids));
        // 转换为目标VO并收集结果
        for (QuestionBank questionBank : questionBanks) {
            resultList.add(converter.apply(questionBank));
        }
        return resultList;
    }

    /**
     * 查询考试信息
     * @param id
     * @return
     */
    public ExaminationVO queryExaminationInformation(String id) {
        // 判断考试信息是否存在
        ExaminationInformation examinationInformation = examinationInformationMapper.selectById(id);
        if (examinationInformation == null
                || examinationInformation.getIsDelete().equals(DeleteConstant.YES)
                || !examinationInformation.getType().equals(ExaminationConstant.EXAMINATION_INFORMATION)) {
            throw new BaseException(MessageConstant.EXAMINATION_INFORMATION_NOT_EXIST);
        }
        ExaminationVO examinationVO = BeanUtil.copyProperties(examinationInformation, ExaminationVO.class);

        // 将字符串转换成数组（用户ID）
        String[] userIds = examinationInformation.getUserIds().split(",");
        // 将用户的id转换成用户名字
        List<String> userNameList = userMapper.queryUserNames(userIds);
        examinationVO.setUserNames(String.join(",", userNameList));
        // 返回修改人姓名
        examinationVO.setUpdateName(userMapper.selectById(examinationInformation.getUpdateBy()).getUserName());

        // 1. 先得到各类题目的id集合
        String[] singleChoiceIds = examinationInformation.getSingleChoiceIds().split(",");
        String[] multipleChoiceIds = examinationInformation.getMultipleChoiceIds().split(",");
        String[] judgeIds = examinationInformation.getJudgeIds().split(",");
        String[] fillBlankIds = examinationInformation.getFillBlankIds().split(",");

        // 2. 定义转换函数（不同题目类型的VO转换逻辑）
        // 单选题/多选题转换为ChoiceVO的逻辑
        Function<QuestionBank, ChoiceVO> choiceVOConverter = questionBank -> {
            List<String> questionOptionList = new ArrayList<>();
            if (questionBank.getQuestionOption() != null && !questionBank.getQuestionOption().isEmpty()) {
                // 分割选项字符串为列表（简化原循环添加逻辑）
                questionOptionList.addAll(Arrays.asList(questionBank.getQuestionOption().split(",")));
            }
            return ChoiceVO.builder()
                    .id(questionBank.getId())
                    .question(questionBank.getQuestion())
                    .questionOption(questionOptionList)
                    .build();
        };

        // 判断题/填空题转换为WriteVO的逻辑
        Function<QuestionBank, WriteVO> writeVOConverter = questionBank ->
                WriteVO.builder()
                        .id(questionBank.getId())
                        .question(questionBank.getQuestion())
                        .build();

        // 3. 调用通用方法查询并转换各类题目
        List<ChoiceVO> singleChoiceList = getQuestionList(singleChoiceIds, questionBankMapper, choiceVOConverter);
        List<ChoiceVO> multipleChoiceList = getQuestionList(multipleChoiceIds, questionBankMapper, choiceVOConverter);
        List<WriteVO> judgeList = getQuestionList(judgeIds, questionBankMapper, writeVOConverter);
        List<WriteVO> fillBlankList = getQuestionList(fillBlankIds, questionBankMapper, writeVOConverter);

        // 4. 设置到返回VO中
        examinationVO.setSingleChoice(singleChoiceList);
        examinationVO.setMultipleChoice(multipleChoiceList);
        examinationVO.setJudge(judgeList);
        examinationVO.setFillBlank(fillBlankList);

        return examinationVO;
    }

    /**
     * 查询考试信息列表
     * @param examinationPageDTO
     * @return
     */
    public PageResult queryExaminationInformationList(ExaminationPageDTO examinationPageDTO) {
        //配置默认页码1和分页大小5
        if(examinationPageDTO.getPage() == null || examinationPageDTO.getPage() < 1){
            examinationPageDTO.setPage(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(examinationPageDTO.getPageSize() == null || examinationPageDTO.getPageSize() < 1){
            examinationPageDTO.setPageSize(PageConstant.PAGE_SIZE);
        }
        PageHelper.startPage(examinationPageDTO.getPage(), examinationPageDTO.getPageSize());
        List<ExaminationPageVO> examinationInformationList =
                examinationInformationMapper.selectExaminationInformationList(examinationPageDTO);
        PageInfo<ExaminationPageVO> pageInfo = new PageInfo<>(examinationInformationList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 新增考试试题
     * @param addExaminationPaperDTO
     */
    public void addExaminationPaper(AddExaminationPaperDTO addExaminationPaperDTO) {
        //判断是否有传入各类题型的分值
        if(addExaminationPaperDTO.getScoreValue() == null){
            throw new BaseException(MessageConstant.EXAMINATION_SCORE_ERROR);
        }
        //后续需要保证分值的形式为"?,?,?,?"
        String[] scoreValues = addExaminationPaperDTO.getScoreValue().split(",");
        //判断长度是否为4，如果不是4个，则返回错误信息
        if(scoreValues.length != 4){
            throw new BaseException(MessageConstant.EXAMINATION_SCORE_ERROR);
        }
        //进行业务判断
        // 调用通用函数处理四类题目（核心重构点）
        QuestionProcessResult singleResult = processQuestionIds
                (addExaminationPaperDTO.getSingleChoiceIds(), QuestionOptionConstant.SINGLE_CHOICE);
        QuestionProcessResult multiResult = processQuestionIds
                (addExaminationPaperDTO.getMultipleChoiceIds(), QuestionOptionConstant.MULTI_CHOICE);
        QuestionProcessResult judgeResult = processQuestionIds
                (addExaminationPaperDTO.getJudgeIds(), QuestionOptionConstant.JUDGEMENT);
        QuestionProcessResult fillResult = processQuestionIds
                (addExaminationPaperDTO.getFillBlankIds(), QuestionOptionConstant.FILL_BLANKS);
        // 提取有效题目ID列表
        List<String> singleValidIds = singleResult.getValidQuestionIds();
        List<String> multiValidIds = multiResult.getValidQuestionIds();
        List<String> judgeValidIds = judgeResult.getValidQuestionIds();
        List<String> fillValidIds = fillResult.getValidQuestionIds();
        // 合并需更新选中状态的题目ID
        List<String> toUpdateChoiceIds = new ArrayList<>();
        toUpdateChoiceIds.addAll(singleResult.getToUpdateSelectedIds());
        toUpdateChoiceIds.addAll(multiResult.getToUpdateSelectedIds());
        toUpdateChoiceIds.addAll(judgeResult.getToUpdateSelectedIds());
        toUpdateChoiceIds.addAll(fillResult.getToUpdateSelectedIds());
        // 更新题目次数
        //对新加入的题目增加次数并修改
        List<String> addQuestionIds = new ArrayList<>();
        addQuestionIds.addAll(singleValidIds);
        addQuestionIds.addAll(multiValidIds);
        addQuestionIds.addAll(judgeValidIds);
        addQuestionIds.addAll(fillValidIds);
        for (String addQuestionId : addQuestionIds) {
            questionBankMapper.updateQuestionBankTimes(addQuestionId, TimesConstant.ADD);
        }
        // 转换ID列表为字符串
        String singleIdsStr = String.join(",", singleValidIds);
        String multiIdsStr = String.join(",", multiValidIds);
        String judgeIdsStr = String.join(",", judgeValidIds);
        String fillIdsStr = String.join(",", fillValidIds);
        // 生成考试试题ID
        IdGenerate idGenerate = new IdGenerate();
        String examinationPaperId = idGenerate.nextUUID(ExaminationInformation.class);
        // 计算总题目数量
        Integer totalQuestionCount = singleValidIds.size() + multiValidIds.size() + judgeValidIds.size() + fillValidIds.size();
        if (totalQuestionCount == 0) {
            throw new BaseException(MessageConstant.QUESTION_ADD_FAILED);
        }
        // 构建考试试题并插入数据库
        ExaminationInformation examinationPaper = ExaminationInformation.builder()
                .id(examinationPaperId)
                .userIds(addExaminationPaperDTO.getUserId())
                .examinationName(addExaminationPaperDTO.getPaperName())
                .scoreValue(addExaminationPaperDTO.getScoreValue())
                .singleChoiceIds(singleIdsStr)
                .multipleChoiceIds(multiIdsStr)
                .judgeIds(judgeIdsStr)
                .fillBlankIds(fillIdsStr)
                .questionQuantity(totalQuestionCount)
                .type(ExaminationConstant.EXAMINATION_PAPER)
                .note(addExaminationPaperDTO.getNote())
                .isDelete(DeleteConstant.NO)
                .build();
        examinationInformationMapper.insertExaminationInformation(examinationPaper);

    }

    /**
     * 新增考试模版
     * @param addExaminationTemplateDTO
     */
    public void addExaminationTemplate(AddExaminationTemplateDTO addExaminationTemplateDTO) {
        ExaminationInformation examinationTemplate = new ExaminationInformation();
        //判断是否有id传入
        if(addExaminationTemplateDTO.getUserId() == null){
            examinationTemplate.setUserIds(addExaminationTemplateDTO.getUserId());
        }
        //判断是否有考试信息名称
        if(addExaminationTemplateDTO.getExaminationName() != null
                && !addExaminationTemplateDTO.getExaminationName().equals("")){
            examinationTemplate.setExaminationName(addExaminationTemplateDTO.getExaminationName());
        }
        //判断是否有考试信息备注
        if(addExaminationTemplateDTO.getNote() != null
                && !addExaminationTemplateDTO.getNote().equals("")){
            examinationTemplate.setNote(addExaminationTemplateDTO.getNote());
        }
        //判断是否有考试介绍
        if(addExaminationTemplateDTO.getExaminationInstruction() != null
                && !addExaminationTemplateDTO.getExaminationInstruction().equals("")){
            examinationTemplate.setExaminationInstruction(addExaminationTemplateDTO.getExaminationInstruction());
        }
        //判断是否有传入分值，并且判断分值的形式为"?,?,?,?"
        if(addExaminationTemplateDTO.getScoreValue() != null
                && !addExaminationTemplateDTO.getScoreValue().equals("")){
            examinationTemplate.setScoreValue(addExaminationTemplateDTO.getScoreValue());
            String[] scoreValues = addExaminationTemplateDTO.getScoreValue().split(",");
            if(scoreValues.length != 4){
                throw new BaseException(MessageConstant.PARAMETER_ERROR);
            }
            //判断是否是正数
            for (String scoreValue : scoreValues) {
                if(!NumberUtils.isCreatable(scoreValue)){
                    throw new BaseException(MessageConstant.PARAMETER_ERROR);
                }
            }
            examinationTemplate.setScoreValue(addExaminationTemplateDTO.getScoreValue());
        }
        //加入其他信息
        examinationTemplate.setIsDelete(DeleteConstant.NO);
        examinationTemplate.setType(ExaminationConstant.TEMPLATE);
        IdGenerate idGenerate = new IdGenerate();
        examinationTemplate.setId(idGenerate.nextUUID(ExaminationInformation.class));
        examinationInformationMapper.insertExaminationInformation(examinationTemplate);
    }

    /**
     * 查询考试模版列表
     * @param examinationTemplatePageDTO
     * @return
     */
    public PageResult listExaminationTemplate(ExaminationTemplatePageDTO examinationTemplatePageDTO) {
        //配置默认页码1和分页大小5
        if(examinationTemplatePageDTO.getPage() == null || examinationTemplatePageDTO.getPage() < 1){
            examinationTemplatePageDTO.setPage(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(examinationTemplatePageDTO.getPageSize() == null || examinationTemplatePageDTO.getPageSize() < 1){
            examinationTemplatePageDTO.setPageSize(PageConstant.PAGE_SIZE);
        }
        PageHelper.startPage(examinationTemplatePageDTO.getPage(), examinationTemplatePageDTO.getPageSize());
        List<ExaminationTemplatePageVO> examinationTemplateList =
                examinationInformationMapper.selectExaminationTemplateList(examinationTemplatePageDTO);
        PageInfo<ExaminationTemplatePageVO> pageInfo = new PageInfo<>(examinationTemplateList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 查询考试模版详情
     * @param id
     * @return
     */
    public ExaminationTemplateVO searchExaminationTemplate(String id) {
        //判断id是否存在
        if(id == null || id.equals("")){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        ExaminationTemplateVO examinationTemplateVO =
                examinationInformationMapper.selectExaminationTemplate(id);
        if(examinationTemplateVO == null){
            throw new BaseException(MessageConstant.EXAMINATION_TEMPLATE_NOT_EXIST);
        }
        //判断该模版是否是通用模版
        if(examinationTemplateVO.getUserId() == null){
            examinationTemplateVO.setUserName("通用模版");
        }
        return examinationTemplateVO;
    }

    /**
     * 编辑模版
     * @param updateExaminationTemplateDTO
     */
    public void updateExaminationTemplate(UpdateExaminationTemplateDTO updateExaminationTemplateDTO) {
        //判断id是否存在
        if(updateExaminationTemplateDTO.getId() == null || updateExaminationTemplateDTO.getId().equals("")){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        ExaminationInformation examinationTemplate =
                BeanUtil.copyProperties(updateExaminationTemplateDTO,ExaminationInformation.class);
        examinationTemplate.setUserIds(updateExaminationTemplateDTO.getUserId());
        examinationInformationMapper.updateExaminationInformation(examinationTemplate);
    }

    /**
     * 删除模版
     * @param ids
     */
    public void deleteExaminationTemplate(String[] ids) {
        //判断id是否存在
        if(ids == null || ids.length == 0){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //直接删除
        Integer flag = examinationInformationMapper.removeBatchByIds(ids,ExaminationConstant.TEMPLATE);
        if(flag == 0){
            throw new BaseException(MessageConstant.DELETE_EXAMINATION_TEMPLATE_FAIL);
        }
    }

    /**
     * 查询考试试题列表
     * @param examinationPaperPageDTO
     * @return
     */
    public PageResult queryExaminationPaperList(ExaminationPaperPageDTO examinationPaperPageDTO) {
        //配置默认页码1和分页大小5
        if(examinationPaperPageDTO.getPage() == null || examinationPaperPageDTO.getPage() < 1){
            examinationPaperPageDTO.setPage(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(examinationPaperPageDTO.getPageSize() == null || examinationPaperPageDTO.getPageSize() < 1){
            examinationPaperPageDTO.setPageSize(PageConstant.PAGE_SIZE);
        }
        PageHelper.startPage(examinationPaperPageDTO.getPage(), examinationPaperPageDTO.getPageSize());
        List<ExaminationPaperPageVO> examinationInformationList =
                examinationInformationMapper.selectExaminationPaperList(examinationPaperPageDTO);
        PageInfo<ExaminationPaperPageVO> pageInfo = new PageInfo<>(examinationInformationList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 查询考试试题详情
     * @param id
     * @return
     */
    public ExaminationPaperVO searchExaminationPaper(String id) {
        //判断id是否存在
        ExaminationInformation examinationPaper = examinationInformationMapper.selectById(id);
        if (examinationPaper == null
                || examinationPaper.getIsDelete().equals(DeleteConstant.YES)
                || !examinationPaper.getType().equals(ExaminationConstant.EXAMINATION_PAPER)) {
            throw new BaseException(MessageConstant.EXAMINATION_PAPER_NOT_EXIST);
        }
        ExaminationPaperVO examinationPaperVO =
                BeanUtil.copyProperties(examinationPaper, ExaminationPaperVO.class);
        //将其他数据放入examinationPaperVO中
        examinationPaperVO.setUserId(examinationPaper.getUserIds());
        examinationPaperVO.setPaperName(examinationPaper.getExaminationName());
        //判断该模版是否是通用模版
        if(examinationPaper.getUserIds().equals("-1")){
            examinationPaperVO.setUserName("共享试题");
        }else{
            examinationPaperVO.setUserName(userMapper.selectById(examinationPaper.getUserIds()).getUserName());
        }
        // 1. 先得到各类题目的id集合
        String[] singleChoiceIds = examinationPaper.getSingleChoiceIds().split(",");
        String[] multipleChoiceIds = examinationPaper.getMultipleChoiceIds().split(",");
        String[] judgeIds = examinationPaper.getJudgeIds().split(",");
        String[] fillBlankIds = examinationPaper.getFillBlankIds().split(",");

        // 2. 定义转换函数（不同题目类型的VO转换逻辑）
        // 单选题/多选题转换为ChoiceVO的逻辑
        Function<QuestionBank, ChoiceVO> choiceVOConverter = questionBank -> {
            List<String> questionOptionList = new ArrayList<>();
            if (questionBank.getQuestionOption() != null && !questionBank.getQuestionOption().isEmpty()) {
                // 分割选项字符串为列表（简化原循环添加逻辑）
                questionOptionList.addAll(Arrays.asList(questionBank.getQuestionOption().split(",")));
            }
            return ChoiceVO.builder()
                    .id(questionBank.getId())
                    .question(questionBank.getQuestion())
                    .questionOption(questionOptionList)
                    .answer(questionBank.getAnswerType())
                    .note(questionBank.getNote())
                    .build();
        };

        // 判断题/填空题转换为WriteVO的逻辑
        Function<QuestionBank, WriteVO> writeVOConverter = questionBank ->
                WriteVO.builder()
                        .id(questionBank.getId())
                        .question(questionBank.getQuestion())
                        .answer(questionBank.getAnswerType())
                        .note(questionBank.getNote())
                        .build();
        // 3. 调用通用方法查询并转换各类题目
        List<ChoiceVO> singleChoiceList = getQuestionList(singleChoiceIds, questionBankMapper, choiceVOConverter);
        List<ChoiceVO> multipleChoiceList = getQuestionList(multipleChoiceIds, questionBankMapper, choiceVOConverter);
        List<WriteVO> judgeList = getQuestionList(judgeIds, questionBankMapper, writeVOConverter);
        List<WriteVO> fillBlankList = getQuestionList(fillBlankIds, questionBankMapper, writeVOConverter);

        // 4. 设置到返回VO中
        examinationPaperVO.setSingleChoice(singleChoiceList);
        examinationPaperVO.setMultipleChoice(multipleChoiceList);
        examinationPaperVO.setJudge(judgeList);
        examinationPaperVO.setFillBlank(fillBlankList);

        return examinationPaperVO;
    }

    /**
     * 删除试题
     * @param ids
     */
    public void deleteExaminationPaper(String[] ids) {
        //判断id是否存在
        if(ids == null || ids.length == 0){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //直接删除
        List<String> deleteIds = new ArrayList<>();
        List<String> canDeleteIds = new ArrayList<>();
        //获取考试信息中的题目ID并放入deleteIds集合中
        for(String id : ids){
            ExaminationInformation examinationPaper = examinationInformationMapper.selectById(id);
            if(examinationPaper == null || examinationPaper.getIsDelete().equals(DeleteConstant.YES)
                    || !examinationPaper.getType().equals(ExaminationConstant.EXAMINATION_PAPER)){
                continue;
            }
            canDeleteIds.add(id);
            //将题目ID放入deleteIds集合中
            if(examinationPaper.getSingleChoiceIds() != null){
                deleteIds.addAll(Arrays.asList(examinationPaper.getSingleChoiceIds().split(",")));
            }
            if(examinationPaper.getMultipleChoiceIds() != null){
                deleteIds.addAll(Arrays.asList(examinationPaper.getMultipleChoiceIds().split(",")));
            }
            if(examinationPaper.getJudgeIds() != null){
                deleteIds.addAll(Arrays.asList(examinationPaper.getJudgeIds().split(",")));
            }
            if(examinationPaper.getFillBlankIds() != null){
                deleteIds.addAll(Arrays.asList(examinationPaper.getFillBlankIds().split(",")));
            }
        }
        // 执行删除逻辑
        if(!canDeleteIds.isEmpty()) {
            Integer flag = examinationInformationMapper.removeBatchByIds(
                    canDeleteIds.toArray(new String[0]), ExaminationConstant.EXAMINATION_PAPER);
            if(flag == 0){
                throw new BaseException(MessageConstant.DELETE_EXAMINATION_PAPER_FAIL);
            }
            //删除对应的考试信息的题目
            for(String deleteId : deleteIds){
                questionBankMapper.updateQuestionBankTimes(deleteId, TimesConstant.REDUCE);
            }
            //检查所有题目状态
            List<QuestionBank> questionBanks = questionBankMapper.selectList(
                    new QueryWrapper<QuestionBank>().in("id", deleteIds)
                            .le("times", 0)
                            .eq("is_choose", StatusConstant.ENABLE));
            if(!questionBanks.isEmpty()){
                //将id提取出来
                String[] questionIds = questionBanks.stream().map(QuestionBank::getId).toArray(String[]::new);
                //将题目状态改为不可选
                questionBankMapper.updateQuestionBankSelected(questionIds, StatusConstant.DISABLE);
            }
        }
        // 处理删除结果
        if(canDeleteIds.size() != ids.length) {
            if(canDeleteIds.isEmpty()) {
                throw new BaseException(MessageConstant.EXAMINATION_PAPER_FAIL_DELETED);
            } else {
                throw new BaseException(MessageConstant.EXAMINATION_PAPER_PART_DELETED);
            }
        }

    }

    /**
     * 通用方法：判断题型ID是否变更，计算删除和新增的题目ID，并返回是否需要更新
     * @param newIdsStr DTO中的新题型ID字符串
     * @param oldIdsStr 原实体中的旧题型ID字符串
     * @param deleteQuestionIds 用于收集被删除题目的集合（旧有新无）
     * @param addQuestionIds 用于收集新加入题目的集合（新有旧无）
     * @return 新题型ID字符串（需要更新时返回；无需更新时返回null）
     */
    private String checkQuestionChange(String newIdsStr, String oldIdsStr,
                                       List<String> deleteQuestionIds, List<String> addQuestionIds) {
        // 新ID为空时无需处理
        if (newIdsStr == null || newIdsStr.trim().isEmpty()) {
            return null;
        }
        // 处理旧ID字符串：拆分并过滤空值
        List<String> oldIds;
        if (oldIdsStr == null || oldIdsStr.trim().isEmpty()) {
            oldIds = Collections.emptyList();
        } else {
            oldIds = Arrays.stream(oldIdsStr.split(","))
                    .map(String::trim)
                    .filter(id -> !id.isEmpty())
                    .collect(Collectors.toList());
        }
        // 处理新ID字符串：拆分并过滤空值
        List<String> newIds = Arrays.stream(newIdsStr.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .collect(Collectors.toList());

        // 新旧ID列表完全相同时无需处理
        if (oldIds.equals(newIds)) {
            return null;
        }
        // 1. 计算被删除的ID（旧有新无）
        for (String oldId : oldIds) {
            if (!newIds.contains(oldId)) {
                deleteQuestionIds.add(oldId);
            }
        }
        // 2. 计算新加入的ID（新有旧无）
        for (String newId : newIds) {
            if (!oldIds.contains(newId)) {
                addQuestionIds.add(newId);
            }
        }
        // 返回新ID字符串，表示需要更新实体
        return newIdsStr;
    }

    /**
     * 编辑试题
     * @param updateExaminationPaperDTO
     */
    public void updateExaminationPaper(UpdateExaminationPaperDTO updateExaminationPaperDTO) {
        //判断id是否存在
        if(updateExaminationPaperDTO.getId() == null || updateExaminationPaperDTO.getId().isEmpty()){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //判断试卷是否存在
        ExaminationInformation examinationPaper = examinationInformationMapper.selectById(updateExaminationPaperDTO.getId());
        if(examinationPaper == null || examinationPaper.getIsDelete().equals(DeleteConstant.YES)
                || !examinationPaper.getType().equals(ExaminationConstant.EXAMINATION_PAPER)){
            throw new BaseException(MessageConstant.EXAMINATION_PAPER_NOT_EXIST);
        }
        //新建一个ExaminationInformation对象
        ExaminationInformation newExaminationIPaper = ExaminationInformation.builder()
                .id(updateExaminationPaperDTO.getId())
                .build();
        //判断是否有变化
        if(updateExaminationPaperDTO.getPaperName() != null && !updateExaminationPaperDTO.getPaperName().isEmpty()
                && !updateExaminationPaperDTO.getPaperName().equals(examinationPaper.getExaminationName())){
            newExaminationIPaper.setExaminationName(updateExaminationPaperDTO.getPaperName());
        }
        if(updateExaminationPaperDTO.getNote() != null && !updateExaminationPaperDTO.getNote().isEmpty()
                && !updateExaminationPaperDTO.getNote().equals(examinationPaper.getNote())){
            newExaminationIPaper.setNote(updateExaminationPaperDTO.getNote());
        }
        if(updateExaminationPaperDTO.getScoreValue() != null && !updateExaminationPaperDTO.getScoreValue().isEmpty()
                && !updateExaminationPaperDTO.getScoreValue().equals(examinationPaper.getScoreValue())){
            newExaminationIPaper.setScoreValue(updateExaminationPaperDTO.getScoreValue());
        }
        if(updateExaminationPaperDTO.getUserId() != null && !updateExaminationPaperDTO.getUserId().isEmpty()
                && !updateExaminationPaperDTO.getUserId().equals(examinationPaper.getUserIds())){
            newExaminationIPaper.setUserIds(updateExaminationPaperDTO.getUserId());
        }
        //用集合存储被删除的题目
        List<String> deleteQuestionIds = new ArrayList<>();
        List<String> addQuestionIds = new ArrayList<>();
        //调用判断方法
        newExaminationIPaper.setSingleChoiceIds(checkQuestionChange(
                updateExaminationPaperDTO.getSingleChoiceIds(),
                examinationPaper.getSingleChoiceIds(), deleteQuestionIds, addQuestionIds));
        newExaminationIPaper.setMultipleChoiceIds(checkQuestionChange(
                updateExaminationPaperDTO.getMultipleChoiceIds(),
                examinationPaper.getMultipleChoiceIds(), deleteQuestionIds, addQuestionIds));
        newExaminationIPaper.setJudgeIds(checkQuestionChange(
                updateExaminationPaperDTO.getJudgeIds(),
                examinationPaper.getJudgeIds(), deleteQuestionIds, addQuestionIds));
        newExaminationIPaper.setFillBlankIds(checkQuestionChange(
                updateExaminationPaperDTO.getFillBlankIds(),
                examinationPaper.getFillBlankIds(), deleteQuestionIds, addQuestionIds));
        // 调用通用函数处理四类题目（核心重构点）
        QuestionProcessResult singleResult = processQuestionIds(
                newExaminationIPaper.getSingleChoiceIds(), QuestionOptionConstant.SINGLE_CHOICE);
        QuestionProcessResult multiResult = processQuestionIds(
                newExaminationIPaper.getMultipleChoiceIds(), QuestionOptionConstant.MULTI_CHOICE);
        QuestionProcessResult judgeResult = processQuestionIds(
                newExaminationIPaper.getJudgeIds(), QuestionOptionConstant.JUDGEMENT);
        QuestionProcessResult fillResult = processQuestionIds(
                newExaminationIPaper.getFillBlankIds(), QuestionOptionConstant.FILL_BLANKS);
        // 提取有效题目ID列表
        List<String> singleValidIds = singleResult.getValidQuestionIds();
        List<String> multiValidIds = multiResult.getValidQuestionIds();
        List<String> judgeValidIds = judgeResult.getValidQuestionIds();
        List<String> fillValidIds = fillResult.getValidQuestionIds();


        // 更新题目选中状态
        if (!addQuestionIds.isEmpty()) {
            questionBankMapper.updateQuestionBankSelected(
                    addQuestionIds.toArray(new String[0]), StatusConstant.ENABLE);
            //对新加入的题目增加次数并修改
            for (String addQuestionId : addQuestionIds) {
                questionBankMapper.updateQuestionBankTimes(addQuestionId, TimesConstant.ADD);
            }
        }
        //更新题目的状态
        if (!deleteQuestionIds.isEmpty()) {
            Iterator<String> iterator = deleteQuestionIds.iterator();
            while (iterator.hasNext()) {
                String deleteQuestionId = iterator.next();
                QuestionBank questionBank = questionBankMapper.selectById(deleteQuestionId);
                if (questionBank != null) {
                    questionBank.setTimes(questionBank.getTimes() - 1);
                    if (questionBank.getTimes() > 0) {
                        //修改题目的次数
                        questionBankMapper.updateQuestionBankTimes(
                                deleteQuestionId, TimesConstant.REDUCE);
                        // 移除
                        iterator.remove();
                    }
                }
            }
            //判断是否还有修改状态的题目
            if(!deleteQuestionIds.isEmpty()){
                questionBankMapper.updateQuestionBankSelected(
                        deleteQuestionIds.toArray(new String[0]), StatusConstant.DISABLE);
            }
        }
        // 转换ID列表为字符串
        String singleIdsStr = String.join(",", singleValidIds);
        String multiIdsStr = String.join(",", multiValidIds);
        String judgeIdsStr = String.join(",", judgeValidIds);
        String fillIdsStr = String.join(",", fillValidIds);
        // 计算总题目数量（保持原有逻辑）
        Integer totalQuestionCount = singleValidIds.size() + multiValidIds.size() + judgeValidIds.size() + fillValidIds.size();
        log.info("总题目数量：{}", totalQuestionCount);
        // 计算修改后的题目总数（保持原有逻辑）
        Integer oldTotalCount = examinationPaper.getQuestionQuantity();
        Integer singleOldCount = examinationPaper.getSingleChoiceIds().split(",").length;
        Integer multiOldCount = examinationPaper.getMultipleChoiceIds().split(",").length;
        Integer judgeOldCount = examinationPaper.getJudgeIds().split(",").length;
        Integer fillOldCount = examinationPaper.getFillBlankIds().split(",").length;

        Integer changeCount = 0;
        if (isNeedUpdateWithValidElement(singleResult.getValidQuestionIds().toArray(new String[0]), newExaminationIPaper.getSingleChoiceIds())) {
            oldTotalCount -= singleOldCount;
            changeCount += singleResult.getValidQuestionIds().size();
        }
        if (isNeedUpdateWithValidElement(multiResult.getValidQuestionIds().toArray(new String[0]), newExaminationIPaper.getMultipleChoiceIds())) {
            oldTotalCount -= multiOldCount;
            changeCount += multiResult.getValidQuestionIds().size();
        }
        if (isNeedUpdateWithValidElement(judgeResult.getValidQuestionIds().toArray(new String[0]), newExaminationIPaper.getJudgeIds())) {
            oldTotalCount -= judgeOldCount;
            changeCount += judgeResult.getValidQuestionIds().size();
        }
        if (isNeedUpdateWithValidElement(fillResult.getValidQuestionIds().toArray(new String[0]), newExaminationIPaper.getFillBlankIds())) {
            oldTotalCount -= fillOldCount;
            changeCount += fillResult.getValidQuestionIds().size();
        }

        // 校验题目数量合法性
        log.info("修改后的题目数量：{}", oldTotalCount + totalQuestionCount);
        if (oldTotalCount + totalQuestionCount == 0) {
            throw new BaseException(MessageConstant.QUESTION_ADD_FAILED);
        }
        // 更新考试试题到数据库
        newExaminationIPaper.setQuestionQuantity(oldTotalCount + totalQuestionCount);
        examinationInformationMapper.updateExaminationInformation(newExaminationIPaper);

    }


}
