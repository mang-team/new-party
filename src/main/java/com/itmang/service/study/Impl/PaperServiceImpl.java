package com.itmang.service.study.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.StatusConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.mapper.study.ExaminationInformationMapper;
import com.itmang.mapper.study.ExaminationPaperMapper;
import com.itmang.mapper.study.QuestionBankMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.AddPaperDTO;
import com.itmang.pojo.dto.PaperPageDTO;
import com.itmang.pojo.dto.PaperUpdateDTO;
import com.itmang.pojo.entity.*;
import com.itmang.pojo.vo.PaperPageVO;
import com.itmang.pojo.vo.PaperVO;
import com.itmang.service.study.PaperService;
import com.itmang.utils.AnswerParserUtil;
import com.itmang.utils.CodeUtil;
import com.itmang.utils.IdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class PaperServiceImpl extends ServiceImpl<ExaminationPaperMapper, ExaminationPaper> implements PaperService {

    @Autowired
    private ExaminationPaperMapper examinationPaperMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ExaminationInformationMapper examinationInformationMapper;
    @Autowired
    private QuestionBankMapper questionBankMapper;


    /**
     * 新增考卷
     * @param addPaperDTO
     */
    public void addPaperById(AddPaperDTO addPaperDTO) {
        //先查看考试信息存不存在
        ExaminationInformation examinationInformation =
                examinationInformationMapper.selectById(addPaperDTO.getExaminationInformationId());
        if (examinationInformation == null ||
                examinationInformation.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.EXAMINATION_INFORMATION_NOT_EXIST);
        }
        //再查看用户是否存在
        String[] userIds = addPaperDTO.getIds().split(",");
        List<String> canAddUserIds = new ArrayList<>();
        for (String userId : userIds) {
            User user = userMapper.selectById(userId);
            if ( user == null || user.getIsDelete().equals(DeleteConstant.YES)){
                continue;
            }
            canAddUserIds.add(userId);
        }
        if (!canAddUserIds.isEmpty()) {
            // 为每个用户生成独立的考卷记录
            List<ExaminationPaper> examinationPapers = new ArrayList<>();
            IdGenerate idGenerate = new IdGenerate();
            for (String userId : canAddUserIds) {
                // 为每个用户生成独立的考卷ID和编号
                String paperId = idGenerate.nextUUID(ExaminationPaper.class);
                ExaminationPaper examinationPaper = ExaminationPaper.builder()
                        .id(paperId)
                        .number(CodeUtil.getCode())
                        .examinationInformationId(addPaperDTO.getExaminationInformationId())
                        .userId(userId)
                        .questionQuantity(examinationInformation.getQuestionQuantity())
                        .answeredAlreadyQuantity(0)
                        .isSubmit(StatusConstant.DISABLE)
                        .score(0)
                        .createBy(BaseContext.getCurrentId())
                        .createTime(LocalDateTime.now())
                        .updateBy(BaseContext.getCurrentId())
                        .updateTime(LocalDateTime.now())
                        .isDelete(DeleteConstant.NO)
                        .build();

                examinationPapers.add(examinationPaper);
            }
            // 一次性为所有有效用户插入考卷记录
            examinationPaperMapper.insertExaminationPaper(examinationPapers);
            //判断逻辑
            if(canAddUserIds.size() != userIds.length){
                throw new BaseException(MessageConstant.ADD_PART_PAPER);
            }
        }else{
            throw new BaseException(MessageConstant.ADD_PAPER_FAIL);
        }
    }

    /**
     * 根据id修改考卷状态
     * @param paperId
     * @param submit
     */
    public void submitPaperById(String paperId, Integer submit) {
        // 根据paperId查询考卷信息
        ExaminationPaper examinationPaper = examinationPaperMapper.selectById(paperId);
        if (examinationPaper == null || examinationPaper.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.PAPER_NOT_EXIST);
        }
        // 更新考卷状态
        if(examinationPaper.getIsSubmit().equals(submit)){
            if(submit.equals(StatusConstant.ENABLE)){
                throw new BaseException(MessageConstant.PAPER_HAVE_ALREADY_SUBMIT);
            }else{
                throw new BaseException(MessageConstant.PAPER_HAVE_RETURN);
            }
        }else{
            // 更新数据库中的考卷信息
            ExaminationPaper updateExaminationPaper = ExaminationPaper.builder()
                    .id(paperId)
                    .isSubmit(submit)
                    .build();
            examinationPaperMapper.updatePaperById(updateExaminationPaper);
        }


    }

    /**
     * 根据id删除考卷
     * @param ids
     */
    public void deletePaperById(String[] ids) {
        List<String> canDeletePaperIds = new ArrayList<>();
        for (String id : ids) {
            ExaminationPaper examinationPaper = examinationPaperMapper.selectById(id);
            if (examinationPaper == null
                    || examinationPaper.getIsDelete().equals(DeleteConstant.YES)) {
                continue;
            }
            //判断考卷是否提交了
            if(examinationPaper.getIsSubmit().equals(StatusConstant.DISABLE)){
                continue;
            }
            canDeletePaperIds.add(id);
        }
        if (!canDeletePaperIds.isEmpty()) {
            examinationPaperMapper.deletePaperById(canDeletePaperIds);
            if(canDeletePaperIds.size() != ids.length){
                throw new BaseException(MessageConstant.DELETE_PART_PAPER);
            }
        }else{
            throw new BaseException(MessageConstant.DELETE_PAPER_FAIL);
        }
    }

    /**
     * 根据id修改考卷答案
     * @param paperUpdateDTO
     */
    public void updatePaperAnswer(PaperUpdateDTO paperUpdateDTO) {
        // 校验数据有效性
        if (paperUpdateDTO.getId() == null || paperUpdateDTO.getId().isEmpty()) {
            throw new BaseException(MessageConstant.ID_CANNOT_BE_NULL);
        }
        // 查询考卷信息
        ExaminationPaper examinationPaper = examinationPaperMapper.selectById(paperUpdateDTO.getId());
        if (examinationPaper == null || examinationPaper.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.PAPER_NOT_EXIST);
        }
        // 状态校验：已提交的考卷不能修改
        if (examinationPaper.getIsSubmit().equals(StatusConstant.ENABLE)) {
            throw new BaseException(MessageConstant.PAPER_HAVE_ALREADY_SUBMIT);
        }
        // 构建更新对象
        ExaminationPaper updatePaper = ExaminationPaper.builder()
                .id(paperUpdateDTO.getId())
                .answeredAlreadyQuantity(paperUpdateDTO.getAnsweredAlreadyQuantity())
                .singleChoiceAnswers(paperUpdateDTO.getSingleChoiceAnswers())
                .multipleChoiceAnswers(paperUpdateDTO.getMultipleChoiceAnswers())
                .judgeAnswers(paperUpdateDTO.getJudgeAnswers())
                .fillBlankAnswers(paperUpdateDTO.getFillBlankAnswers())
                .build();
        // 执行更新
        examinationPaperMapper.updatePaperById(updatePaper);
    }

    /**
     * 根据id查询考卷
     * @param id
     * @return
     */
    public PaperVO queryPaperById(String id) {
        // 参数校验
        if (id == null || id.isEmpty()) {
            throw new BaseException(MessageConstant.ID_CANNOT_BE_NULL);
        }
        // 查询考卷信息
        PaperVO paperVO = examinationPaperMapper.getPaperVOById(id);
        if (paperVO == null) {
            throw new BaseException(MessageConstant.PAPER_NOT_EXIST);
        }
        return paperVO;
    }

    /**
     * 查询考卷列表
     * @param paperPageDTO
     * @return
     */
    public PageResult queryPaperPageList(PaperPageDTO paperPageDTO) {
        PageHelper.startPage(paperPageDTO.getPage(), paperPageDTO.getPageSize());
        List<PaperPageVO> paperVOList = examinationPaperMapper.getPaperList(paperPageDTO);
        PageInfo<PaperPageVO> pageInfo = new PageInfo<>(paperVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 批量批改考卷
     * @param ids
     */
//    public void correctPapers(String[] ids) {
//        List<String> canCorrectPaperIds = new ArrayList<>();
//        Map<String, List<Map<String, String>>> correctPaperIds = new HashMap<>();
//        for (String id : ids) {
//            //先验证考卷是否存在
//            ExaminationPaper examinationPaper = examinationPaperMapper.selectById(id);
//            if (examinationPaper == null || examinationPaper.getIsDelete().equals(DeleteConstant.YES)) {
//                throw new BaseException(MessageConstant.PAPER_NOT_EXIST);
//            }
//            //判断考卷是否提交了
//            if(examinationPaper.getIsSubmit().equals(StatusConstant.DISABLE)){
//                throw new BaseException(MessageConstant.PAPER_NOT_SUBMIT);
//            }
//            //查找correctPaperIds集合中是否有该配套的考试题目id
//            if(!correctPaperIds.containsKey(examinationPaper.getExaminationInformationId())){
//                //不存在的话就直接添加，并将每种题目的答案都存储起来
//                List<Map<String, String>> correctPaperId = new ArrayList<>();
//                Map<String, String> singleChoiceAnswers = new HashMap<>();
//                Map<String, String> multipleChoiceAnswers = new HashMap<>();
//                Map<String, String> judgeAnswers = new HashMap<>();
//                Map<String, String> fillBlankAnswers = new HashMap<>();
//                //获得单选题答案
//                ExaminationInformation examinationInformation =
//                        examinationInformationMapper.selectById(examinationPaper.getExaminationInformationId());
//                //分别得到单选题、多选题、判断题、填空题的ids并查询答案
//                if(examinationInformation.getSingleChoiceIds() != null){
//                    String[] singleChoiceIdArray = examinationInformation.getSingleChoiceIds().split(",");
//                    //查询每个单选题答案并记录
//                    for (String singleChoiceId : singleChoiceIdArray) {
//                        QuestionBank questionBank = questionBankMapper.selectById(singleChoiceId);
//                        //将每一题的答案都记到单选题答案集合中
//                        singleChoiceAnswers.put(questionBank.getId(), questionBank.getAnswerType());
//                    }
//                    correctPaperId.add(singleChoiceAnswers);
//                }
//                if(examinationInformation.getMultipleChoiceIds() != null){
//                    String[] multipleChoiceIdArray = examinationInformation.getMultipleChoiceIds().split(",");
//                    for (String multipleChoiceId : multipleChoiceIdArray) {
//                        QuestionBank questionBank = questionBankMapper.selectById(multipleChoiceId);
//                        //将每一题的答案都记到单选题答案集合中
//                        multipleChoiceAnswers.put(questionBank.getId(), questionBank.getAnswerType());
//                    }
//                    correctPaperId.add(multipleChoiceAnswers);
//                }
//                if(examinationInformation.getJudgeIds() != null){
//                    String[] judgeIdArray = examinationInformation.getJudgeIds().split(",");
//                    for (String judgeId : judgeIdArray) {
//                        QuestionBank questionBank = questionBankMapper.selectById(judgeId);
//                        //将每一题的答案都记到单选题答案集合中
//                        judgeAnswers.put(questionBank.getId(), questionBank.getAnswerType());
//                    }
//                    correctPaperId.add(judgeAnswers);
//                }
//                if(examinationInformation.getFillBlankIds() != null){
//                    String[] fillBlankIdArray = examinationInformation.getFillBlankIds().split(",");
//                    for (String fillBlankId : fillBlankIdArray) {
//                        QuestionBank questionBank = questionBankMapper.selectById(fillBlankId);
//                        //将每一题的答案都记到单选题答案集合中
//                        fillBlankAnswers.put(questionBank.getId(), questionBank.getAnswerType());
//                    }
//                    correctPaperId.add(fillBlankAnswers);
//                }
//            }
//            //已经有配套的考试题目id的答案储存在Map集合中了，直接批改试卷
//            //先获得考生试卷作答的答案
//            //数据库中的数据是[1,2,1,3]这样存储的，每个都是一个题的答案,将答案装转化成字符串集合
//            List<String> singleChoiceAnswersList = AnswerParserUtil
//                    .parseSingleChoiceAnswers(examinationPaper.getSingleChoiceAnswers());
//            List<List<String>> multipleChoiceAnswersList = AnswerParserUtil
//                    .parseMultipleChoiceAnswers(examinationPaper.getMultipleChoiceAnswers());
//            List<String> judgeAnswersList = AnswerParserUtil
//                    .parseSingleChoiceAnswers(examinationPaper.getJudgeAnswers());
//            List<String> fillBlankAnswersList = AnswerParserUtil
//                    .parseFillBlankAnswers(examinationPaper.getFillBlankAnswers());
//            //获得答案后，直接开始对比答案与作答答案，并给出得分
//            Integer score = 0;
//            for(String item : singleChoiceAnswersList){
//
//            }
//
//        }
//    }
    public void correctPapers(String[] ids) {
        // 缓存：考试信息ID -> 正确答案容器（避免重复查询）
        Map<String, List<Map<String, String>>> correctPaperIds = new HashMap<>();

        for (String id : ids) {
            // 先验证考卷是否存在
            ExaminationPaper examinationPaper = examinationPaperMapper.selectById(id);
            if (examinationPaper == null || examinationPaper.getIsDelete().equals(DeleteConstant.YES)) {
                throw new BaseException(MessageConstant.PAPER_NOT_EXIST);
            }
            // 判断考卷是否提交了
            if (examinationPaper.getIsSubmit().equals(StatusConstant.DISABLE)) {
                throw new BaseException(MessageConstant.PAPER_NOT_SUBMIT);
            }

            // 查找correctPaperIds集合中是否有该配套的考试题目id
            String examInfoId = examinationPaper.getExaminationInformationId();
            if (!correctPaperIds.containsKey(examInfoId)) {
                // 不存在的话就直接添加，并将每种题目的答案都存储起来
                List<Map<String, String>> correctPaperId = new ArrayList<>();
                Map<String, String> singleChoiceAnswers = new HashMap<>();
                Map<String, String> multipleChoiceAnswers = new HashMap<>();
                Map<String, String> judgeAnswers = new HashMap<>();
                Map<String, String> fillBlankAnswers = new HashMap<>();

                // 获得单选题答案
                ExaminationInformation examinationInformation =
                        examinationInformationMapper.selectById(examInfoId);
                // 分别得到单选题、多选题、判断题、填空题的ids并查询答案
                if (examinationInformation.getSingleChoiceIds() != null) {
                    String[] singleChoiceIdArray = examinationInformation.getSingleChoiceIds().split(",");
                    // 查询每个单选题答案并记录
                    for (String singleChoiceId : singleChoiceIdArray) {
                        QuestionBank questionBank = questionBankMapper.selectById(singleChoiceId);
                        // 将每一题的答案都记到单选题答案集合中
                        singleChoiceAnswers.put(questionBank.getId(), questionBank.getAnswerType());
                    }
                    correctPaperId.add(singleChoiceAnswers);
                }
                if (examinationInformation.getMultipleChoiceIds() != null) {
                    String[] multipleChoiceIdArray = examinationInformation.getMultipleChoiceIds().split(",");
                    for (String multipleChoiceId : multipleChoiceIdArray) {
                        QuestionBank questionBank = questionBankMapper.selectById(multipleChoiceId);
                        // 将每一题的答案都记到多选题答案集合中
                        multipleChoiceAnswers.put(questionBank.getId(), questionBank.getAnswerType());
                    }
                    correctPaperId.add(multipleChoiceAnswers);
                }
                if (examinationInformation.getJudgeIds() != null) {
                    String[] judgeIdArray = examinationInformation.getJudgeIds().split(",");
                    for (String judgeId : judgeIdArray) {
                        QuestionBank questionBank = questionBankMapper.selectById(judgeId);
                        // 将每一题的答案都记到判断题答案集合中
                        judgeAnswers.put(questionBank.getId(), questionBank.getAnswerType());
                    }
                    correctPaperId.add(judgeAnswers);
                }
                if (examinationInformation.getFillBlankIds() != null) {
                    String[] fillBlankIdArray = examinationInformation.getFillBlankIds().split(",");
                    for (String fillBlankId : fillBlankIdArray) {
                        QuestionBank questionBank = questionBankMapper.selectById(fillBlankId);
                        // 将每一题的答案都记到填空题答案集合中
                        fillBlankAnswers.put(questionBank.getId(), questionBank.getAnswerType());
                    }
                    correctPaperId.add(fillBlankAnswers);
                }

                // 将该考试的所有题型答案存入缓存
                correctPaperIds.put(examInfoId, correctPaperId);
            }

            // 已经有配套的考试题目id的答案储存在Map集合中了，直接批改试卷
            // 先获得考生试卷作答的答案
            // 数据库中的数据是[1,2,1,3]这样存储的，每个都是一个题的答案,将答案装转化成字符串集合
            List<String> singleChoiceAnswersList = AnswerParserUtil
                    .parseSingleChoiceAnswers(examinationPaper.getSingleChoiceAnswers());
            List<List<String>> multipleChoiceAnswersList = AnswerParserUtil
                    .parseMultipleChoiceAnswers(examinationPaper.getMultipleChoiceAnswers());
            List<String> judgeAnswersList = AnswerParserUtil
                    .parseSingleChoiceAnswers(examinationPaper.getJudgeAnswers());
            List<String> fillBlankAnswersList = AnswerParserUtil
                    .parseFillBlankAnswers(examinationPaper.getFillBlankAnswers());

            // 获得答案后，直接开始对比答案与作答答案，并给出得分
            Integer score = calculateScore(correctPaperIds.get(examInfoId),
                    singleChoiceAnswersList, multipleChoiceAnswersList,
                    judgeAnswersList, fillBlankAnswersList,
                    examinationInformationMapper.selectById(examInfoId));

            // 更新考卷分数和批改状态
            ExaminationPaper updateExaminationPaper = ExaminationPaper.builder()
                    .id(examinationPaper.getId())
                    .score(score)
                    .build();
            examinationPaperMapper.updatePaperById(updateExaminationPaper);
        }
    }

    /**
     * 计算考生得分
     */
    private Integer calculateScore(List<Map<String, String>> correctAnswersList,
                                   List<String> singleChoiceAnswersList,
                                   List<List<String>> multipleChoiceAnswersList,
                                   List<String> judgeAnswersList,
                                   List<String> fillBlankAnswersList,
                                   ExaminationInformation examInfo) {
        //通过examInfo获取各题型的分值，分值是由集合存储的，建立一个集合存储，存储顺序为单选题、多选题、判断题、填空题
        String[] scoreValue = examInfo.getScoreValue().split(",");
        Integer singleChoiceScore = Integer.parseInt(scoreValue[0]);
        Integer multipleChoiceScore = Integer.parseInt(scoreValue[1]);
        Integer judgeScore = Integer.parseInt(scoreValue[2]);
        Integer fillBlankScore = Integer.parseInt(scoreValue[3]);
        log.info("各题题分值：{},{},{},{}",
                singleChoiceScore, multipleChoiceScore, judgeScore, fillBlankScore);
        // 从correctAnswersList中提取各题型正确答案
        Map<String, String> singleChoiceAnswers = correctAnswersList.get(0); // 单选题答案
        Map<String, String> multipleChoiceAnswers = correctAnswersList.get(1); // 多选题答案
        Map<String, String> judgeAnswers = correctAnswersList.get(2); // 判断题答案
        Map<String, String> fillBlankAnswers = correctAnswersList.get(3); // 填空题答案

        // 初始化总分
        int score = 0;

        // 计算单选题得分
        if (examInfo.getSingleChoiceIds() != null) {
            String[] singleChoiceIds = examInfo.getSingleChoiceIds().split(",");
            for (int i = 0; i < singleChoiceIds.length && i < singleChoiceAnswersList.size(); i++) {
                String questionId = singleChoiceIds[i];
                String correctAnswer = singleChoiceAnswers.get(questionId);
                String studentAnswer = singleChoiceAnswersList.get(i);

                //TODO 这里控制题目的分值不正确
                if (correctAnswer != null && correctAnswer.equals(studentAnswer)) {
                    // 单选题答对，加分（需根据题目分值计算，此处假设每题1分）
                    score += singleChoiceScore;
                }
            }
        }

        // 计算多选题得分（需完全匹配才得分）
        if (examInfo.getMultipleChoiceIds() != null) {
            String[] multipleChoiceIds = examInfo.getMultipleChoiceIds().split(",");
            for (int i = 0; i < multipleChoiceIds.length && i < multipleChoiceAnswersList.size(); i++) {
                String questionId = multipleChoiceIds[i];
                String correctAnswerStr = multipleChoiceAnswers.get(questionId);
                List<String> studentAnswerList = multipleChoiceAnswersList.get(i);

                if (correctAnswerStr != null) {
                    List<String> correctAnswerList = Arrays.asList(correctAnswerStr.split(","));
                    // 多选题需完全匹配才得分（需根据题目分值计算，此处假设每题2分）
                    if (correctAnswerList.size() == studentAnswerList.size() &&
                            correctAnswerList.containsAll(studentAnswerList)) {
                        score += multipleChoiceScore;
                    }
                }
            }
        }

        // 计算判断题得分
        if (examInfo.getJudgeIds() != null) {
            String[] judgeIds = examInfo.getJudgeIds().split(",");
            for (int i = 0; i < judgeIds.length && i < judgeAnswersList.size(); i++) {
                String questionId = judgeIds[i];
                String correctAnswer = judgeAnswers.get(questionId);
                String studentAnswer = judgeAnswersList.get(i);

                if (correctAnswer != null && correctAnswer.equals(studentAnswer)) {
                    // 判断题答对，加分（需根据题目分值计算，此处假设每题1分）
                    score += judgeScore;
                }
            }
        }

        // 计算填空题得分（需所有空都答对才得分）
        if (examInfo.getFillBlankIds() != null) {
            String[] fillBlankIds = examInfo.getFillBlankIds().split(",");
            for (int i = 0; i < fillBlankIds.length && i < fillBlankAnswersList.size(); i++) {
                String questionId = fillBlankIds[i];
                String correctAnswerStr = fillBlankAnswers.get(questionId);
                String studentAnswerStr = fillBlankAnswersList.get(i);

                if (correctAnswerStr != null && studentAnswerStr != null) {
                    String[] correctAnswers = correctAnswerStr.split(";"); // 假设填空题多个空用分号分隔
                    String[] studentAnswers = studentAnswerStr.split(";");
                    //去除两边的[]符号,并将答案修改
                    studentAnswers = Arrays.stream(studentAnswers).map(s -> s.substring(1, s.length() - 1)).toArray(String[]::new);
                    boolean allCorrect = true;
                    if (correctAnswers.length == studentAnswers.length) {
                        for (int j = 0; j < correctAnswers.length; j++) {
                            if (!correctAnswers[j].equals(studentAnswers[j])) {
                                allCorrect = false;
                                break;
                            }
                        }
                    } else {
                        allCorrect = false;
                    }

                    if (allCorrect) {
                        // 填空题答对，加分（需根据题目分值计算，此处假设每题3分）
                        score += fillBlankScore;
                    }
                }
            }
        }
        return score;
    }

}
