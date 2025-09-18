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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RegisterRecodeService extends IService<SignInRecord> {


    void addRegisterRecodeInformation(List<AddRegisterRecordDTO> addRegisterRecordDTOList, String UserId);

    void deleteRegisterRecodeInformation(DeleteRegisterRecodeDTO deleteRegisterRecodeDTO,String UserId);

    void updateRegisterRecodeInformation(UpdateRegisterRecordDTO updateRegisterRecordDTO,String UserId);

    PageResult queryRegisterRecodeInformationList(FindRegisterSignDTO findRegisterSignDTO);
}