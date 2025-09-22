package com.itmang.service.study.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.*;
import com.itmang.exception.BaseException;
import com.itmang.mapper.study.ExaminationInformationMapper;
import com.itmang.mapper.study.QuestionBankMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.AddExaminationDTO;
import com.itmang.pojo.dto.ExaminationPageDTO;
import com.itmang.pojo.dto.ExaminationUpdateDTO;
import com.itmang.pojo.entity.*;
import com.itmang.pojo.vo.ChoiceVO;
import com.itmang.pojo.vo.ExaminationPageVO;
import com.itmang.pojo.vo.ExaminationVO;
import com.itmang.pojo.vo.WriteVO;
import com.itmang.service.study.ExaminationService;
import com.itmang.utils.IdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
        if(!participateUserIds.isEmpty()){
            //分别将题目的id集合进行分割成数组，要进行传参判断
            String[] singleChoiceIds =new String[0];
            String[] multipleChoiceIds =new String[0];
            String[] judgeIds =new String[0];
            String[] fillBlankIds =new String[0];
            if(addExaminationDTO.getSingleChoiceIds() != null && !addExaminationDTO.getSingleChoiceIds().isEmpty()){
                singleChoiceIds = addExaminationDTO.getSingleChoiceIds().split(",");
            }
            if(addExaminationDTO.getMultipleChoiceIds() != null && !addExaminationDTO.getMultipleChoiceIds().isEmpty()){
                multipleChoiceIds = addExaminationDTO.getMultipleChoiceIds().split(",");
            }
           if(addExaminationDTO.getJudgeIds() != null && !addExaminationDTO.getJudgeIds().isEmpty()){
               judgeIds = addExaminationDTO.getJudgeIds().split(",");
           }
            if(addExaminationDTO.getFillBlankIds() != null && !addExaminationDTO.getFillBlankIds().isEmpty()){
                fillBlankIds = addExaminationDTO.getFillBlankIds().split(",");
            }
            List<String> participateUpdateChoiceIds = new ArrayList<>();
            List<String> participateSingleChoiceIds = new ArrayList<>();
            List<String> participateMultipleChoiceIds = new ArrayList<>();
            List<String> participateJudgeIds = new ArrayList<>();
            List<String> participateFillBlankIds = new ArrayList<>();
            //分别创建集合进行存储不同的题目
            if(singleChoiceIds.length > 0 && singleChoiceIds[0] != null && !singleChoiceIds[0].isEmpty()) {
                for (String singleChoiceId : singleChoiceIds) {
                    //判断题目是否存在
                    QuestionBank questionBank1 = questionBankMapper.selectById(singleChoiceId);
                    if ( questionBank1 != null && questionBank1.getIsDelete().equals(DeleteConstant.NO)
                            && questionBank1.getType().equals(QuestionOptionConstant.SINGLE_CHOICE) ) {
                        participateSingleChoiceIds.add(singleChoiceId);
                        if(questionBank1.getIsChoose().equals(StatusConstant.DISABLE)){
                            participateUpdateChoiceIds.add(singleChoiceId);
                        }
                    }
                }
            }
            if(multipleChoiceIds.length > 0 && (multipleChoiceIds[0] != null || !multipleChoiceIds[0].isEmpty())) {
                for (String multipleChoiceId : multipleChoiceIds) {
                    //判断题目是否存在
                    QuestionBank questionBank2 = questionBankMapper.selectById(multipleChoiceId);
                    if ( questionBank2 != null && questionBank2.getIsDelete().equals(DeleteConstant.NO)
                            && questionBank2.getType().equals(QuestionOptionConstant.MULTI_CHOICE)) {
                        participateMultipleChoiceIds.add(multipleChoiceId);
                        if(questionBank2.getIsChoose().equals(StatusConstant.DISABLE)){
                            participateUpdateChoiceIds.add(multipleChoiceId);
                        }
                    }
                }
            }
            if(judgeIds.length > 0 && (judgeIds[0] != null || !judgeIds[0].isEmpty())) {
                for (String judgeId : judgeIds) {
                    //判断题目是否存在
                    QuestionBank questionBank3 = questionBankMapper.selectById(judgeId);
                    if ( questionBank3 != null && questionBank3.getIsDelete().equals(DeleteConstant.NO)
                            && questionBank3.getType().equals(QuestionOptionConstant.JUDGEMENT)) {
                        participateJudgeIds.add(judgeId);
                        if(questionBank3.getIsChoose().equals(StatusConstant.DISABLE)){
                            participateUpdateChoiceIds.add(judgeId);
                        }
                    }
                }
            }
            if(fillBlankIds.length > 0 && (fillBlankIds[0] != null || !fillBlankIds[0].isEmpty())) {
                for (String fillBlankId : fillBlankIds) {
                    //判断题目是否存在
                    QuestionBank questionBank4 = questionBankMapper.selectById(fillBlankId);
                    if ( questionBank4 != null && questionBank4.getIsDelete().equals(DeleteConstant.NO)
                            && questionBank4.getType().equals(QuestionOptionConstant.FILL_BLANKS)) {
                        participateFillBlankIds.add(fillBlankId);
                        if(questionBank4.getIsChoose().equals(StatusConstant.DISABLE)){
                            participateUpdateChoiceIds.add(fillBlankId);
                        }
                    }
                }
            }
            //将数据封装到实体类中
            //将所有题目的id集合转换成数组，然后修改题目的被选中状态（一个数组中）
            //修改题目的被选中状态
            if(!participateUpdateChoiceIds.isEmpty()){
                questionBankMapper.updateQuestionBankSelected(participateUpdateChoiceIds.toArray(new String[0]));
            }
            //先将存在的数据id集合转换成字符串
            String participateUserIdsStr = String.join(",", participateUserIds);
            String participateSingleChoiceIdsStr = String.join(",", participateSingleChoiceIds);
            String participateMultipleChoiceIdsStr = String.join(",", participateMultipleChoiceIds);
            String participateJudgeIdsStr = String.join(",", participateJudgeIds);
            String participateFillBlankIdsStr = String.join(",", participateFillBlankIds);
            //生成考试信息id
            IdGenerate idGenerate = new IdGenerate();
            String examinationInformationId = idGenerate.nextUUID(ExaminationInformation.class);
            //计算题目的总数量
            Integer totalQuestionCount = participateSingleChoiceIds.size()
                    + participateMultipleChoiceIds.size() + participateJudgeIds.size()
                    + participateFillBlankIds.size();
            if(totalQuestionCount == 0){
                throw new BaseException(MessageConstant.QUESTION_ADD_FAILED);
            }
            ExaminationInformation examinationInformation = ExaminationInformation.builder()
                    .id(examinationInformationId)
                    .examinationName(addExaminationDTO.getExaminationName())
                    .userIds(participateUserIdsStr)
                    .scoreValue(addExaminationDTO.getScoreValue())
                    .singleChoiceIds(participateSingleChoiceIdsStr)
                    .multipleChoiceIds(participateMultipleChoiceIdsStr)
                    .judgeIds(participateJudgeIdsStr)
                    .fillBlankIds(participateFillBlankIdsStr)
                    .questionQuantity(totalQuestionCount)
                    .startTime(addExaminationDTO.getStartTime())
                    .endTime(addExaminationDTO.getEndTime())
                    .isDelete(DeleteConstant.NO)
                    .build();
            //将数据插入到数据库中
            examinationInformationMapper.insertExaminationInformation(examinationInformation);
            if(participateUserIds.size() != userIds.length){
                throw new BaseException(MessageConstant.USER_PART_ADD_FAILED);
            }
            if(totalQuestionCount != singleChoiceIds.length+multipleChoiceIds.length
                    +judgeIds.length+fillBlankIds.length){
                log.info("题目总数量：{}",singleChoiceIds.length+multipleChoiceIds.length
                        +judgeIds.length+fillBlankIds.length);
                throw new BaseException(MessageConstant.QUESTION_PART_ADD_FAILED);
            }
        }else{
            //用户添加失败
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
        for (String id : ids) {
            ExaminationInformation examinationInformation = examinationInformationMapper.selectById(id);
            if(examinationInformation == null ||
                    examinationInformation.getIsDelete().equals(DeleteConstant.YES)){
                continue;
            }
            //不是空值，判断考试是否在考试时间之前
            if(!examinationInformation.getStartTime().isAfter(LocalDateTime.now())){
                continue;
            }
            //将满足条件的存入canDeleteIds集合中
            canDeleteIds.add(id);
        }
        // 执行删除逻辑
        if(!canDeleteIds.isEmpty()) {
            examinationInformationMapper.removeBatchByIds(canDeleteIds.toArray(new String[0]));
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
        // 对传入的参数进行校验，数据不修改是null或者""的时候就不修改那部分
        if (examinationUpdateDTO.getId() == null || examinationUpdateDTO.getId().isEmpty()) {
            throw new BaseException(MessageConstant.EXAMINATION_INFORMATION_NOT_EXIST);
        }else{
            //再判断有没有其他参数传入
            if((examinationUpdateDTO.getUserIds() == null || examinationUpdateDTO.getUserIds().isEmpty())
                    && (examinationUpdateDTO.getExaminationName() == null || examinationUpdateDTO.getExaminationName().isEmpty())
                    && (examinationUpdateDTO.getScoreValue() == null || examinationUpdateDTO.getScoreValue().isEmpty())
                    && (examinationUpdateDTO.getExaminationInstruction() == null || examinationUpdateDTO.getExaminationInstruction().isEmpty())
                    && examinationUpdateDTO.getStartTime() == null && examinationUpdateDTO.getEndTime() == null
                    && (examinationUpdateDTO.getSingleChoiceIds() == null || examinationUpdateDTO.getSingleChoiceIds().isEmpty())
                    && (examinationUpdateDTO.getMultipleChoiceIds() == null || examinationUpdateDTO.getMultipleChoiceIds().isEmpty())
                    && (examinationUpdateDTO.getJudgeIds() == null || examinationUpdateDTO.getJudgeIds().isEmpty())
                    && (examinationUpdateDTO.getFillBlankIds() == null || examinationUpdateDTO.getFillBlankIds().isEmpty())) {
                throw new BaseException(MessageConstant.PARAMETER_ERROR);
            }
        }
        // 判断是否有传入考试时间的范围，并且结束时间要大于开始时间，而且开始时间要大于等于现在的系统时间
        //查找考试信息
        ExaminationInformation examinationInformation = examinationInformationMapper.selectById(examinationUpdateDTO.getId());
        if(examinationInformation == null){
            throw new BaseException(MessageConstant.EXAMINATION_INFORMATION_NOT_EXIST);
        }
        if(examinationUpdateDTO.getStartTime() != null && examinationUpdateDTO.getEndTime() != null){
            if(examinationUpdateDTO.getStartTime().isAfter(examinationUpdateDTO.getEndTime())
                || !examinationUpdateDTO.getStartTime().isAfter(LocalDateTime.now())){
                throw new BaseException(MessageConstant.EXAMINATION_TIME_ERROR);
            }
        } else if (examinationUpdateDTO.getStartTime() != null && examinationUpdateDTO.getEndTime() == null) {
            if(examinationUpdateDTO.getStartTime().isAfter(examinationInformation.getEndTime())
                || !examinationUpdateDTO.getStartTime().isAfter(LocalDateTime.now())){
                throw new BaseException(MessageConstant.EXAMINATION_TIME_ERROR);
            }
        }else if(examinationUpdateDTO.getStartTime() == null && examinationUpdateDTO.getEndTime() != null){
            if(examinationUpdateDTO.getEndTime().isBefore(examinationInformation.getStartTime())){
                throw new BaseException(MessageConstant.EXAMINATION_TIME_ERROR);
            }
        }
        //判断是否有传入各类题型的分值
        if(examinationUpdateDTO.getScoreValue() != null && !examinationUpdateDTO.getScoreValue().isEmpty()){
            String[] scoreValues = examinationUpdateDTO.getScoreValue().split(",");
            //判断长度是否为4，如果不是4个，则返回错误信息
            if(scoreValues.length != 4){
                throw new BaseException(MessageConstant.EXAMINATION_SCORE_ERROR_2);
            }
        }
        List<String> participateUserIds = new ArrayList<>();
        String[] userIds = null;
        if(examinationUpdateDTO.getUserIds() != null && !examinationUpdateDTO.getUserIds().isEmpty()){
            //TODO 后续可优化代码，减少查询次数
            //先查询用户都是否存在
            //用户的id集合是用逗号分隔的字符串，先将数据进行分割
             userIds = examinationUpdateDTO.getUserIds().split(",");
            for (String userId : userIds) {
                //判断用户是否存在
                User user = userMapper.selectById(userId);
                if ( user != null && user.getIsDelete().equals(DeleteConstant.NO)
                        && user.getStatus().equals(StatusConstant.ENABLE) ) {
                    //加入集合
                    participateUserIds.add(userId);
                }
            }
        }
        //进行业务判断
        //分别将题目的id集合进行分割成数组，要进行传参判断
        String[] singleChoiceIds =new String[0];
        String[] multipleChoiceIds =new String[0];
        String[] judgeIds =new String[0];
        String[] fillBlankIds =new String[0];
        if(examinationUpdateDTO.getSingleChoiceIds() != null && !examinationUpdateDTO.getSingleChoiceIds().isEmpty()
            || !examinationUpdateDTO.getSingleChoiceIds().equals(FlagConstant.NULL)){
            singleChoiceIds = examinationUpdateDTO.getSingleChoiceIds().split(",");
        }
        if(examinationUpdateDTO.getMultipleChoiceIds() != null && !examinationUpdateDTO.getMultipleChoiceIds().isEmpty()
            || !examinationUpdateDTO.getMultipleChoiceIds().equals(FlagConstant.NULL)){
            multipleChoiceIds = examinationUpdateDTO.getMultipleChoiceIds().split(",");
        }
        if(examinationUpdateDTO.getJudgeIds() != null && !examinationUpdateDTO.getJudgeIds().isEmpty()
            || !examinationUpdateDTO.getJudgeIds().equals(FlagConstant.NULL)){
            judgeIds = examinationUpdateDTO.getJudgeIds().split(",");
        }
        if(examinationUpdateDTO.getFillBlankIds() != null && !examinationUpdateDTO.getFillBlankIds().isEmpty()
            || !examinationUpdateDTO.getFillBlankIds().equals(FlagConstant.NULL)){
            fillBlankIds = examinationUpdateDTO.getFillBlankIds().split(",");
        }
        List<String> participateUpdateChoiceIds = new ArrayList<>();
        List<String> participateSingleChoiceIds = new ArrayList<>();
        List<String> participateMultipleChoiceIds = new ArrayList<>();
        List<String> participateJudgeIds = new ArrayList<>();
        List<String> participateFillBlankIds = new ArrayList<>();
        //分别创建集合进行存储不同的题目
        if(singleChoiceIds.length > 0 && singleChoiceIds[0] != null && !singleChoiceIds[0].isEmpty()) {
            for (String singleChoiceId : singleChoiceIds) {
                //判断题目是否存在
                QuestionBank questionBank1 = questionBankMapper.selectById(singleChoiceId);
                if ( questionBank1 != null && questionBank1.getIsDelete().equals(DeleteConstant.NO)
                        && questionBank1.getType().equals(QuestionOptionConstant.SINGLE_CHOICE) ) {
                    participateSingleChoiceIds.add(singleChoiceId);
                    if(questionBank1.getIsChoose().equals(StatusConstant.DISABLE)){
                        participateUpdateChoiceIds.add(singleChoiceId);
                    }
                }
            }
        }
        if(multipleChoiceIds.length > 0 && (multipleChoiceIds[0] != null || !multipleChoiceIds[0].isEmpty())) {
            for (String multipleChoiceId : multipleChoiceIds) {
                //判断题目是否存在
                QuestionBank questionBank2 = questionBankMapper.selectById(multipleChoiceId);
                if ( questionBank2 != null && questionBank2.getIsDelete().equals(DeleteConstant.NO)
                        && questionBank2.getType().equals(QuestionOptionConstant.MULTI_CHOICE)) {
                    participateMultipleChoiceIds.add(multipleChoiceId);
                    if(questionBank2.getIsChoose().equals(StatusConstant.DISABLE)){
                        participateUpdateChoiceIds.add(multipleChoiceId);
                    }
                }
            }
        }
        if(judgeIds.length > 0 && (judgeIds[0] != null || !judgeIds[0].isEmpty())) {
            for (String judgeId : judgeIds) {
                //判断题目是否存在
                QuestionBank questionBank3 = questionBankMapper.selectById(judgeId);
                if ( questionBank3 != null && questionBank3.getIsDelete().equals(DeleteConstant.NO)
                        && questionBank3.getType().equals(QuestionOptionConstant.JUDGEMENT)) {
                    participateJudgeIds.add(judgeId);
                    if(questionBank3.getIsChoose().equals(StatusConstant.DISABLE)){
                        participateUpdateChoiceIds.add(judgeId);
                    }
                }
            }
        }
        if(fillBlankIds.length > 0 && (fillBlankIds[0] != null || !fillBlankIds[0].isEmpty())) {
            for (String fillBlankId : fillBlankIds) {
                //判断题目是否存在
                QuestionBank questionBank4 = questionBankMapper.selectById(fillBlankId);
                if ( questionBank4 != null && questionBank4.getIsDelete().equals(DeleteConstant.NO)
                        && questionBank4.getType().equals(QuestionOptionConstant.FILL_BLANKS)) {
                    participateFillBlankIds.add(fillBlankId);
                    if(questionBank4.getIsChoose().equals(StatusConstant.DISABLE)){
                        participateUpdateChoiceIds.add(fillBlankId);
                    }
                }
            }
        }
        //将数据封装到实体类中
        //将所有题目的id集合转换成数组，然后修改题目的被选中状态（一个数组中）
        //修改题目的被选中状态
        if(!participateUpdateChoiceIds.isEmpty()){
            questionBankMapper.updateQuestionBankSelected(participateUpdateChoiceIds.toArray(new String[0]));
        }
        //先将存在的数据id集合转换成字符串
        String participateUserIdsStr = String.join(",", participateUserIds);
        String participateSingleChoiceIdsStr = String.join(",", participateSingleChoiceIds);
        String participateMultipleChoiceIdsStr = String.join(",", participateMultipleChoiceIds);
        String participateJudgeIdsStr = String.join(",", participateJudgeIds);
        String participateFillBlankIdsStr = String.join(",", participateFillBlankIds);
        //计算题目的总数量
        Integer totalQuestionCount = participateSingleChoiceIds.size()
                + participateMultipleChoiceIds.size() + participateJudgeIds.size()
                + participateFillBlankIds.size();
        log.info("总题目数量：{}",totalQuestionCount);
        //重新计算其他的数量
        Integer oldtotalQuestionCount = examinationInformation.getQuestionQuantity();
        Integer singleChoiceCount = examinationInformation.getSingleChoiceIds().split(",").length;
        Integer multipleChoiceCount = examinationInformation.getMultipleChoiceIds().split(",").length;
        Integer judgeCount = examinationInformation.getJudgeIds().split(",").length;
        Integer fillBlankCount = examinationInformation.getFillBlankIds().split(",").length;
        //判断修改的是哪类题型的数量
        Integer changeCount = 0;
        if (isNeedUpdateWithValidElement(singleChoiceIds, examinationUpdateDTO.getSingleChoiceIds())) {
            oldtotalQuestionCount -= singleChoiceCount;
            changeCount += (singleChoiceIds != null) ? singleChoiceIds.length : 0;
        }
        if (isNeedUpdateWithValidElement(multipleChoiceIds, examinationUpdateDTO.getMultipleChoiceIds())) {
            oldtotalQuestionCount -= multipleChoiceCount;
            changeCount += (multipleChoiceIds != null) ? multipleChoiceIds.length : 0;
        }
        // 处理判断题
        if (isNeedUpdateWithValidElement(judgeIds, examinationUpdateDTO.getJudgeIds())) {
            oldtotalQuestionCount -= judgeCount;
            changeCount += (judgeIds != null) ? judgeIds.length : 0;
        }
        // 处理填空题
        if (isNeedUpdateWithValidElement(fillBlankIds, examinationUpdateDTO.getFillBlankIds())) {
            oldtotalQuestionCount -= fillBlankCount;
            changeCount += (fillBlankIds != null) ? fillBlankIds.length : 0;
        }
        //设置可以清空某个题型的题目功能
        log.info("修改后的题目数量：{}",oldtotalQuestionCount+totalQuestionCount);
        if(oldtotalQuestionCount+totalQuestionCount == 0){
            throw new BaseException(MessageConstant.QUESTION_ADD_FAILED);
        }

        ExaminationInformation updateExaminationInformation =
                BeanUtil.copyProperties(examinationUpdateDTO, ExaminationInformation.class);
        updateExaminationInformation.setQuestionQuantity(oldtotalQuestionCount+totalQuestionCount);
        //将数据插入到数据库中
        examinationInformationMapper.updateExaminationInformation(updateExaminationInformation);
        if(userIds != null && participateUserIds.size() != userIds.length){
            throw new BaseException(MessageConstant.USER_PART_ADD_FAILED);
        }
        if(totalQuestionCount != changeCount){
            log.info("题目修改数量：{}",changeCount);
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
     * 查询考试信息
     * @param id
     * @return
     */
    public ExaminationVO queryExaminationInformation(String id) {
        //TODO 返回题目
        //判断考试信息是否存在
        ExaminationInformation examinationInformation = examinationInformationMapper.selectById(id);
        if(examinationInformation == null
                || examinationInformation.getIsDelete().equals(DeleteConstant.YES)){
            throw new BaseException(MessageConstant.EXAMINATION_INFORMATION_NOT_EXIST);
        }
        ExaminationVO examinationVO = BeanUtil.copyProperties(examinationInformation, ExaminationVO.class);
        //将字符串转换成数组
        String[] userIds = examinationInformation.getUserIds().split(",");
        //将用户的id转换成用户名字
        List<String> userNameList = userMapper.queryUserNames(userIds);
        //返回用户名字集合
        examinationVO.setUserNames(String.join(",", userNameList));
        //返回修改人姓名
        examinationVO.setUpdateName(userMapper.selectById(examinationInformation.getUpdateBy()).getUserName());
        //将题目拆解，返回各类题目信心
        //1.先得到各类题目的id集合
        String[] singleChoiceIds = examinationInformation.getSingleChoiceIds().split(",");
        String[] multipleChoiceIds = examinationInformation.getMultipleChoiceIds().split(",");
        String[] judgeIds = examinationInformation.getJudgeIds().split(",");
        String[] fillBlankIds = examinationInformation.getFillBlankIds().split(",");
        //2.根据id集合查询题目信息
        List singleChoiceList = new ArrayList<>();
        List multipleChoiceList = new ArrayList<>();
        List judgeList = new ArrayList<>();
        List fillBlankList = new ArrayList<>();
        if(singleChoiceIds != null && singleChoiceIds.length > 0 && !singleChoiceIds[0].isEmpty()){
            List<QuestionBank> singleChoiceIdsList = questionBankMapper.selectBatchIds(Arrays.asList(singleChoiceIds));
            for (QuestionBank singleChoice : singleChoiceIdsList) {
                //将单选题信息进行存储
                List<String> questionOptionList = new ArrayList<>();
                if(singleChoice.getQuestionOption() != null && !singleChoice.getQuestionOption().isEmpty()) {
                    String[] questionOptions = singleChoice.getQuestionOption().split(",");
                    for (String questionOption : questionOptions) {
                        questionOptionList.add(questionOption);
                    }
                }
                //创建一个ChoiceVO对象，来进行存储
                ChoiceVO choiceVO = ChoiceVO.builder()
                        .id(singleChoice.getId())
                        .question(singleChoice.getQuestion())
                        .questionOption(questionOptionList)
                        .build();
                singleChoiceList.add(choiceVO);
            }
        }
        if(multipleChoiceIds != null && multipleChoiceIds.length > 0 && !multipleChoiceIds[0].isEmpty()){
            List<QuestionBank> multipleChoiceIdsList = questionBankMapper.selectBatchIds(Arrays.asList(multipleChoiceIds));
            for (QuestionBank multipleChoice : multipleChoiceIdsList) {
                //将多选题信息进行存储
                List<String> questionOptionList = new ArrayList<>();
                if(multipleChoice.getQuestionOption() != null && !multipleChoice.getQuestionOption().isEmpty()) {
                    String[] questionOptions = multipleChoice.getQuestionOption().split(",");
                    for (String questionOption : questionOptions) {
                        questionOptionList.add(questionOption);
                    }
                }
                //创建一个ChoiceVO对象，来进行存储
                ChoiceVO choiceVO = ChoiceVO.builder()
                        .id(multipleChoice.getId())
                        .question(multipleChoice.getQuestion())
                        .questionOption(questionOptionList)
                        .build();
                multipleChoiceList.add(choiceVO);
            }
        }
        //判断题和填空题只用将题目储存在list集合中
        if(judgeIds != null && judgeIds.length > 0 && !judgeIds[0].isEmpty()){
            List<QuestionBank> judgeIdsList = questionBankMapper.selectBatchIds(Arrays.asList(judgeIds));
            for (QuestionBank judge : judgeIdsList) {
                //创建一个WriteVO对象，来进行存储
                WriteVO writeVO = WriteVO.builder()
                        .id(judge.getId())
                        .question(judge.getQuestion())
                        .build();
                judgeList.add(writeVO);
            }
        }
        if(fillBlankIds != null && fillBlankIds.length > 0 && !fillBlankIds[0].isEmpty()){
            List<QuestionBank> fillBlankIdsList = questionBankMapper.selectBatchIds(Arrays.asList(fillBlankIds));
            for (QuestionBank fillBlank : fillBlankIdsList) {
                //创建一个WriteVO对象，来进行存储
                WriteVO writeVO = WriteVO.builder()
                        .id(fillBlank.getId())
                        .question(fillBlank.getQuestion())
                        .build();
                fillBlankList.add(writeVO);
            }
        }
        //3.讲题目处理成对应格式，并返回
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


}
