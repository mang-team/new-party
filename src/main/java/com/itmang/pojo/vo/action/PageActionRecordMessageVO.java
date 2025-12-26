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
public class PageActionRecordMessageVO {

    @Schema(description = "活动记录表id")
    private String id;
    @Schema(description = "活动信息id")
    private String actionInformationId;
    @Schema(description = "活动名称")
    private String actionName;
    @Schema(description = "活动状态")
    private Integer state;
    @Schema(description = "备注")
    private String notes;
    @Schema(description = "参与时间")
    private LocalDateTime participationTime;

}
