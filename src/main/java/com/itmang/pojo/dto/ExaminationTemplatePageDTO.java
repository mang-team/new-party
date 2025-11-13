package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "ExaminationTemplatePageDTO", description = "考试模版条件DTO")
public class ExaminationTemplatePageDTO {

    @Schema(name = "userId", description = "用户id")
    private String userId;
    @Schema(name = "userName", description = "用户名称")
    private String userName;
    @Schema(name = "note", description = "模版备注")
    private String note;
    @Schema(name = "page", description = "当前页")
    private Integer page;
    @Schema(name = "pageSize", description = "每页大小")
    private Integer pageSize;
}