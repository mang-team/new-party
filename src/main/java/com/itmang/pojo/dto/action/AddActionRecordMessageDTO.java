package com.itmang.pojo.dto.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddActionRecordMessageDTO {
    private String id;
    private String actionInformationId;  // 活动信息id
    private String userId;   // 用户id  外检
    private int state;       // 状态  未开始1 进行中2 已结束3
    private String notes;    // 备注
    private String createBy; // 创建人
    private LocalDateTime createTime; // 创建时间
    private String updateBy;         // 修改人
    private LocalDateTime updateTime; // 修改时间
}
