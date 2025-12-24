package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateRegisterRecordDTO implements Serializable {

    @Schema(description = "id")
    private String id;
    @Schema(description = "备注")
    private String notes;
}