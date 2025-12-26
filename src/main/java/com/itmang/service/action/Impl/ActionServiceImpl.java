package com.itmang.service.action.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.PageConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.mapper.action.ActionMapper;
import com.itmang.mapper.action.ActionRecordMapper;
import com.itmang.mapper.activity.SignInRecordMapper;
import com.itmang.pojo.dto.action.AddActionMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionMessageDTO;
import com.itmang.pojo.dto.action.PageDetailActionMessageDTO;
import com.itmang.pojo.entity.ActionInformation;
import com.itmang.pojo.entity.ActionRecord;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.SignInRecord;
import com.itmang.pojo.vo.action.DetailActionMessageVO;
import com.itmang.pojo.vo.action.pageShortActionMessageVO;
import com.itmang.service.action.ActionService;
import com.itmang.utils.IdGenerate;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActionServiceImpl extends ServiceImpl<ActionMapper, ActionInformation>
        implements ActionService {

    @Resource
    private ActionMapper actionMapper;
    @Resource
    private ActionRecordMapper actionRecordMapper;
    @Resource
    private IdGenerate idGenerate;




    /**
     * 根据活动id修改活动信息
     * @param modifyActionMessageDTO
     */
    @Override
    public boolean modifyActionMessage(ModifyActionMessageDTO modifyActionMessageDTO) {
        // 判断id是否为空
        if(modifyActionMessageDTO.getId() == null){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //查询当前活动信息是否存在
        ActionInformation actionInformation = actionMapper.selectOne(new QueryWrapper<ActionInformation>()
                .eq("id",modifyActionMessageDTO.getId())
                .eq("is_delete", DeleteConstant.NO)
        );
        if(actionInformation == null){
            throw new BaseException("活动信息不存在");
        }
        // 判断活动开始时间是否在当前时间之前
        if(actionInformation.getActionStartTime().isBefore(LocalDateTime.now())){
            throw new BaseException("活动开始时间不能早于当前时间");
        }
        // 根据活动id修改活动信息，查看返回值modify是否为0，从而判断是否成功修改
        ActionInformation updateActionInformation = new ActionInformation();
        BeanUtil.copyProperties(modifyActionMessageDTO,updateActionInformation);
        updateActionInformation.setUpdateTime(LocalDateTime.now());
        updateActionInformation.setUpdateBy(BaseContext.getCurrentId());
        int modify = actionMapper.modifyActionMessage(updateActionInformation);
        return modify > 0;
    }

    /**
     * 新增活动信息
     * @param addActionMessageDTO
     * @return
     */
    @Override
    public void addActionMessage(AddActionMessageDTO addActionMessageDTO) {
        // 判断传进来的参数
        if(addActionMessageDTO == null){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        ActionInformation actionInformation = new ActionInformation();
        if(addActionMessageDTO.getActionName() == null || addActionMessageDTO.getActionName().equals("")){
            throw new BaseException("活动名称不能为空");
        }
        if(addActionMessageDTO.getSignUpStartTime() == null  || addActionMessageDTO.getSignUpEndTime() == null || addActionMessageDTO.getSignUpStartTime().toString().equals("") || addActionMessageDTO.getSignUpEndTime().toString().equals("")){
            throw new BaseException("活动报名时间不能为空");
        }
        if(addActionMessageDTO.getActionStartTime() == null  || addActionMessageDTO.getActionEndTime() == null || addActionMessageDTO.getActionStartTime().toString().equals("") || addActionMessageDTO.getActionEndTime().toString().equals("")){
            throw new BaseException("活动时间不能为空");
        }
        if(addActionMessageDTO.getOrganizerPhone() == null || addActionMessageDTO.getOrganizerPhone().equals("")){
            throw new BaseException("组织者电话不能为空");
        }
        if(addActionMessageDTO.getEventAddress() == null || addActionMessageDTO.getEventAddress().equals("")){
            throw new BaseException("活动地点不能为空");
        }
        if(addActionMessageDTO.getRecruitmentQuantity() == null){
            throw new BaseException("招募人数不能为空");
        }
        BeanUtil.copyProperties(addActionMessageDTO,actionInformation);
        // 生成UUID作为主键
        actionInformation.setId(idGenerate.nextUUID(actionInformation));
        actionInformation.setCreateTime(LocalDateTime.now());
        actionInformation.setCreateBy(BaseContext.getCurrentId());
        actionInformation.setParticipantQuantity(0);
        actionInformation.setIsDelete(DeleteConstant.NO);
        //插入
        actionMapper.insert(actionInformation);
    }

    /**
     * 根据活动id批量删除活动信息
     * @param ids
     */

    public void deleteActionMessage(String[] ids) {
        //判断当前的参数
        if(ids == null || ids.length == 0){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        List<String> canDeleteIds = new ArrayList<>();
        //循环判断信息
        for (String id : ids) {
            //查询要删除的活动信息是否存在
            ActionInformation actionInformation = actionMapper.selectOne(new QueryWrapper<ActionInformation>()
                    .eq("id",id)
                    .eq("is_delete",DeleteConstant.NO)
            );
            if(actionInformation == null){
                continue;
            }
            //判断活动的开始时间是否在当前时间之前
            if(actionInformation.getActionStartTime().isBefore(LocalDateTime.now())){
                continue;
            }
            //判断活动是否在报名开始
            if(actionInformation.getSignUpStartTime().isBefore(LocalDateTime.now())){
                //判断是否有人报名
                if(actionInformation.getParticipantQuantity() > 0){
                    //将报名的人的活动记录删除
                    //根据活动信息的id查询所有关于这活动记录的 id
                    List<String> actionRecordIds =
                            actionRecordMapper.selectList(new QueryWrapper<ActionRecord>()
                                    .eq("action_id",actionInformation.getId())
                            ).stream().map(ActionRecord::getId).collect(Collectors.toList());
                    //根据id批量删除活动记录
                    actionRecordMapper.deleteActionRecordMessage(actionRecordIds);
                }
            }
            //将可以删除的id添加到集合中
            canDeleteIds.add(id);
        }

        //判断集合是否为空
        if(canDeleteIds.size() > 0){
            //批量删除活动信息
            actionMapper.deleteActionMessage(canDeleteIds);
        }
        if(canDeleteIds.size() <= 0){
            new BaseException(MessageConstant.DELETE_FAIL);
        }
    }




//    @Override
//    public PageResult pageGetDetailActionMessage(PageDetailActionMessageDTO actionMessageDTO) {
//        if(actionMessageDTO.getPage() == null || actionMessageDTO.getPage() < 1){
//            actionMessageDTO.setPage(PageConstant.PAGE_NUM);
//        }
//        //查看是否有分页大小
//        if(actionMessageDTO.getPageSize() == null || actionMessageDTO.getPageSize() < 1){
//            actionMessageDTO.setPageSize(PageConstant.PAGE_SIZE);
//        }
//        // 使用PageHelper插件进行分页查询
//        PageHelper.startPage(actionMessageDTO.getPage(), actionMessageDTO.getPageSize());
//
//        // 将查询到的结果存储起来  注意已被删除活动信息 is_delete 为 1
//        Page<DetailActionMessageVO> page = actionMapper.pageGetDetailActionMessage(actionMessageDTO);
//
//        // 得到总个数和具体内容
//        Long total = page.getTotal();
//        List<DetailActionMessageVO> records = page.getResult();
//
//        // 返回结果
//        return new PageResult(total,records);
//    }



    /**
     * 根据活动id查询详细活动信息
     * @param id
     * @return
     */
    public DetailActionMessageVO GetDetailActionMessage(String id) {
        //判断传进来的id
        if(id == null || id.equals("")){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        // 根据id查询活动信息
        DetailActionMessageVO detailActionMessageVO = actionMapper.GetDetailActionMessage(id);
        if (detailActionMessageVO == null){
            throw new BaseException(MessageConstant.ACTION_NOT_FOUND);
        }
        return detailActionMessageVO;
    }



//    /**
//     * 分页查询简略活动信息
//     * @param actionMessageDTO
//     * @return
//     */
//    public PageResult pageGetShortActionMessage(PageDetailActionMessageDTO actionMessageDTO) {
//        //配置默认页码1和分页大小5
//        if(actionMessageDTO.getPage() == null || actionMessageDTO.getPage() < 1){
//            actionMessageDTO.setPage(PageConstant.PAGE_NUM);
//        }
//        //查看是否有分页大小
//        if(actionMessageDTO.getPageSize() == null || actionMessageDTO.getPageSize() < 1){
//            actionMessageDTO.setPageSize(PageConstant.PAGE_SIZE);
//        }
//        // 使用PageHelper插件进行分页查询
//        PageHelper.startPage(actionMessageDTO.getPage(), actionMessageDTO.getPageSize());
//        // 将查询到的结果存储起来  注意已被删除活动信息 is_delete 为 1
//        Page<pageShortActionMessageVO> page = actionMapper.pageGetShortActionMessage(actionMessageDTO);
//
//        // 返回结果
//        return new PageResult(page.getTotal(),page.getResult());
//    }


    /**
     * 分页查询活动信息
     * @param actionMessageDTO
     * @return
     */
    public PageResult pageGetActionMessage(PageDetailActionMessageDTO actionMessageDTO) {
        //配置默认页码1和分页大小5
        if(actionMessageDTO.getPage() == null || actionMessageDTO.getPage() < 1){
            actionMessageDTO.setPage(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(actionMessageDTO.getPageSize() == null || actionMessageDTO.getPageSize() < 1){
            actionMessageDTO.setPageSize(PageConstant.PAGE_SIZE);
        }
        // 使用PageHelper插件进行分页查询
        PageHelper.startPage(actionMessageDTO.getPage(), actionMessageDTO.getPageSize());
        // 将查询到的结果存储起来  注意已被删除活动信息 is_delete 为 1
        Page<pageShortActionMessageVO> page = actionMapper.pageGetShortActionMessage(actionMessageDTO);

        // 返回结果
        return new PageResult(page.getTotal(),page.getResult());
    }
}
