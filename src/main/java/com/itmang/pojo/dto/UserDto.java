package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName:UserDto
 * Package:com.itmang.pojo.dto
 * Description:
 *
 * @Author: 绯雾sama
 * @Create:2025/9/10 14:28
 * Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String userName;

    private String image;

}
