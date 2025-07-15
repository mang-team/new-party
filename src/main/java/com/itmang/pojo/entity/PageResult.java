package com.itmang.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PageResult",description = "分页查询结果")
public class PageResult implements Serializable {

    @Schema(name = "total",description = "总记录数")
    private Long total; //总记录数

    @Schema(name = "records",description = "当前页数数据集合")
    private List records; //当前页数据集合

}
