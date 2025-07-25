package com.itmang.service.study.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.QuestionOptionConstant;
import com.itmang.constant.StatusConstant;
import com.itmang.exception.BaseException;
import com.itmang.mapper.study.ExaminationInformationMapper;
import com.itmang.mapper.study.QuestionBankMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.AddExaminationDTO;
import com.itmang.pojo.dto.ExaminationPageDTO;
import com.itmang.pojo.dto.ExaminationUpdateDTO;
import com.itmang.pojo.entity.*;
import com.itmang.pojo.vo.ExaminationPageVO;
import com.itmang.pojo.vo.ExaminationVO;
import com.itmang.service.study.ExaminationService;
import com.itmang.utils.IdGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
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
        if(participateUserIds.size()>0){
            //分别将题目的id集合进行分割成数组
            String[] singleChoiceIds = addExaminationDTO.getSingleChoiceIds().split(",");
            String[] multipleChoiceIds = addExaminationDTO.getMultipleChoiceIds().split(",");
            String[] judgeIds = addExaminationDTO.getJudgeIds().split(",");
            String[] fillBlankIds = addExaminationDTO.getFillBlankIds().split(",");
            List<String> participateUpdateChoiceIds = new ArrayList<>();
            List<String> participateSingleChoiceIds = new ArrayList<>();
            List<String> participateMultipleChoiceIds = new ArrayList<>();
            List<String> participateJudgeIds = new ArrayList<>();
            List<String> participateFillBlankIds = new ArrayList<>();
            //分别创建集合进行存储不同的题目
            if(singleChoiceIds.length > 0) {
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
            if(multipleChoiceIds.length > 0) {
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
            if(judgeIds.length > 0) {
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
            if(fillBlankIds.length > 0) {;
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
            if(participateUpdateChoiceIds.size()>0){
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
        //先查找考试信息是否存在
        ExaminationInformation examinationInformation = examinationInformationMapper
                .selectById(examinationUpdateDTO.getId());
        if(examinationInformation == null ||
            examinationInformation.getIsDelete().equals(DeleteConstant.YES)){
            throw new BaseException(MessageConstant.EXAMINATION_INFORMATION_NOT_EXIST);
        }
        //先查询用户都是否存在
        //用户的id集合是用逗号分隔的字符串，先将数据进行分割
        List<String> participateUserIds = new ArrayList<>();
        String[] userIds = examinationUpdateDTO.getUserIds().split(",");
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
        if(participateUserIds.size()>0){
            //分别将题目的id集合进行分割成数组
            String[] singleChoiceIds = examinationUpdateDTO.getSingleChoiceIds().split(",");
            String[] multipleChoiceIds = examinationUpdateDTO.getMultipleChoiceIds().split(",");
            String[] judgeIds = examinationUpdateDTO.getJudgeIds().split(",");
            String[] fillBlankIds = examinationUpdateDTO.getFillBlankIds().split(",");
            List<String> participateUpdateChoiceIds = new ArrayList<>();
            List<String> participateSingleChoiceIds = new ArrayList<>();
            List<String> participateMultipleChoiceIds = new ArrayList<>();
            List<String> participateJudgeIds = new ArrayList<>();
            List<String> participateFillBlankIds = new ArrayList<>();
            //分别创建集合进行存储不同的题目
            if(singleChoiceIds.length > 0) {
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
            if(multipleChoiceIds.length > 0) {
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
            if(judgeIds.length > 0) {
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
            if(fillBlankIds.length > 0) {;
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
            if(participateUpdateChoiceIds.size()>0){
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
            ExaminationInformation updateExaminationInformation = ExaminationInformation.builder()
                    .id(examinationUpdateDTO.getId())
                    .examinationName(examinationUpdateDTO.getExaminationName())
                    .userIds(participateUserIdsStr)
                    .scoreValue(examinationUpdateDTO.getScoreValue())
                    .singleChoiceIds(participateSingleChoiceIdsStr)
                    .multipleChoiceIds(participateMultipleChoiceIdsStr)
                    .judgeIds(participateJudgeIdsStr)
                    .fillBlankIds(participateFillBlankIdsStr)
                    .questionQuantity(totalQuestionCount)
                    .startTime(examinationUpdateDTO.getStartTime())
                    .endTime(examinationUpdateDTO.getEndTime())
                    .build();
            //将数据插入到数据库中
            examinationInformationMapper.updateExaminationInformation(updateExaminationInformation);
            if(participateUserIds.size() != userIds.length){
                throw new BaseException(MessageConstant.USER_PART_ADD_FAILED);
            }
            if(totalQuestionCount != singleChoiceIds.length+multipleChoiceIds.length
                    +judgeIds.length+fillBlankIds.length){
                throw new BaseException(MessageConstant.QUESTION_PART_ADD_FAILED);
            }
        }else{
            //用户添加失败
            throw new BaseException(MessageConstant.USER_ADD_FAILED);
        }
    }

    /**
     * 查询考试信息
     * @param id
     * @return
     */
    public ExaminationVO queryExaminationInformation(String id) {
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
        examinationVO.setUserNames(String.join(",", userNameList));
        examinationVO.setUpdateName(userMapper.selectById(examinationInformation.getUpdateBy()).getUserName());
        return examinationVO;
    }

    /**
     * 查询考试信息列表
     * @param examinationPageDTO
     * @return
     */
    public PageResult queryExaminationInformationList(ExaminationPageDTO examinationPageDTO) {
        PageHelper.startPage(examinationPageDTO.getPage(), examinationPageDTO.getPageSize());
        List<ExaminationPageVO> examinationInformationList =
                examinationInformationMapper.selectExaminationInformationList(examinationPageDTO);
        PageInfo<ExaminationPageVO> pageInfo = new PageInfo<>(examinationInformationList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }


}
