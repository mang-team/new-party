package com.itmang.service.action;

import com.itmang.pojo.dto.action.AddActionRecordMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionRecordMessageDTO;
import com.itmang.pojo.dto.action.PageActionRecordMessageDTO;
import com.itmang.pojo.entity.PageResult;

import java.util.List;

public interface ActionRecordService {
    /**
     * 分页查询活动记录表信息
     * @param pageActionRecordMessageDTO
     * @return
     */
    PageResult pageGetActionRecordMessage(PageActionRecordMessageDTO pageActionRecordMessageDTO);

    /**
     * 新增活动记录信息
     * @param addActionRecordMessageDTO
     */
    void addActionRecordMessage(AddActionRecordMessageDTO addActionRecordMessageDTO);

    /**
     * 修改活动记录表信息
     * @param modifyActionRecordMessageDTO
     * @return
     */
    boolean modifyActionRecordMessage(ModifyActionRecordMessageDTO modifyActionRecordMessageDTO);

    /**
     * 根据活动记录id批量删除活动记录
     * @param idList
     */
    void deleteActionRecordMessage(List<String> idList);
}
