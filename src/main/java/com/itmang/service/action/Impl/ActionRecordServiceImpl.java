package com.itmang.service.action.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itmang.context.BaseContext;
import com.itmang.mapper.action.ActionRecordMapper;
import com.itmang.pojo.dto.action.AddActionRecordMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionRecordMessageDTO;
import com.itmang.pojo.dto.action.PageActionRecordMessageDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.vo.action.PageActionRecordMessageVO;
import com.itmang.service.action.ActionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ActionRecordServiceImpl implements ActionRecordService {
    @Autowired
    private ActionRecordMapper actionRecordMapper;

    /**
     * 分页查询活动记录表信息
     * @param pageActionRecordMessageDTO
     * @return
     */
    @Override
    public PageResult pageGetActionRecordMessage(PageActionRecordMessageDTO pageActionRecordMessageDTO) {
        PageHelper.startPage(pageActionRecordMessageDTO.getPage(),pageActionRecordMessageDTO.getPageSize());

        Page<PageActionRecordMessageVO> page = actionRecordMapper.pageGetActionRecordMessage(pageActionRecordMessageDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增活动记录信息
     * @param addActionRecordMessageDTO
     */
    @Override
    public void addActionRecordMessage(AddActionRecordMessageDTO addActionRecordMessageDTO) {
        // 设置用户id 外键
        addActionRecordMessageDTO.setUserId(BaseContext.getCurrentId());
        // 调用方法
        // 添加活动记录
        actionRecordMapper.addActionRecordMessage(addActionRecordMessageDTO);
    }

    /**
     * 根据活动记录id修改活动记录表信息
     * @param modifyActionRecordMessageDTO
     * @return
     */
    @Override
    public boolean modifyActionRecordMessage(ModifyActionRecordMessageDTO modifyActionRecordMessageDTO) {
        // 根据活动记录id修改活动记录表信息，查看返回值result是否大于0，从而判断是否成功修改
        int result = actionRecordMapper.modifyActionRecordMessage(modifyActionRecordMessageDTO);
        return result > 0;
    }

    /**
     * 根据活动记录id批量删除活动记录
     * @param idList
     */
    @Override
    public void deleteActionRecordMessage(List<String> idList) {
        // 更新时间
        LocalDateTime now = LocalDateTime.now();
        // 时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式转换
        LocalDateTime dateTime = LocalDateTime.parse(now.format(formatter), formatter);
        // 调用mapper删除
        actionRecordMapper.deleteActionRecordMessage(idList,dateTime,BaseContext.getCurrentId());
    }

}
