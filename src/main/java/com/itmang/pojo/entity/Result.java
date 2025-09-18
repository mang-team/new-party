package com.itmang.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Result",description = "响应结果")
public class Result<T> implements Serializable {
    @Schema(name = "code",description = "响应码")
    private Integer code;//响应码（1表示响应成功，0表示响应失败）

    @Schema(name = "msg",description = "响应信息")
    private String msg;//响应信息

    @Schema(name = "data",description = "响应数据")
    private T data;//响应成功返回的数据

    public static <T> Result<T> success() {
        return new Result<T>(1,"sucess",null);
    }

    public static <T> Result<T> success( T object) {
        return new Result<T>(1, "sucess", object);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<T>(0, msg, null);
    }

    // ✅ 新增：支持错误信息和数据的方法
    public static <T> Result<T> error(String msg, T data) {
        return new Result<T>(0, msg, data);
    }
}

