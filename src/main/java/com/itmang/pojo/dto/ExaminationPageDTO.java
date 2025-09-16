package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;


@Schema(name = "ExaminationPageDTO",description = "考试情况分页DTO")
@Data
public class ExaminationPageDTO implements Serializable {

    @Schema(name = "examinationName", description = "考试名称")
    private String examinationName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "startTimeStart", description = "开始考试时间开始时间")
    private LocalDateTime startTimeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "startTimeEnd", description = "开始考试时间结束时间")
    private LocalDateTime startTimeEnd;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "endTimeStart", description = "结束考试时间开始时间")
    private LocalDateTime endTimeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "endTimeEnd", description = "结束考试时间结束时间")
    private LocalDateTime endTimeEnd;
    @Schema(name = "page", description = "当前页")
    private Integer page;
    @Schema(name = "pageSize", description = "每页大小")
    private Integer pageSize;
}
