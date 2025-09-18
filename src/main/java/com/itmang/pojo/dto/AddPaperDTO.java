package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Schema(name = "AddPaperDTO",description = "考卷DTO")
@Data
public class AddPaperDTO implements Serializable {

    @Schema(name = "ids", description = "用户id集合")
    private String ids;
    @Schema(name = "examinationInformationId", description = "考试信息id")
    private String examinationInformationId;
}
