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
    int page;
    int pageSize;
    private String actionInformationId;  // 活动信息id
}
