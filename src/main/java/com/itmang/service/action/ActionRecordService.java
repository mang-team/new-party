package com.itmang.service.action;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.action.AddActionRecordMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionRecordMessageDTO;
import com.itmang.pojo.dto.action.PageActionRecordMessageDTO;
import com.itmang.pojo.entity.ActionRecord;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.SignInRecord;
import com.itmang.pojo.vo.action.DetailActionRecordMessageVO;

import java.util.List;

public interface ActionRecordService extends IService<ActionRecord> {
    /**
     * 分页查询活动记录表信息
     * @param pageActionRecordMessageDTO
     * @return
     */
    PageResult pageGetActionRecordMessage(PageActionRecordMessageDTO pageActionRecordMessageDTO);

    /**
     * 报名接口
     * @param addActionRecordMessageDTO
     */
    void addActionRecordMessage(AddActionRecordMessageDTO addActionRecordMessageDTO);

    /**
     * 修改活动记录表信息
     * @param modifyActionRecordMessageDTO
     * @return
     */
    void modifyActionRecordMessage(ModifyActionRecordMessageDTO modifyActionRecordMessageDTO);

    /**
     * 根据活动记录id批量删除活动记录
     * @param ids
     */
    void deleteActionRecordMessage(String[] ids);

    /**
     * 根据活动记录id查询活动记录表信息
     * @param id
     * @return
     */
    DetailActionRecordMessageVO getActionRecordMessageById(String id);
}
