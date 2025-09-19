package com.itmang.pojo.dto;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * ClassName:PageUserDto
 * Package:com.itmang.pojo.dto
 * Description:
 *
 * @Author: 绯雾sama
 * @Create:2025/9/19 8:41
 * Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageUserDto {

    private Integer current;

    private Integer size;

    private String userName;

    private LocalDateTime createStartTime;

    private LocalDateTime createEndTime;

    private LocalDateTime updateStartTime;

    private LocalDateTime updateEndTime;
}
