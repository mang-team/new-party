package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 头像上传DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AvatarUploadDTO", description = "头像上传DTO")
public class AvatarUploadDTO implements Serializable {

    @Schema(name = "avatarFile", description = "头像文件")
    private MultipartFile avatarFile;
}