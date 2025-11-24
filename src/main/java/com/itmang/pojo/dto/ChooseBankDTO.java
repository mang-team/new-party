package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;


@Schema(name = "ChooseBankDTO",description = "选题DTO")
@Data
public class ChooseBankDTO implements Serializable {

    @Schema(name = "type", description = "题目类型")
    private String type;
    @Schema(name = "num", description = "数量")
    private Integer num;

}
