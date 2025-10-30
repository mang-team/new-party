package com.itmang.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateRegisterRecordDTO implements Serializable {
    private String id;

    private Integer state;

    private String notes;
}