package com.itmang.pojo.vo;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Schema(name = "DatasVO",description = "资料VO")
@Data
public class SituationVO implements Serializable {

    @Schema(name = "userName", description = "学习用户名字")
    private String userName;
    @Schema(name = "datasCode", description = "学习资料编码")
    private String datasCode;
    @Schema(name = "learningTime", description = "学习累计时间")
    private Integer learningTime;
    @Schema(name = "learningProgress", description = "学习进度")
    private Integer learningProgress;
    @Schema(name = "startTime", description = "开始学习时间")
    private LocalDateTime startTime;
    @Schema(name = "content", description = "学习分享")
    private String content;
    @Schema(name = "isPoints", description = "是否获得积分")
    private Integer isPoints;
    @Schema(name = "updateName", description = "修改人名字")
    private String updateName;
    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;;
}
