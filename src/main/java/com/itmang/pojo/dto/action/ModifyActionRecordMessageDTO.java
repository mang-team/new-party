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
public class ModifyActionRecordMessageDTO {

    private String id;     // id
    private String notes;  // 备注

}
