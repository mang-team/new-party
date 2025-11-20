package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Schema(name = "AddSituationDTO",description = "学习情况DTO")
@Data
@Builder
public class AddSituationDTO implements Serializable {

    @Schema(name = "userId",description = "用户id")
    private String userId;
    @Schema(name = "datasId",description = "学习资料id")
    private String datasId;

}
