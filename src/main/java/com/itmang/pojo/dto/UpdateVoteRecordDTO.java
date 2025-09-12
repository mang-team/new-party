package com.itmang.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateVoteRecordDTO implements Serializable {
    private String id;

    private String notes;

    private String choose;
}