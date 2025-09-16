package com.itmang.pojo.vo.action;

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
    private int state;    // 状态
    private String notes; // 备注
    private String createBy;   // 创建人
    private LocalDateTime createTime; // 创建时间
    private String updateBy;   // 修改人
    private LocalDateTime updateTime; // 修改时间
}
