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
        PageHelper.startPage(actionMessageDTO.getPage(), actionMessageDTO.getPageSize());

        Page<PageDetailActionMessageVO> page = actionMapper.pageGetDetailActionMessage(actionMessageDTO);

        Long total = page.getTotal();
        List<PageDetailActionMessageVO> records = page.getResult();

        return new PageResult(total,records);
    }

    /**
     * 分页查询简略活动信息
     * @param actionMessageDTO
     * @return
     */
    @Override
    public PageResult pageGetShortActionMessage(PageDetailActionMessageDTO actionMessageDTO) {
        PageHelper.startPage(actionMessageDTO.getPage(), actionMessageDTO.getPageSize());

        Page<pageShortActionMessageVO> page = actionMapper.pageGetShortActionMessage(actionMessageDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据活动id修改活动信息
     * @param modifyActionMessageDTO
     */
    @Override
    public boolean modifyActionMessage(ModifyActionMessageDTO modifyActionMessageDTO) {
        int modify = actionMapper.modifyActionMessage(modifyActionMessageDTO);
        return modify > 0 ? true : false;
    }

    /**
     * 新增活动信息
     * @param addActionMessageDTO
     * @return
     */
    @Override
    public void addActionMessage(AddActionMessageDTO addActionMessageDTO) {
        addActionMessageDTO.setUserId(BaseContext.getCurrentId());
        actionMapper.addActionMessage(addActionMessageDTO);
    }
}
