package com.itmang.service.action;

import com.itmang.pojo.dto.action.AddActionMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionMessageDTO;
import com.itmang.pojo.dto.action.PageDetailActionMessageDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.vo.action.DetailActionMessageVO;

import java.util.List;


public interface ActionService {
    /**
     * 根据活动信息id查询详细活动信息
     * @param id
     * @return
     */
    DetailActionMessageVO getDetailActionMessage(String id);

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

    /**
     * 根据活动id批量删除活动信息
     * @param idList
     */
    void deleteActionMessage(List<String> idList);

    /**
     * 修改活动状态
     * @param id
     * @param state
     * @return
     */
    boolean modifyState(String id, int state);
}
