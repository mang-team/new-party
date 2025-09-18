package com.itmang.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddVoteRecordDTO implements Serializable {
    private String voteInformationId;

    private String choose;

    private String notes;
}