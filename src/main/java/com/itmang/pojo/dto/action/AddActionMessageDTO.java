package com.itmang.pojo.dto.action;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddActionMessageDTO {


    private String actionName;        // 活动名称
    private String activityContent;   // 活动内容详情
    private String userId;            // 组织者id
    private String organizerPhone;    // 组织者电话
    private Integer recruitmentQuantity;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signUpStartTime;        // 活动报名开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signUpEndTime;          // 活动报名结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionStartTime;        // 活动开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionEndTime;          // 活动结束时间
    private String eventAddress; // 活动地点


}
