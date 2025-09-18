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


    void addRegisterInformation(List<AddRegisterDTO> addRegisterDTOList,String UserId);

    void deleteRegisterInformation(DeleteRegisterDTO deleteRegisterDTO,String UserId);

    void updateRegisterInformation(UpdateRegisterDTO updateRegisterDTO,String UserId);

    PageResult queryRegisterInformationList(FindRegisterDTO findRegisterDTO);

}
