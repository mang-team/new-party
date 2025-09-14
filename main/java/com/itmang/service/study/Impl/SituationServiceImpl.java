package com.itmang.service.study.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.StatusConstant;
import com.itmang.constant.SituationConstant;
import com.itmang.exception.BaseException;
import com.itmang.mapper.study.DatasMapper;
import com.itmang.mapper.study.StudyRecordMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.AddSituationDTO;
import com.itmang.pojo.dto.SituationPageDTO;
import com.itmang.pojo.dto.SituationUpdateDTO;
import com.itmang.pojo.entity.Datas;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.StudyRecord;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.vo.SituationPageVO;
import com.itmang.pojo.vo.SituationVO;
import com.itmang.service.study.SituationService;
import com.itmang.utils.IdGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SituationServiceImpl extends ServiceImpl<StudyRecordMapper, StudyRecord> implements SituationService {


    @Autowired
    private StudyRecordMapper studyRecordMapper;
    @Autowired
    private DatasMapper datasMapper;
    @Autowired
    private UserMapper userMapper;


    /**
     * 新增学习情况
     * @param addSituationDTO
     */
    public void addSituation(AddSituationDTO addSituationDTO) {
        //先查询用户和资料的id都是否存在
        User user = userMapper.selectById(addSituationDTO.getUserId());
        if (user == null || user.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        Datas datas = datasMapper.selectById(addSituationDTO.getDatasId());
        if (datas == null || datas.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.DATA_NOT_EXISTS);
        }
        //查询用户是否已经学习过该资料
        StudyRecord studyRecord = studyRecordMapper.selectOne(new QueryWrapper<StudyRecord>()
                .eq("user_id", addSituationDTO.getUserId())
                .eq("datas_id", addSituationDTO.getDatasId()));
        if (studyRecord != null && studyRecord.getIsDelete().equals(DeleteConstant.NO)) {
            throw new BaseException(MessageConstant.DATA_ALREADY_STUDIED);
        }
        //查询资料是否可读
        if (datas.getStatus().equals(StatusConstant.DISABLE)) {
            throw new BaseException(MessageConstant.DATA_NOT_READABLE);
        }
        //直接添加
        //生成id
        IdGenerate idGenerate = new IdGenerate();
        String id = idGenerate.nextUUID(StudyRecord.class);
        StudyRecord newStudyRecord = StudyRecord.builder()
                .id(id)
                .userId(addSituationDTO.getUserId())
                .datasId(addSituationDTO.getDatasId())
                .learningTime(0)//开始学习时默认学习时间为0
                .learningProgress(0)//开始学习时默认学习进度为0
                .startTime(LocalDateTime.now())
                .isPoints(StatusConstant.DISABLE)
                .isDelete(DeleteConstant.NO)
                .build();
        studyRecordMapper.addStudyRecord(newStudyRecord);
    }

    /**
     * 修改学习情况
     * @param situationUpdateDTO
     */
    public void updateSituation(SituationUpdateDTO situationUpdateDTO) {
        //先查询学习情况是否存在
        StudyRecord studyRecord = studyRecordMapper.selectById(situationUpdateDTO.getId());
        if (studyRecord == null || studyRecord.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.SITUATION_NOT_EXISTS);
        }
        //修改学习情况
        StudyRecord updateStudyRecord = StudyRecord.builder()
                .id(situationUpdateDTO.getId())
                .content(situationUpdateDTO.getContent())
                .build();
        //根据学习时间判断并记录的当前学习进度与是否获得积分
        if (situationUpdateDTO.getLearningTime() > studyRecord.getLearningTime()
            && situationUpdateDTO.getLearningTime() != studyRecord.getLearningTime()) {
            //学习时间有改动，且是增加，进行判断修改
            updateStudyRecord.setLearningTime(situationUpdateDTO.getLearningTime());
            int progressPercentage = (int) Math.min(
                    (situationUpdateDTO.getLearningTime() * 100) / SituationConstant.PASS_TIME,
                    SituationConstant.FULL_PROGRAM);
            updateStudyRecord.setLearningProgress(progressPercentage);
            if (updateStudyRecord.getLearningProgress() >= SituationConstant.PASS_PROGRAM) {
                updateStudyRecord.setIsPoints(StatusConstant.ENABLE);
            }
        }
        studyRecordMapper.updateStudyRecord(updateStudyRecord);
    }

    /**
     * 查询学习情况
     * @param id
     * @return
     */
    public SituationVO querySituation(String id) {
        //判断学习情况是否存在
        SituationVO  situationVO = studyRecordMapper.querySituation(id);
        if (situationVO == null ) {
            throw new BaseException(MessageConstant.SITUATION_NOT_EXISTS);
        }
        //学习情况存在，直接查询返回
        return situationVO;
    }

    /**
     * 删除学习情况
     * @param situationIds
     */
    public void deleteSituation(String[] situationIds) {
        List<String> canDeleteIds = new ArrayList<>();
        List<String> canNotDeleteIds = new ArrayList<>();
        // 判断学习情况是否存在
        for (String id : situationIds) {
            StudyRecord studyRecord = studyRecordMapper.selectById(id);
            if(studyRecord == null || studyRecord.getIsDelete().equals(DeleteConstant.YES)){
                canNotDeleteIds.add(id);
            } else {
                canDeleteIds.add(id);
            }
        }
        // 执行删除逻辑
        if(!canDeleteIds.isEmpty()) {
            studyRecordMapper.removeBatchByIds(canDeleteIds.toArray(new String[0]));
        }
        // 处理删除结果
        if(!canNotDeleteIds.isEmpty()) {
            if(canDeleteIds.isEmpty()) {
                throw new BaseException(MessageConstant.DATA_FAIL_DELETED);
            } else {
                throw new BaseException(MessageConstant.DATA_PART_DELETED);
            }
        }
    }

    /**
     * 分页查询学习情况
     * @param situationPageDTO
     * @return
     */
    public PageResult pageSituation(SituationPageDTO situationPageDTO) {
        //使用pageHelper工具进行分页查询
        PageHelper.startPage(situationPageDTO.getPage(),situationPageDTO.getPageSize());
        List<SituationPageVO> situationList= studyRecordMapper.pageSearch(situationPageDTO);//进行条件查询
        PageInfo<SituationPageVO> pageInfo = new PageInfo<>(situationList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }


}
