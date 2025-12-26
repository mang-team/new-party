package com.itmang.mapper.action;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.itmang.annotation.AutoFill;
import com.itmang.enumeration.OperationType;
import com.itmang.pojo.dto.action.AddActionRecordMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionRecordMessageDTO;
import com.itmang.pojo.dto.action.PageActionRecordMessageDTO;
import com.itmang.pojo.entity.ActionRecord;
import com.itmang.pojo.entity.SignInInformation;
import com.itmang.pojo.vo.action.DetailActionRecordMessageVO;
import com.itmang.pojo.vo.action.PageActionRecordMessageVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ActionRecordMapper extends BaseMapper<ActionRecord> {
    /**
     * 分页查询活动记录信息
     * @param pageActionRecordMessageDTO
     * @return
     */
    List<PageActionRecordMessageVO> pageGetActionRecordMessage(PageActionRecordMessageDTO pageActionRecordMessageDTO);


    /**
     * 根据活动记录id修改活动记录表信息
     * @param modifyActionRecordMessageDTO
     * @return
     */
    int modifyActionRecordMessage(ModifyActionRecordMessageDTO modifyActionRecordMessageDTO);

    /**
     * 根据活动记录id批量删除活动记录
     * @param idList
     */
    void deleteActionRecordMessage(List<String> idList);

    /**
     * 根据活动记录id查询活动记录信息
     * @param id
     * @return
     */
    DetailActionRecordMessageVO getActionRecordMessageById(String id);
}
