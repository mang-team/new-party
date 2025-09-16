package com.itmang.service.action.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.itmang.context.BaseContext;
import com.itmang.controller.BaseController;
import com.itmang.mapper.action.ActionMapper;
import com.itmang.pojo.dto.action.AddActionMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionMessageDTO;
import com.itmang.pojo.dto.action.PageDetailActionMessageDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.vo.action.PageDetailActionMessageVO;
import com.itmang.pojo.vo.action.pageShortActionMessageVO;
import com.itmang.service.action.ActionService;
import com.itmang.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionMapper actionMapper;

    /**
     * 分页查询详细活动信息
     * @param actionMessageDTO
     * @return
     */
    @Override
    public PageResult pageGetDetailActionMessage(PageDetailActionMessageDTO actionMessageDTO) {
        // 使用PageHelper插件进行分页查询
        PageHelper.startPage(actionMessageDTO.getPage(), actionMessageDTO.getPageSize());

        // 将查询到的结果存储起来  注意已被删除活动信息 is_delete 为 1
        Page<PageDetailActionMessageVO> page = actionMapper.pageGetDetailActionMessage(actionMessageDTO);

        // 得到总个数和具体内容
        Long total = page.getTotal();
        List<PageDetailActionMessageVO> records = page.getResult();

        // 返回结果
        return new PageResult(total,records);
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
        Page<pageShortActionMessageVO> page = actionMapper.pageGetShortActionMessage(actionMessageDTO);

        // 返回结果
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据活动id修改活动信息
     * @param modifyActionMessageDTO
     */
    @Override
    public boolean modifyActionMessage(ModifyActionMessageDTO modifyActionMessageDTO) {
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
        // 更新时间
        LocalDateTime now = LocalDateTime.now();
        // 时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式转换
        LocalDateTime dateTime = LocalDateTime.parse(now.format(formatter), formatter);
        // 调用mapper删除
        actionMapper.deleteActionMessage(idList, dateTime,BaseContext.getCurrentId());
    }
}
