package com.itmang.service.action;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.action.AddActionMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionMessageDTO;
import com.itmang.pojo.dto.action.PageDetailActionMessageDTO;
import com.itmang.pojo.entity.ActionInformation;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.SignInRecord;
import com.itmang.pojo.vo.action.DetailActionMessageVO;

import java.util.List;


public interface ActionService extends IService<ActionInformation> {

//    /**
//     * 分页查询简略活动信息
//     * @param actionMessageDTO
//     * @return
//     */
//    PageResult pageGetShortActionMessage(PageDetailActionMessageDTO actionMessageDTO);

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
     * @param ids
     */
    void deleteActionMessage(String[] ids);

    /**
     * 根据活动id查询详细活动信息
     * @param id
     * @return
     */
    DetailActionMessageVO GetDetailActionMessage(String id);

    /**
     * 分页查询活动信息
     * @param actionMessageDTO
     * @return
     */
    PageResult pageGetActionMessage(PageDetailActionMessageDTO actionMessageDTO);
}
