package com.itmang.service.activity;

/**
 * @Author luo
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.AddRegisterDTO;
import com.itmang.pojo.dto.DeleteRegisterDTO;
import com.itmang.pojo.dto.FindRegisterDTO;
import com.itmang.pojo.dto.UpdateRegisterDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.SignInInformation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RegisterService extends IService<SignInInformation> {


    /**
     * 新增签到信息
     * @param addRegisterDTOList
     * @param UserId
     */
    void addRegisterInformation(List<AddRegisterDTO> addRegisterDTOList,String UserId);

    /**
     * 删除签到信息
     * @param deleteRegisterDTO
     * @param UserId
     */
    void deleteRegisterInformation(DeleteRegisterDTO deleteRegisterDTO,String UserId);

    /**
     * 编辑签到信息
     * @param updateRegisterDTO
     * @param UserId
     */
    void updateRegisterInformation(UpdateRegisterDTO updateRegisterDTO,String UserId);

    /**
     * 分页查询签到信息
     * @param findRegisterDTO
     * @return
     */
    PageResult queryRegisterInformationList(FindRegisterDTO findRegisterDTO);

}
