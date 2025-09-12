package com.itmang.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddRegisterRecordDTO implements Serializable {
    private String signInInformationId;

    private Integer state;

    private String notes;
}