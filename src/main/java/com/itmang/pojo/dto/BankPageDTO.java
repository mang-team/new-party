package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;


@Schema(name = "BankPageDTO",description = "题库分页DTO")
@Data
public class BankPageDTO implements Serializable {

    @Schema(name = "type", description = "题目类型")
    private String type;
    @Schema(name = "question", description = "问题")
    private String question;
    @Schema(name = "answerType", description = "答案类型")
    private String answerType;
    @Schema(name = "isChoose", description = "是否选用")
    private Integer isChoose;
    @Schema(name = "page", description = "当前页")
    private Integer page;
    @Schema(name = "pageSize", description = "每页大小")
    private Integer pageSize;
}
