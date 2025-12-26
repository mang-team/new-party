package com.itmang.service.action.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.ActionConstant;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.PageConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.mapper.action.ActionMapper;
import com.itmang.mapper.action.ActionRecordMapper;
import com.itmang.mapper.activity.SignInRecordMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.action.AddActionRecordMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionRecordMessageDTO;
import com.itmang.pojo.dto.action.PageActionRecordMessageDTO;
import com.itmang.pojo.entity.*;
import com.itmang.pojo.vo.VoteInformationPageVO;
import com.itmang.pojo.vo.action.DetailActionRecordMessageVO;
import com.itmang.pojo.vo.action.PageActionRecordMessageVO;
import com.itmang.service.action.ActionRecordService;
import com.itmang.utils.IdGenerate;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ActionRecordServiceImpl extends ServiceImpl<ActionRecordMapper, ActionRecord>
        implements ActionRecordService {
    @Resource
    private ActionRecordMapper actionRecordMapper;
    @Resource
    private ActionMapper actionMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private IdGenerate idGenerate;

    /**
     * 分页查询活动记录表信息
     * @param pageActionRecordMessageDTO
     * @return
     */
    public PageResult pageGetActionRecordMessage(PageActionRecordMessageDTO pageActionRecordMessageDTO) {
        //配置默认页码1和分页大小5
        if(pageActionRecordMessageDTO.getPage() == null || pageActionRecordMessageDTO.getPage() < 1){
            pageActionRecordMessageDTO.setPage(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(pageActionRecordMessageDTO.getPageSize() == null || pageActionRecordMessageDTO.getPageSize() < 1){
            pageActionRecordMessageDTO.setPageSize(PageConstant.PAGE_SIZE);
        }
        // 使用PageHelper插件进行分页查询
        PageHelper.startPage(pageActionRecordMessageDTO.getPage(),pageActionRecordMessageDTO.getPageSize());
        // 将查询到的结果存储起来  注意已被删除活动信息 is_delete 为 1
        List<PageActionRecordMessageVO> page = actionRecordMapper.pageGetActionRecordMessage(pageActionRecordMessageDTO);
        PageInfo<PageActionRecordMessageVO> pageInfo = new PageInfo<>(page);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 报名接口
     * @param addActionRecordMessageDTO
     */
    public void addActionRecordMessage(AddActionRecordMessageDTO addActionRecordMessageDTO) {
        //判断传进来的参数是否合理
        if(addActionRecordMessageDTO == null || addActionRecordMessageDTO.getActionInformationId() == null
            || addActionRecordMessageDTO.getUserId() == null){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //查询活动记录是否存在
        ActionRecord actionRecord = actionRecordMapper.selectOne(new QueryWrapper<ActionRecord>()
                .eq("action_information_id",addActionRecordMessageDTO.getActionInformationId())
                .eq("user_id",addActionRecordMessageDTO.getUserId())
                .eq("is_delete", DeleteConstant.NO)
        );
        if(actionRecord != null){
            throw new BaseException(MessageConstant.ACTION_RECORD_EXIST);
        }
        //查询用户和活动信息是否存在
        //查询活动信息是否存在
        ActionInformation actionInformation = actionMapper.selectOne(new QueryWrapper<ActionInformation>()
                .eq("id",addActionRecordMessageDTO.getActionInformationId())
                .eq("is_delete",DeleteConstant.NO)
        );
        if(actionInformation == null){
            throw new BaseException(MessageConstant.ACTION_INFORMATION_NOT_EXIST);
        }
        //查询用户是否存在
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("id",addActionRecordMessageDTO.getUserId())
                .eq("is_delete",DeleteConstant.NO)
        );
        if(user == null){
            throw new BaseException(MessageConstant.USER_NOT_EXIST);
        }
        //新增活动记录表信息
        ActionRecord actionRecord1 = ActionRecord.builder()
                .id(idGenerate.nextUUID(actionRecord))
                .actionInformationId(addActionRecordMessageDTO.getActionInformationId())
                .userId(addActionRecordMessageDTO.getUserId())
                .state(ActionConstant.NOTSTART)
                .createBy(BaseContext.getCurrentId())
                .createTime(LocalDateTime.now())
                .build();
        actionRecordMapper.insert(actionRecord1);
    }

    /**
     * 根据活动记录id修改活动记录表信息
     * @param modifyActionRecordMessageDTO
     * @return
     */
    public void modifyActionRecordMessage(ModifyActionRecordMessageDTO modifyActionRecordMessageDTO) {
        // 判断id是否为空
        if(modifyActionRecordMessageDTO.getId() == null
                || modifyActionRecordMessageDTO.getNotes().isEmpty()){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //查询活动记录是否存在
        ActionRecord actionRecord = actionRecordMapper.selectOne(new QueryWrapper<ActionRecord>()
                .eq("id",modifyActionRecordMessageDTO.getId())
                .eq("is_delete",DeleteConstant.NO)
        );
        if(actionRecord == null){
            throw new BaseException(MessageConstant.ACTION_RECORD_NOT_EXIST);
        }
        //修改活动记录表信息
        actionRecord.setNotes(modifyActionRecordMessageDTO.getNotes());
        actionRecord.setUpdateBy(BaseContext.getCurrentId());
        actionRecord.setUpdateTime(LocalDateTime.now());
        actionRecordMapper.update(actionRecord,new QueryWrapper<ActionRecord>()
                .eq("id",modifyActionRecordMessageDTO.getId())
                .eq("is_delete",DeleteConstant.NO)
        );
    }

    /**
     * 批量删除活动记录
     * @param ids
     */

    public void deleteActionRecordMessage(String[] ids) {
        //判断参数是否为空
        if(ids == null || ids.length == 0){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //循环判断
        List<String> canDeleteIds = new ArrayList<>();
        List<String> continueIds = new ArrayList<>();
        List<String> noContinueIds = new ArrayList<>();
        for (String id : ids) {
            //判断活动记录是否存在
            ActionRecord actionRecord = actionRecordMapper.selectOne(new QueryWrapper<ActionRecord>()
                    .eq("id",id)
                    .eq("is_delete",DeleteConstant.NO)
            );
            if(actionRecord == null){
                continue;
            }
            //判断活动信息id是否在continueIds中
            if(continueIds.contains(actionRecord.getActionInformationId())){
                continue;
            }
            if(noContinueIds.contains(actionRecord.getActionInformationId())){
                canDeleteIds.add(id);
                continue;
            }
            //查询该活动记录的报名信息中活动的开始时间是否在当前时间之后
            if(actionRecord.getActionInformationId() != null) {
                ActionInformation actionInformation = actionMapper.selectOne(new QueryWrapper<ActionInformation>()
                        .eq("id", actionRecord.getActionInformationId())
                        .eq("is_delete", DeleteConstant.NO)
                );
                if (actionInformation != null) {
                    if (actionInformation.getActionStartTime().isBefore(LocalDateTime.now())) {
                        continueIds.add(id);
                        continue;
                    }
                }
            }
            canDeleteIds.add(id);
            noContinueIds.add(actionRecord.getActionInformationId());
        }
        if(canDeleteIds.isEmpty()){
            throw new BaseException(MessageConstant.DELETE_FAIL);
        }
        // 调用mapper删除
        actionRecordMapper.deleteActionRecordMessage(canDeleteIds);
    }

    /**
     * 根据活动记录id查询活动记录表信息
     * @param id
     * @return
     */
    public DetailActionRecordMessageVO getActionRecordMessageById(String id) {
        //判断参数是否为空
        if(id == null || id.isEmpty()){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //直接查询
        DetailActionRecordMessageVO detailActionRecordMessageVO =
                actionRecordMapper.getActionRecordMessageById(id);
        if(detailActionRecordMessageVO == null){
            throw new BaseException(MessageConstant.ACTION_RECORD_NOT_EXIST);
        }
        return detailActionRecordMessageVO;
    }


}
