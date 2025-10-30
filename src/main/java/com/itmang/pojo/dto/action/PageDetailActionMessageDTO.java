package com.itmang.pojo.dto.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class PageDetailActionMessageDTO {
    // 设置默认值
    private int page = 1;     // 当前页码
    private int pageSize = 10; //每页展示数
    private String actionName;  // 活动名称
    private String createBy;    // 发布人
}
