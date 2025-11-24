package com.itmang.controller.study;


import com.itmang.pojo.dto.DatasDTO;
import com.itmang.pojo.dto.DatasPageDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.DatasPageVO;
import com.itmang.pojo.vo.DatasVO;
import com.itmang.service.study.DatasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "资料缓存")
    @PostMapping("/add")
    public Result addData(@RequestBody DatasDTO addDatasDTO){
        log.info("发布资料id:{}",addDatasDTO);
        datasService.addData(addDatasDTO);
        return Result.success();
    }

    @Operation(summary = "删除资料（可批量）")
    @PostMapping("/delete/{ids}")
    public Result deleteData(@PathVariable String[] ids){
        log.info("删除资料id:{}",ids);
        datasService.deleteData(ids);
        return Result.success();
    }

    @Operation(summary = "修改资料状态")
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status, String id){
        log.info("修改资料id:{},状态:{}",id,status);
        datasService.updateStatus(id,status);
        return Result.success();
    }

    @Operation(summary = "查询资料")
    @GetMapping("/{id}")
    public Result<DatasVO> updateData(@PathVariable String id){
        log.info("查询资料id:{}",id);
        DatasVO datasVO = datasService.searchData(id);
        return Result.success(datasVO);
    }

    @Operation(summary = "分页查询资料")
    @GetMapping("/page")
    public Result<PageResult> pageData(DatasPageDTO datasPageDTO){
        log.info("分页查询资料:{}",datasPageDTO);
        PageResult pageResult = datasService.pageData(datasPageDTO);
        return Result.success(pageResult);
    }





}
