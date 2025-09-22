package com.itmang.pojo.dto.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageActionRecordMessageDTO {
    // 设置默认值
    private int page = 1;
    private int pageSize = 10;
    private String actionInformationId;  // 活动信息id
}
