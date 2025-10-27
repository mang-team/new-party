package com.itmang.service.action.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.mapper.action.ActionMapper;
import com.itmang.mapper.action.ActionRecordMapper;
import com.itmang.pojo.dto.action.AddActionMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionMessageDTO;
import com.itmang.pojo.dto.action.PageDetailActionMessageDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.vo.action.DetailActionMessageVO;
import com.itmang.pojo.vo.action.PageShortActionMessageVO;
import com.itmang.service.action.ActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionMapper actionMapper;

    @Autowired
    private ActionRecordMapper actionRecordMapper;

    /**
     * 根据活动信息id查询详细活动信息
     * @param id
     * @return
     */
    @Override
    public DetailActionMessageVO getDetailActionMessage(String id) {
        // 判断id是否为空或长度为0
        if(id == null || id.isEmpty()){
            throw new BaseException("活动信息查询id为空");
        }
        // 根据id获取活动详细信息
        DetailActionMessageVO detailActionMessageVO = actionMapper.getDetailActionMessage(id);
        // 判断当前活动是否被删除
        if(detailActionMessageVO.getIsDelete() == 1){
            throw new BaseException("该活动信息已被删除");
        }
        // 查询参加活动的用户id
        List<String> list = actionRecordMapper.getJoinActionIdList(id);
        // 添加到返回对象中
        detailActionMessageVO.setUserList(list);
        return detailActionMessageVO;
    }

    /**
     * 分页查询简略活动信息
     * @param actionMessageDTO
     * @return
     */
    @Override
    public PageResult pageGetShortActionMessage(PageDetailActionMessageDTO actionMessageDTO) {
        // 使用PageHelper插件进行分页查询
        PageHelper.startPage(actionMessageDTO.getPage(), actionMessageDTO.getPageSize());

        // 将查询到的结果存储起来  注意已被删除活动信息 is_delete 为 1
        Page<PageShortActionMessageVO> page = actionMapper.pageGetShortActionMessage(actionMessageDTO);

        // 返回结果
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据活动id修改活动信息
     * @param modifyActionMessageDTO
     */
    @Override
    public boolean modifyActionMessage(ModifyActionMessageDTO modifyActionMessageDTO) {
        // 判断id是否为空
        if(modifyActionMessageDTO.getId() == null){
            throw new BaseException("活动信息 id 不能为空");
        }
        // 根据活动id修改活动信息，查看返回值modify是否为0，从而判断是否成功修改
        int modify = actionMapper.modifyActionMessage(modifyActionMessageDTO);
        return modify > 0;
    }

    /**
     * 新增活动信息
     * @param addActionMessageDTO
     * @return
     */
    @Override
    public void addActionMessage(AddActionMessageDTO addActionMessageDTO) {
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
        // 生成UUID作为主键
        addActionMessageDTO.setId(UUID.randomUUID().toString());
        // 获取并添加userId
        addActionMessageDTO.setUserId(BaseContext.getCurrentId());
        // 调用mapper函数添加活动信息
        actionMapper.addActionMessage(addActionMessageDTO);
    }

    /**
     * 根据活动id批量删除活动信息
     * @param idList
     */
    @Override
    public void deleteActionMessage(List<String> idList) {
        // 判断列表是否为空或不存在
        if(idList == null || idList.size() <= 0){
            throw new BaseException("活动信息 id 列表为空");
        }
        // 更新时间
        LocalDateTime now = LocalDateTime.now();
        // 时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式转换
        LocalDateTime dateTime = LocalDateTime.parse(now.format(formatter), formatter);
        // 调用mapper删除
        actionMapper.deleteActionMessage(idList, dateTime,BaseContext.getCurrentId());
    }

    /**
     * 修改活动状态
     * @param id
     * @param state
     * @return
     */
    @Override
    public boolean modifyState(String id, int state) {
        // 判断id是否为空
        if(id == null || id.isEmpty()){
            throw new BaseException("活动id不能为空");
        }
        // 判断状态是否超出范围 1 未开始，2 进行中，3 已结束
        if(state < 1 || state > 3){
            throw new BaseException("活动状态不存在");
        }

        // 根据活动id修改活动状态，查看返回值modify是否为0，从而判断是否成功修改
        int modify = actionRecordMapper.modifyState(id,state);

        return modify > 0;
    }
}
