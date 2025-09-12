package com.itmang.service.action;

import com.itmang.pojo.dto.action.AddActionMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionMessageDTO;
import com.itmang.pojo.dto.action.PageDetailActionMessageDTO;
import com.itmang.pojo.entity.PageResult;


public interface ActionService {
    /**
     * 分页查询详细活动信息
     * @param actionMessageDTO
     * @return
     */
    PageResult pageGetDetailActionMessage(PageDetailActionMessageDTO actionMessageDTO);

    /**
     * 分页查询简略活动信息
     * @param actionMessageDTO
     * @return
     */
    PageResult pageGetShortActionMessage(PageDetailActionMessageDTO actionMessageDTO);

    /**
     * 根据活动id修改活动信息
     * @param modifyActionMessageDTO
     */
    boolean modifyActionMessage(ModifyActionMessageDTO modifyActionMessageDTO);

    /**
     * 新增活动信息
     * @param addActionMessageDTO
     * @return
     */
    void addActionMessage(AddActionMessageDTO addActionMessageDTO);
}
