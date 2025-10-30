package com.itmang.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 投票信息查询/新增 DTO
 */
@Data
public class FindVoteSignDTO {
    private String id;

    private String voteInformationId;

    private String userId;

    private String choose;

    private String createBy;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Integer isDelete;

    // 分页参数
    private Integer pageNum;

    private Integer pageSize;
}