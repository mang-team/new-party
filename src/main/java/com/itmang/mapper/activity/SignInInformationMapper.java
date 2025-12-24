package com.itmang.mapper.activity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.dto.FindRegisterDTO;
import com.itmang.pojo.entity.SignInInformation;
import com.itmang.pojo.vo.SignInInformationPageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 签到信息 Mapper
 */
@Mapper
public interface SignInInformationMapper extends BaseMapper<SignInInformation> {

    /**
     * 分页查询签到信息
     * @param dto
     * @return
     */
    List<SignInInformationPageVO> querySignInInformationList(FindRegisterDTO dto);


}
