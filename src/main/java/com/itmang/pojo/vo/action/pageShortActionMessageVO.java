package com.itmang.pojo.vo.action;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class pageShortActionMessageVO {
    private String actionName;        // 活动名称
    private String organizerPhone;    // 组织者电话
    private String createBy;     // 发布人
}
