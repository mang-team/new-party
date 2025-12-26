package com.itmang.pojo.dto.action;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageActionRecordMessageDTO {


    @Schema(description = "活动信息id")
    private String actionInformationId;
    @Schema(description = "活动信息名称")
    private String actionInformationName;
    @Schema(description = "用户id")
    private String userId;
    @Schema(description = "用户名字")
    private String userName;
    @Schema(description = "页码")
    private Integer pageSize;
    @Schema(description = "每页条数")
    private Integer page;
}
