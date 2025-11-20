package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Schema(name = "LearnSituationDTO",description = "学习资料DTO")
@Data
public class LearnSituationDTO implements Serializable {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "learningTime", description = "学习累计时间")
    private Integer learningTime;
    @Schema(name = "videoTime", description = "视频时长")
    private Integer videoTime;

}
