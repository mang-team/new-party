package com.itmang.pojo.vo.action;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DetailActionMessageVO {

    // action_information 表信息
    private String actionName;        // 活动名称
    private String activityContent;   // 活动内容详情
    private String organizerPhone;    // 组织者电话
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signUpStartTime;        // 活动报名开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signUpEndTime;          // 活动报名结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionStartTime;        // 活动开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionEndTime;          // 活动结束时间
    private String eventAddress; // 活动地点
    private String createBy;     // 发布人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;  // 发布时间
    private String updateBy;   // 修改人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;  // 修改时间
    private int isDelete;              // 是否删除
    private List<String> userList; // 参加此次活动的用户id

    // action_record 表信息   利用活动id连接
//    private int state;  // 状态 未开始1 进行中2 已结束3
//    private String notes; // 备注

}
