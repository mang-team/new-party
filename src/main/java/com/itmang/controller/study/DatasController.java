package com.itmang.controller.study;


import com.itmang.pojo.entity.Result;
import com.itmang.service.study.DatasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping ("study/datas")
@Tag(name ="资料相关接口")
public class DatasController {

    @Autowired
    private DatasService datasService;


    /**
     * 发布资料接口
     * @param dataId
     * @return
     */
    @Operation(summary = "发布资料")
    @PostMapping("/release")
    public Result releaseData(String dataId){
        log.info("发布资料id:{}",dataId);
        datasService.releaseData(dataId);
        return Result.success();
    }



}
