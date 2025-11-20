package com.itmang.pojo.vo;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Schema(name = "LearnSituationVO",description = "开始学习VO")
@Data
public class LearnSituationVO implements Serializable {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "datasCode", description = "学习资料编码")
    private String datasCode;
    @Schema(name = "title", description = "资料标题")
    private String title;
    @Schema(name = "type", description = "类型")
    private Integer type;
    @Schema(name = "pageview", description = "点击量")
    private String pageview;
    @Schema(name = "releaseName", description = "资料发布人名字")
    private String releaseName;
    @Schema(name = "learningProgress", description = "学习进度")
    private Integer learningProgress;
    @Schema(name = "startTime", description = "开始学习时间")
    private LocalDateTime startTime;
    @Schema(name = "content", description = "学习分享")
    private String content;
    @Schema(name = "isPoints", description = "是否获得积分")
    private Integer isPoints;

}
