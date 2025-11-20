package com.itmang.service.study.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.*;
import com.itmang.exception.BaseException;
import com.itmang.mapper.study.DatasMapper;
import com.itmang.mapper.study.StudyRecordMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.AddSituationDTO;
import com.itmang.pojo.dto.LearnSituationDTO;
import com.itmang.pojo.dto.SituationPageDTO;
import com.itmang.pojo.dto.SituationUpdateDTO;
import com.itmang.pojo.entity.Datas;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.StudyRecord;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.vo.DatasPageVO;
import com.itmang.pojo.vo.LearnSituationVO;
import com.itmang.pojo.vo.SituationPageVO;
import com.itmang.pojo.vo.SituationVO;
import com.itmang.service.study.SituationService;
import com.itmang.utils.IdGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SituationServiceImpl extends ServiceImpl<StudyRecordMapper, StudyRecord> implements SituationService {


    @Autowired
    private StudyRecordMapper studyRecordMapper;
    @Autowired
    private DatasMapper datasMapper;
    @Autowired
    private UserMapper userMapper;
    // 全局计时器Map：key=学习记录ID，value=对应的Timer实例
    private final ConcurrentHashMap<String, Timer> timerMap = new ConcurrentHashMap<>();
    // 后端累计学习时间Map：key=学习记录ID，value=累计秒数（用于校验前端传入的时间）
    private final ConcurrentHashMap<String, Integer> backendLearningTimeMap = new ConcurrentHashMap<>();



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
        //先对数据进行验证
        //如果只有id则不报错
        if ((situationUpdateDTO.getContent() == null || situationUpdateDTO.getContent() == " " ) ) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
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
        //查看是否有页数据
        //配置默认页码1和分页大小5
        if(situationPageDTO.getPage() == null || situationPageDTO.getPage() < 1){
            situationPageDTO.setPage(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(situationPageDTO.getPageSize() == null || situationPageDTO.getPageSize() < 1){
            situationPageDTO.setPageSize(PageConstant.PAGE_SIZE);
        }
        PageHelper.startPage(situationPageDTO.getPage(),situationPageDTO.getPageSize());
        List<SituationPageVO> situationList= studyRecordMapper.pageSearch(situationPageDTO);//进行条件查询
        PageInfo<SituationPageVO> pageInfo = new PageInfo<>(situationList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 结束学习资料
     * @param learnSituationDTO 包含学习记录ID和前端传入的学习时间
     */
    public void learnEndSituation(LearnSituationDTO learnSituationDTO) {
        String recordId = learnSituationDTO.getId();
        // 1. 校验ID
        if (recordId == null || recordId.isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        // 2. 校验学习记录是否存在
        StudyRecord studyRecord = studyRecordMapper.selectById(recordId);
        if (studyRecord == null || studyRecord.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.SITUATION_NOT_EXISTS);
        }
        // 3. 获取前端传入的学习时间，校验合法性
        Integer frontendTime = learnSituationDTO.getLearningTime();
        if (frontendTime == null || frontendTime <= 0) {
            throw new BaseException(MessageConstant.LEARNING_TIME_ERROR);
        }
        // 4. 根据ID获取并取消对应的计时器（关键：通过ID操作指定计时器）
        Timer timer = timerMap.get(recordId);
        if (timer != null) {
            timer.cancel(); // 取消计时任务
            timerMap.remove(recordId); // 从Map中移除，释放资源
        } else {
            // 若计时器不存在，可能是前端异常结束，按业务需求处理（这里抛异常）
            throw new BaseException(MessageConstant.TIMER_NOT_EXIST);
        }
        // 5. 获取后端累计的学习时间（用于校验前端时间是否合理）
        Integer backendTime = backendLearningTimeMap.getOrDefault(recordId, 0);
        backendLearningTimeMap.remove(recordId); // 移除累计时间，释放资源
        System.out.printf("前端时间：%d，后端时间：%d\n", frontendTime, backendTime);
        // 6. 校验前后端时间差（允许±10秒误差）

        if (Math.abs(frontendTime - backendTime) <= 10) {
            Integer newLearningTime = frontendTime + studyRecord.getLearningTime();
            System.out.println("更新学习时间：" + newLearningTime);
            // 计算学习进度（假设及格时间为PASS_TIME，满分进度为100）
            int progressPercentage = (int) Math.min(
                    (newLearningTime * 100.0) / 
                            (learnSituationDTO.getVideoTime() != null && learnSituationDTO.getVideoTime() > 0 ? 
                            learnSituationDTO.getVideoTime() : SituationConstant.PASS_TIME),
                    SituationConstant.FULL_PROGRAM // 例如100
            );

            // 构建更新对象
            StudyRecord updateRecord = StudyRecord.builder()
                    .id(recordId)
                    .learningTime(newLearningTime) // 累加学习时间（秒）
                    .learningProgress(progressPercentage)
                    // 若达到及格进度，标记可获得积分
                    .isPoints(progressPercentage >= SituationConstant.PASS_PROGRAM ? StatusConstant.ENABLE : StatusConstant.DISABLE)
                    .build();

            // 更新数据库
            studyRecordMapper.updateStudyRecord(updateRecord);
        } else {
            // 时间差过大，可能是前端作弊，抛异常
            throw new BaseException(MessageConstant.TIME_DIFFERENCE_TOO_LARGE);
        }
    }


    /**
     * 开始学习资料（创建并启动对应ID的计时器）
     * @param id 学习记录ID
     * @return 学习情况VO
     */
    public LearnSituationVO learnStartSituation(String id) {
        // 1. 校验ID
        if (id == null || id.isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        // 2. 校验学习记录是否存在
        LearnSituationVO learnSituationVO = studyRecordMapper.queryLearnSituation(id);
        if (learnSituationVO == null) {
            throw new BaseException(MessageConstant.SITUATION_NOT_EXISTS);
        }
        // 3. 若已有该ID的计时器，先取消（防止重复启动）
        Timer existingTimer = timerMap.get(id);
        if (existingTimer != null) {
            existingTimer.cancel();
            timerMap.remove(id);
        }
        System.out.println("开始学习");
        // 4. 初始化后端累计时间（从0开始）
        backendLearningTimeMap.put(id, 0);
        // 5. 创建新计时器，每秒累加后端学习时间（关键：绑定ID的计时任务）
        Timer newTimer = new Timer();
        timerMap.put(id, newTimer);
        // 计时任务：每秒+1秒（实际业务可根据需求调整间隔，例如1000ms=1秒）
        newTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 累加后端时间（使用ConcurrentHashMap的原子操作保证线程安全）
                backendLearningTimeMap.compute(id, (k, v) -> v == null ? 1 : v + 1);
            }
        }, 0, 1000); // 延迟0ms开始，每1000ms执行一次
        // 6. 返回学习情况
        return learnSituationVO;
    }

    /**
     * 批量新增学习情况
     * @param addSituationDTO
     */
    public void addsSituation(AddSituationDTO addSituationDTO) {
        //判断id是否存在
        if (addSituationDTO.getDatasId() == null || addSituationDTO.getDatasId().isEmpty()
            || addSituationDTO.getUserId() == null || addSituationDTO.getUserId().isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //将userid拆开
        String[] userIds = addSituationDTO.getUserId().split(",");
        AddSituationDTO newAddSituationDTO = AddSituationDTO.builder()
                .datasId(addSituationDTO.getDatasId())
                .build();
        for (String userId : userIds) {
            newAddSituationDTO.setUserId(userId);
            this.addSituation(newAddSituationDTO);
        }
    }


}
