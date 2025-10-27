package com.itmang.mapper.action;

import com.github.pagehelper.Page;
import com.itmang.annotation.AutoFill;
import com.itmang.enumeration.OperationType;
import com.itmang.pojo.dto.action.AddActionMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionMessageDTO;
import com.itmang.pojo.dto.action.PageDetailActionMessageDTO;
import com.itmang.pojo.vo.action.DetailActionMessageVO;
import com.itmang.pojo.vo.action.PageShortActionMessageVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ActionMapper {
    /**
     * 根据活动信息id查询详细活动信息
     * @param id
     * @return
     */
    @Select("select * from action_information where id = #{id}")
    DetailActionMessageVO getDetailActionMessage(String id);

    /**
     * 分页查询简略活动信息
     * @param actionMessageDTO
     * @return
     */
    Page<PageShortActionMessageVO> pageGetShortActionMessage(PageDetailActionMessageDTO actionMessageDTO);

    /**
     * 根据活动id修改活动信息
     * @param modifyActionMessageDTO
     * @return
     */
    @AutoFill(value = OperationType.UPDATE)
    int modifyActionMessage(ModifyActionMessageDTO modifyActionMessageDTO);

    /**
     * 新增活动信息
     * @param addActionMessageDTO
     */
    @Insert("insert into action_information(id,action_name,activity_content,user_id,organizer_phone,sign_up_start_time,sign_up_end_time,action_start_time,action_end_time,event_address,create_by,create_time,update_by,update_time)" +
            "values (#{id},#{actionName},#{activityContent},#{userId},#{organizerPhone},#{signUpStartTime},#{signUpEndTime},#{actionStartTime},#{actionEndTime},#{eventAddress},#{createBy},#{createTime},#{updateBy},#{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void addActionMessage(AddActionMessageDTO addActionMessageDTO);

    /**
     * 根据活动id批量删除活动信息
     *
     * @param idList
     * @param now
     * @param currentId
     */
    void deleteActionMessage(List<String> idList, LocalDateTime now, String currentId);


}
