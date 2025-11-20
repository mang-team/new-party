package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Schema(name = "SituationUpdateDTO",description = "学习情况DTO")
@Data
public class SituationUpdateDTO implements Serializable {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "content", description = "学习分享")
    private String content;
}
