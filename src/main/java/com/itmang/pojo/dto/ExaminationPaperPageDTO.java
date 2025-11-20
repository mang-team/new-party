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
@Schema(name = "ExaminationPaperPageDTO", description = "考试试题条件DTO")
public class ExaminationPaperPageDTO {

    @Schema(name = "paperName", description = "试题名称")
    private String paperName;
    @Schema(name = "userId", description = "用户id")
    private String userId;
    @Schema(name = "page", description = "当前页")
    private Integer page;
    @Schema(name = "pageSize", description = "每页大小")
    private Integer pageSize;
}