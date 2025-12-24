package com.itmang.service.activity.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.PageConstant;
import com.itmang.constant.StatusConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;

import com.itmang.mapper.activity.SignInRecordMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.AddRegisterRecordDTO;
import com.itmang.pojo.dto.DeleteRegisterRecodeDTO;
import com.itmang.pojo.dto.FindRegisterSignDTO;
import com.itmang.pojo.dto.UpdateRegisterRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.SignInInformation;
import com.itmang.pojo.entity.SignInRecord;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.vo.SignInInformationPageVO;
import com.itmang.pojo.vo.SignInInformationVO;
import com.itmang.pojo.vo.SignInRecordPageVO;
import com.itmang.pojo.vo.SignInRecordVO;
import com.itmang.service.activity.RegisterRecodeService;
import com.itmang.service.activity.RegisterService;
import com.itmang.utils.IdGenerate;
import com.itmang.websocket.WebSocketMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegisterRecodeServiceImpl extends ServiceImpl<SignInRecordMapper, SignInRecord>
        implements RegisterRecodeService {

    @Resource
    private WebSocketMessageService webSocketMessageService;
    @Resource
    private RegisterService  registerService;
    @Resource
    private SignInRecordMapper signInRecordMapper;
    @Resource
    private IdGenerate idGenerate;
    @Resource
    private UserMapper userMapper;


    /**
     * 新增签到信息
     * @param addRegisterRecordDTO
     * @param UserId
     */
    public void addRegisterRecodeInformation(AddRegisterRecordDTO addRegisterRecordDTO, String userId) {
        if (addRegisterRecordDTO == null || addRegisterRecordDTO.getUserIds() == null
                || addRegisterRecordDTO.getUserIds().isEmpty() || addRegisterRecordDTO.getSignInInformationId() == null
               || addRegisterRecordDTO.getSignInInformationId().isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //将信息都提取出来
        String signInId = addRegisterRecordDTO.getSignInInformationId();
        String[] userIds = addRegisterRecordDTO.getUserIds().split(",");
        if(userIds.length == 0){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //为每一个用户创建一个签到记录
        List<SignInRecord> addRegisterRecordDTOList = new ArrayList<>();
        for (String id : userIds) {
            //检查用户是否存在
            User user = userMapper.selectById(id);
            if (user == null || user.getIsDelete() == DeleteConstant.YES) {
                continue;
            }
            //检查是否存在签到记录
            SignInRecord record = this.getOne(new LambdaQueryWrapper<SignInRecord>()
                                .eq(SignInRecord::getUserId, id)
                                .eq(SignInRecord::getSignInInformationId, signInId)
                                .eq(SignInRecord::getIsDelete, DeleteConstant.NO)
                                .last("limit 1")
            );
            if( record != null){
                continue;
            }
            SignInRecord newRecord = SignInRecord.builder()
                    .id(idGenerate.nextUUID(SignInRecord.class))
                    .signInInformationId(signInId)
                    .userId(id)
                    .state(StatusConstant.DISABLE)
                    .createBy(userId)
                    .createTime(LocalDateTime.now())
                    .isDelete(DeleteConstant.NO)
                    .build();
            addRegisterRecordDTOList.add(newRecord);

        }
        if(addRegisterRecordDTOList.isEmpty()){
            throw new BaseException(MessageConstant.SIGN_IN_RECORD_HAVE_EXIST);
        }
        boolean saved = this.saveBatch(addRegisterRecordDTOList);
        if (!saved) {
            log.error("批量保存签到记录失败，userId={}, count={}", userId, addRegisterRecordDTOList.size());
            throw new BaseException("新增签到记录失败");
        }

        SignInInformation signInInformation = registerService.getById(addRegisterRecordDTOList.get(0).getSignInInformationId());
        // 创建签到VO对象
        SignInInformationVO signInVO = SignInInformationVO.builder()
                .id(signInInformation.getId())
                .signInTitle(signInInformation.getSignInTitle())
                .signInContent(signInInformation.getSignInContent())
                .startTime(signInInformation.getStartTime())
                .endTime(signInInformation.getEndTime())
                .build();
        for (SignInRecord record : addRegisterRecordDTOList) {
            // 发送签到VO对象给用户
            webSocketMessageService.sendSignInNotificationToUser(record.getUserId(), signInVO);
        }

        log.info("批量新增签到记录成功，userId={}, count={}", userId, addRegisterRecordDTOList.size());
    }

    /**
     * 删除签到记录
     * @param deleteRegisterRecodeDTO
     * @param userId
     */
    public void deleteRegisterRecodeInformation(DeleteRegisterRecodeDTO deleteRegisterRecodeDTO, String userId) {
        //检查传进来的参数是否存在
        if (deleteRegisterRecodeDTO == null || deleteRegisterRecodeDTO.getIds() == null){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        String[] ids = deleteRegisterRecodeDTO.getIds().split(",");
        if(ids.length == 0){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //遍历判断所删除的签到记录是否能删除
        //创建一个集合存放存在的签到信息id
        List<String> signInRecordIdList = new ArrayList<>();
        //创建一个集合存放可以删除的签到记录id
        List<String> canDeleteSignInRecordIds = new ArrayList<>();
        for (String id : ids) {
            SignInRecord signInRecord = this.getById(id);
            if (signInRecord == null || signInRecord.getIsDelete().equals(DeleteConstant.YES)) {
                continue;
            }
            //判断signInRecordIdList集合中是否有数据
            if (signInRecordIdList.contains(signInRecord.getSignInInformationId())) {
                canDeleteSignInRecordIds.add(id);
                continue;
            }
            //签到信息没查过，查询一下
            SignInInformation signInInformation =
                    registerService.getById(signInRecord.getSignInInformationId());
            if (signInInformation == null || signInInformation.getIsDelete().equals(DeleteConstant.YES)) {
                continue;
            }
            //判断签到是否开始了，如果签到开始时间在当前时间之后，则可以删除
            if (signInInformation.getStartTime().isBefore(LocalDateTime.now())) {
                continue;
            }
            signInRecordIdList.add(signInRecord.getSignInInformationId());
            canDeleteSignInRecordIds.add(id);
        }
        if (canDeleteSignInRecordIds.isEmpty()) {
            throw new BaseException(MessageConstant.DELETE_FAIL);
        }
        //执行删除逻辑
        signInRecordMapper.deleteByIds(canDeleteSignInRecordIds);



    }

    /**
     * 编辑签到记录
     * @param updateRegisterRecordDTO
     * @param UserId
     */
    public void updateRegisterRecodeInformation(UpdateRegisterRecordDTO updateRegisterRecordDTO, String userId) {
        // 1. 根据传入的 ID 查询是否存在
        SignInRecord signIn = this.getById(updateRegisterRecordDTO.getId());
        if (signIn == null) {

            throw new BaseException("签到信息不存在，无法修改");
        }

        if(signIn.getIsDelete().equals(DeleteConstant.NO)){

            if (StringUtils.isNotBlank(updateRegisterRecordDTO.getNotes())) {
                signIn.setNotes(updateRegisterRecordDTO.getNotes());
            }


            // 3. 设置更新信息
            signIn.setUpdateBy(userId); // 或者从登录用户获取
            signIn.setUpdateTime(LocalDateTime.now());

            // 4. 执行更新
            boolean isUpdated = this.updateById(signIn);
            if (!isUpdated) {
                throw new BaseException("修改签到信息失败");
            }
        }


    }

    /**
     * 分页查询签到信息
     * @param dto
     * @return
     */
    public PageResult queryRegisterRecodeInformationList(FindRegisterSignDTO dto) {
        //先检查传进来的参数
        if (dto == null ) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //配置默认页码1和分页大小5
        if(dto.getPageNum() == null || dto.getPageNum() < 1){
            dto.setPageNum(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(dto.getPageSize() == null || dto.getPageSize() < 1){
            dto.setPageSize(PageConstant.PAGE_SIZE);
        }
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<SignInRecordPageVO> signInPageVOList = signInRecordMapper.querySignInRecordList(dto);
        PageInfo<SignInRecordPageVO> pageInfo = new PageInfo<>(signInPageVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 用户签到
     * @param id
     */
    public void userSign(String id) {
        //先检查传进来的参数
        if (id == null || id.isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //获取当前用户的id
        String userId = BaseContext.getCurrentId();
        //根据id查询签到信息
        SignInRecord signInRecord = signInRecordMapper.selectOne(new QueryWrapper<SignInRecord>()
                .eq("id", id)
                .eq("is_delete", DeleteConstant.NO)
        );
        if (signInRecord == null) {
            throw new BaseException(MessageConstant.SIGN_IN_RECORD_NOT_EXIST);
        }
        //判断查询的签到信息是否已经签到
        if (signInRecord.getState().equals(StatusConstant.ENABLE)) {
            throw new BaseException(MessageConstant.SIGN_IN_RECORD_ALREADY_SIGN_IN);
        }
        //不是的话直接修改状态为签到状态，并且记录修改人和时间
        signInRecord.setState(StatusConstant.ENABLE);
        signInRecord.setUpdateBy(userId);
        signInRecord.setUpdateTime(LocalDateTime.now());
        if(signInRecordMapper.updateById(signInRecord) >0){
            log.info("用户签到成功，userId={}", userId);
        }else{
            log.error("用户签到失败，userId={}", userId);
            throw new BaseException(MessageConstant.SIGN_IN_FAILED);
        }

    }

    /**
     * 修改用户状态
     * @param status
     * @param id
     */
    public void updateUserStatus(Integer status, String id) {
        //先检查传进来的参数
        if (id == null || id.isEmpty() || status == null ) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //检查传进来的转态是否合法，是否是1或者2
        if(!status.equals(StatusConstant.ENABLE) && !status.equals(StatusConstant.DISABLE)){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //查询当前签到记录是否存在
        SignInRecord signInRecord = signInRecordMapper.selectOne(new QueryWrapper<SignInRecord>()
                .eq("id", id)
                .eq("is_delete", DeleteConstant.NO)
        );
        if (signInRecord == null) {
            throw new BaseException(MessageConstant.SIGN_IN_RECORD_NOT_EXIST);
        }
        //核对记录里的状态是否和传入的状态一致
        if (signInRecord.getState().equals(status)) {
            throw new BaseException(MessageConstant.UPDATE_FAILED);
        }
        //修改状态，并且记录修改人和时间
        signInRecord.setState(status);
        signInRecord.setUpdateBy(BaseContext.getCurrentId());
        signInRecord.setUpdateTime(LocalDateTime.now());
        if(signInRecordMapper.updateById(signInRecord) >0){
            log.info("修改用户状态成功，userId={}", BaseContext.getCurrentId());
        }else{
            log.error("修改用户状态失败，userId={}", BaseContext.getCurrentId());
            throw new BaseException(MessageConstant.UPDATE_FAILED);
        }


    }

    /**
     * 查询签到信息记录
     * @param id
     * @return
     */
    public SignInRecordVO queryRegisterRecodeInformation(String id) {
        //先检查传进来的参数
        if (id == null || id.isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //根据id查询签到信息
        SignInRecordVO signInRecordVO = signInRecordMapper.queryRegisterRecodeInformation(id);
        if (signInRecordVO == null) {
            throw new BaseException(MessageConstant.SIGN_IN_RECORD_NOT_EXIST);
        }
        return signInRecordVO;
    }


}
