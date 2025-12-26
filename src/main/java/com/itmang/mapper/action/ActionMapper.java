package com.itmang.mapper.action;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.itmang.annotation.AutoFill;
import com.itmang.enumeration.OperationType;
import com.itmang.pojo.dto.action.AddActionMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionMessageDTO;
import com.itmang.pojo.dto.action.PageDetailActionMessageDTO;
import com.itmang.pojo.entity.ActionInformation;
import com.itmang.pojo.entity.SignInInformation;
import com.itmang.pojo.vo.action.DetailActionMessageVO;
import com.itmang.pojo.vo.action.pageShortActionMessageVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ActionMapper extends BaseMapper<ActionInformation> {

    /**
     * 分页查询活动信息
     * @param actionMessageDTO
     * @return
     */
    Page<pageShortActionMessageVO> pageGetShortActionMessage(PageDetailActionMessageDTO actionMessageDTO);

    /**
     * 根据活动id修改活动信息
     * @param modifyActionMessageDTO
     * @return
     */
    @AutoFill(value = OperationType.UPDATE)
    int modifyActionMessage(ActionInformation modifyActionMessageDTO);

//    /**
//     * 新增活动信息
//     * @param addActionMessageDTO
//     */
//    @Insert("insert into action_information(id,action_name,activity_content,user_id,organizer_phone,sign_up_start_time,sign_up_end_time,action_start_time,action_end_time,event_address,create_by,create_time,update_by,update_time)" +
//            "values (#{id},#{actionName},#{activityContent},#{userId},#{organizerPhone},#{signUpStartTime},#{signUpEndTime},#{actionStartTime},#{actionEndTime},#{eventAddress},#{createBy},#{createTime},#{updateBy},#{updateTime})")
//    @AutoFill(value = OperationType.INSERT)
//    void addActionMessage(AddActionMessageDTO addActionMessageDTO);

    /**
     * 根据活动id批量删除活动信息
     *
     * @param idList
     */
    void deleteActionMessage(List<String> idList);

    /**
     * 根据活动id查询详细活动信息
     * @param id
     * @return
     */
    DetailActionMessageVO GetDetailActionMessage(String id);
}
