package com.itmang.pojo.vo.action;


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
public class pageShortActionMessageVO {


    @Schema(description = "活动id")
    private String id;
    @Schema(description = "活动名称")
    private String actionName;
    @Schema(description = "活动人数")
    private Integer recruitmentQuantity;
    @Schema(description = "活动参与人数")
    private Integer participantQuantity;
    @Schema(description = "活动报名开始时间")
    private LocalDateTime signUpStartTime;
    @Schema(description = "活动报名结束时间")
    private LocalDateTime signUpEndTime;
    @Schema(description = "活动开始时间")
    private LocalDateTime actionStartTime;
    @Schema(description = "活动结束时间")
    private LocalDateTime actionEndTime;
    @Schema(description = "活动地点")
    private String eventAddress;
}
