package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;


@Schema(name = "SituationPageDTO",description = "学习情况分页DTO")
@Data
public class SituationPageDTO implements Serializable {

    @Schema(name = "userName", description = "学习用户名字")
    private String userName;
    @Schema(name = "datasCode", description = "学习资料编码")
    private String datasCode;
    @Schema(name = "datasName", description = "学习资料名称")
    private String datasName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "startTimeStart", description = "开始学习时间开始时间")
    private LocalDateTime startTimeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "startTimeEnd", description = "开始学习时间结束时间")
    private LocalDateTime startTimeEnd;
    @Schema(name = "page", description = "当前页")
    private Integer page;
    @Schema(name = "pageSize", description = "每页大小")
    private Integer pageSize;
}
