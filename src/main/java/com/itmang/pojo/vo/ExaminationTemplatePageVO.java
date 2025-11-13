package com.itmang.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "ExaminationTemplatePageVO", description = "考试模版分页VO")
public class ExaminationTemplatePageVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "note", description = "备注")
    private String note;
    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;
    @Schema(name = "updateName", description = "修改人姓名")
    private String updateName;
}