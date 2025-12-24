package com.itmang.service.activity;

/**
 * @Author luo
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.AddRegisterRecordDTO;
import com.itmang.pojo.dto.DeleteRegisterRecodeDTO;
import com.itmang.pojo.dto.FindRegisterSignDTO;
import com.itmang.pojo.dto.UpdateRegisterRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.SignInRecord;
import com.itmang.pojo.vo.SignInRecordVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RegisterRecodeService extends IService<SignInRecord> {

    /**
     * 新增签到信息
     * @param addRegisterRecordDTO
     * @param UserId
     */
    void addRegisterRecodeInformation(AddRegisterRecordDTO addRegisterRecordDTO, String UserId);

    /**
     * 删除签到记录
     * @param deleteRegisterRecodeDTO
     * @param UserId
     */
    void deleteRegisterRecodeInformation(DeleteRegisterRecodeDTO deleteRegisterRecodeDTO,String UserId);

    /**
     * 编辑签到记录
     * @param updateRegisterRecordDTO
     * @param UserId
     */
    void updateRegisterRecodeInformation(UpdateRegisterRecordDTO updateRegisterRecordDTO,String UserId);

    /**
     * 分页查询签到信息
     * @param findRegisterSignDTO
     * @return
     */
    PageResult queryRegisterRecodeInformationList(FindRegisterSignDTO findRegisterSignDTO);

    /**
     * 用户签到
     * @param id
     */
    void userSign(String id);

    /**
     * 修改用户状态
     * @param status
     * @param id
     */
    void updateUserStatus(Integer status, String id);

    /**
     * 查询签到信息记录
     * @param id
     * @return
     */
    SignInRecordVO queryRegisterRecodeInformation(String id);
}