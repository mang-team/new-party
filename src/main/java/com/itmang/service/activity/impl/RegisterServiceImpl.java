package com.itmang.service.activity.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.PageConstant;
import com.itmang.exception.BaseException;


import com.itmang.mapper.activity.SignInInformationMapper;
import com.itmang.mapper.activity.SignInRecordMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.AddRegisterDTO;
import com.itmang.pojo.dto.DeleteRegisterDTO;
import com.itmang.pojo.dto.FindRegisterDTO;
import com.itmang.pojo.dto.UpdateRegisterDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.SignInInformation;
import com.itmang.pojo.entity.SignInRecord;
import com.itmang.pojo.vo.SignInInformationPageVO;
import com.itmang.pojo.vo.SignInInformationVO;
import com.itmang.pojo.vo.VoteInformationPageVO;
import com.itmang.service.activity.RegisterService;
import com.itmang.utils.IdGenerate;
import com.itmang.utils.UserUtil;
import com.itmang.websocket.WebSocketMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegisterServiceImpl extends ServiceImpl<SignInInformationMapper, SignInInformation>
        implements RegisterService {

    @Autowired
    private WebSocketMessageService webSocketMessageService;
    @Resource
    private SignInRecordMapper signInRecordMapper;
    @Resource
    private IdGenerate  idGenerate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private SignInInformationMapper signInInformationMapper;

    /**
     * 新增签到信息
     * @param addRegisterDTOList
     * @param UserId
     */
    public void addRegisterInformation(List<AddRegisterDTO> addRegisterDTOList, String UserId) {
        String createBy = UserId;
        if (addRegisterDTOList == null || addRegisterDTOList.isEmpty()) {
            log.warn("新增签到信息列表为空");
            return;
        }

        List<SignInInformation> signInInformationList = addRegisterDTOList.stream().map(dto -> {
            SignInInformation info = new SignInInformation();
            info.setId(idGenerate.nextUUID(SignInInformation.class)); // 主键
            info.setSignInTitle(dto.getSignInTitle());
            info.setSignInContent(dto.getSignInContent());
            List<String> userIds = UserUtil.processValidUserIds(dto :: getUserIds , userMapper);
            info.setUserIds(String.join(",", userIds));
            // 转换时间字符串为 LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (dto.getStartTime() != null && !dto.getStartTime().isEmpty()) {
                info.setStartTime(LocalDateTime.parse(dto.getStartTime(), formatter));
            }
            if (dto.getEndTime() != null && !dto.getEndTime().isEmpty()) {
                info.setEndTime(LocalDateTime.parse(dto.getEndTime(), formatter));
            }
            info.setCreateBy(createBy);
            info.setCreateTime(LocalDateTime.now());
            info.setUpdateBy(createBy);
            info.setUpdateTime(LocalDateTime.now());
            info.setIsDelete(DeleteConstant.NO); // 默认未删除
            return info;
        }).toList();

        // 批量插入
        boolean saved = this.saveBatch(signInInformationList);

        if (!saved) {


            throw new BaseException("新增签到信息失败");

        }

        for (SignInInformation signInInfo : signInInformationList) {
            // 获取参与签到的用户并发送个性化消息
            LambdaQueryWrapper<SignInRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SignInRecord::getSignInInformationId, signInInfo.getId());
            List<SignInRecord> signInRecordList = signInRecordMapper.selectList(queryWrapper);
            List<String> userIds = signInRecordList
                    .stream()
                    .map(SignInRecord::getUserId).collect(Collectors.toList());
            if (!userIds.isEmpty()) {
                // 创建签到VO对象
                SignInInformationVO signInVO = SignInInformationVO.builder()
                        .id(signInInfo.getId())
                        .signInTitle(signInInfo.getSignInTitle())
                        .signInContent(signInInfo.getSignInContent())
                        .startTime(signInInfo.getStartTime())
                        .endTime(signInInfo.getEndTime())
                        .build();

                // 发送签到VO对象给用户
                webSocketMessageService.sendSignInNotificationToUsers(userIds, signInVO);
            }
        }

        log.info("新增签到信息成功，数量：{}，已发送WebSocket通知", signInInformationList.size());
    }

    /**
     * 删除签到信息
     * @param deleteRegisterDTO
     * @param UserId
     */
    public void deleteRegisterInformation(DeleteRegisterDTO deleteRegisterDTO, String UserId) {
        if (deleteRegisterDTO.getId() == null) {
            throw new IllegalArgumentException(MessageConstant.PARAMETER_ERROR);
        }
        // 查询签到信息
        //将id字符串转为数组
        String[] ids = deleteRegisterDTO.getId().split(",");
        //将可以删除的签到信息都放入新的集合
        List<SignInInformation> canDeleteSignInInformationList = new ArrayList<>();
        for (String id : ids) {
            SignInInformation info = getById(id);
            //判断是否可以删除
            if (info == null && info.getIsDelete().equals(DeleteConstant.YES)) {
                continue;
            }
            //判断是否开始
            if (info.getStartTime().isBefore(LocalDateTime.now())) {
                continue;
            }
            //修改逻辑删除状态
            info.setIsDelete(DeleteConstant.YES);
            info.setUpdateTime(LocalDateTime.now());
            info.setUpdateBy(UserId);
            canDeleteSignInInformationList.add(info);
        }
        // 执行批量删除
        boolean isDeleted = this.updateBatchById(canDeleteSignInInformationList);
        if (!isDeleted) {
            throw new BaseException(MessageConstant.DELETE_FAIL);
        }
    }

    /**
     * 编辑签到信息
     * @param updateRegisterDTO
     * @param UserId
     */
    public void updateRegisterInformation(UpdateRegisterDTO updateRegisterDTO, String UserId) {
        // 1. 根据传入的 ID 查询是否存在
        SignInInformation signIn = this.getById(updateRegisterDTO.getId());
        if (signIn == null) {
            throw new BaseException("签到信息不存在，无法修改");
        }
        if (signIn.getIsDelete() == 2) {
            //判断签到是否开始
            if (signIn.getStartTime().isBefore(LocalDateTime.now())) {
                throw new BaseException("签到已开始，无法修改");
            }
            // 2. 将 DTO 的字段更新到实体类中
            if (StringUtils.isNotBlank(updateRegisterDTO.getSignInTitle())) {
                signIn.setSignInTitle(updateRegisterDTO.getSignInTitle());
            }

            if (StringUtils.isNotBlank(updateRegisterDTO.getSignInContent())) {
                signIn.setSignInContent(updateRegisterDTO.getSignInContent());
            }

            if (updateRegisterDTO.getStartTime() != null) {
                signIn.setStartTime(updateRegisterDTO.getStartTime());
            }

            if (updateRegisterDTO.getEndTime() != null) {
                signIn.setEndTime(updateRegisterDTO.getEndTime());
            }

            // 3. 设置更新信息
            signIn.setUpdateBy(UserId); // 或者从登录用户获取
            signIn.setUpdateTime(LocalDateTime.now());

            // 4. 执行更新
            boolean isUpdated = this.updateById(signIn);
            if (!isUpdated) {
                throw new BaseException("修改签到信息失败");
            }
        }


    }

    //新加逻辑删除判断
    /**
     * 分页查询签到信息
     * @param dto
     * @return
     */
    public PageResult queryRegisterInformationList(FindRegisterDTO dto) {
        //配置默认页码1和分页大小5
        if(dto.getPageNum() == null || dto.getPageNum() < 1){
            dto.setPageNum(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(dto.getPageSize() == null || dto.getPageSize() < 1){
            dto.setPageSize(PageConstant.PAGE_SIZE);
        }
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<SignInInformationPageVO> signInPageVOList = signInInformationMapper
                .querySignInInformationList(dto);
        PageInfo<SignInInformationPageVO> pageInfo = new PageInfo<>(signInPageVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}

