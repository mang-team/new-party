package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Schema(name = "PaperPageDTO",description = "考卷分页DTO")
@Data
public class PaperPageDTO implements Serializable {

    @Schema(name = "number", description = "考卷编号")
    private String number;
    @Schema(name = "examinationName", description = "考试名称")
    private String examinationName;
    @Schema(name = "userName", description = "考试用户名字")
    private String userName;
    @Schema(name = "isSubmit", description = "考卷状态")
    private Integer isSubmit;
    @Schema(name = "scoreMin", description = "分数最小值")
    private Integer scoreMin;
    @Schema(name = "scoreMax", description = "分数最大值")
    private Integer scoreMax;
    @Schema(name = "page", description = "当前页")
    private Integer page;
    @Schema(name = "pageSize", description = "每页大小")
    private Integer pageSize;
}
