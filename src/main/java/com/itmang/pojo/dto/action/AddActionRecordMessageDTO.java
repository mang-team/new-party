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

    private String actionInformationId;  // 活动信息id
    private String userId;   // 用户id  外检

}
