package com.itmang.pojo.vo;

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
@Schema(name = "BankPageVO", description = "题目分页VO")
public class BankPageVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "type", description = "题目类型")
    private String type;
    @Schema(name = "question", description = "问题")
    private String question;
    @Schema(name = "isChoose", description = "是否选用")
    private Integer isChoose;
    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

}