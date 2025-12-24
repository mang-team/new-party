package com.itmang.mapper.activity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.dto.FindRegisterSignDTO;
import com.itmang.pojo.entity.SignInRecord;
import com.itmang.pojo.vo.SignInRecordPageVO;
import com.itmang.pojo.vo.SignInRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 签到信息 Mapper
 */
@Mapper
public interface SignInRecordMapper extends BaseMapper<SignInRecord> {

    /**
     * 条件查询签到记录列表
     * @param dto
     * @return
     */
    List<SignInRecordPageVO> querySignInRecordList(FindRegisterSignDTO dto);

    /**
     * 查询签到记录信息
     * @param id
     * @return
     */
    SignInRecordVO queryRegisterRecodeInformation(String id);

    /**
     * 批量删除签到记录
     * @param canDeleteSignInRecordIds
     */
    void deleteByIds(List<String> canDeleteSignInRecordIds);
}