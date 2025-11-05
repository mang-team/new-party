package com.itmang.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FindRegisterDTO implements Serializable {
    private String id;               // 签到 ID，可为空
    private String signInTitle;    // 签到标题，可为空
    private String signInContent;  // 签到内容，可为空
    private String createTime;     // 创建时间，可为空
    private String updateTime;     // 更新时间，可为空
    private String createBy;
    private String updateBy;
    private String startTime;      // 签到开始时间，可为空
    private String endTime;        // 签到结束时间，可为空
    private Integer isDelete;      // 是否删除，可为空

    private Integer pageNum;       // 分页页码，可为空
    private Integer pageSize;      // 分页大小，可为空
}