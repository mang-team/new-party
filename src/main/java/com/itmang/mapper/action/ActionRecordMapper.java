package com.itmang.mapper.action;

import com.github.pagehelper.Page;
import com.itmang.annotation.AutoFill;
import com.itmang.enumeration.OperationType;
import com.itmang.pojo.dto.action.AddActionRecordMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionRecordMessageDTO;
import com.itmang.pojo.dto.action.PageActionRecordMessageDTO;
import com.itmang.pojo.vo.action.PageActionRecordMessageVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ActionRecordMapper {
    /**
     * 分页查询活动记录信息
     * @param pageActionRecordMessageDTO
     * @return
     */
    Page<PageActionRecordMessageVO> pageGetActionRecordMessage(PageActionRecordMessageDTO pageActionRecordMessageDTO);

    /**
     * 新增活动记录信息
     * @param addActionRecordMessageDTO
     */
    @Insert("insert into action_record(id,action_information_id,user_id,state,notes,create_by,create_time,update_by,update_time)" +
            "values (#{id},#{actionInformationId},#{userId},#{state},#{notes},#{createBy},#{createTime},#{updateBy},#{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void addActionRecordMessage(AddActionRecordMessageDTO addActionRecordMessageDTO);

    /**
     * 根据活动记录id修改活动记录表信息
     * @param modifyActionRecordMessageDTO
     * @return
     */
    int modifyActionRecordMessage(ModifyActionRecordMessageDTO modifyActionRecordMessageDTO);

    /**
     * 根据活动记录id批量删除活动记录
     * @param idList
     * @param dateTime
     * @param currentId
     */
    void deleteActionRecordMessage(List<String> idList, LocalDateTime dateTime, String currentId);

    /**
     * 根据活动信息id查询参加活动的用户id
     * @param id
     * @return
     */
    @Select("select id from action_record where action_information_id = #{id}")
    List<String> getJoinActionIdList(String id);

    /**
     * 修改活动状态
     * @param id
     * @param state
     * @return
     */
    @Update("update action_record set state = #{state} where action_information_id = #{id}")
    int modifyState(String id, int state);
}
