package com.itmang.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "StudyRecord", description = "学习记录表实体类")
public class StudyRecord {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "userId", description = "学习用户，外键（用户id）")
    private String userId;

    @Schema(name = "datasId", description = "学习资料，外键（资料id）")
    private String datasId;

    @Schema(name = "learningTime", description = "学习累计时间（默认值：0，单位为分钟）")
    private Integer learningTime;

    @Schema(name = "learningProgress", description = "学习进度(0-100)（默认值：0）")
    private Integer learningProgress;

    @Schema(name = "startTime", description = "开始学习时间")
    private LocalDateTime startTime;

    @Schema(name = "content", description = "学习分享")
    private String content;

    @Schema(name = "isPoints", description = "是否获得积分(获得为1，不获得为2)（冗余字段），当60%后获得积分")
    private Integer isPoints;

    @Schema(name = "createBy", description = "创建人")
    private String createBy;

    @Schema(name = "createTime", description = "创建时间")
    private LocalDateTime createTime;

    @Schema(name = "updateBy", description = "修改人")
    private String updateBy;

    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(name = "isDelete", description = "是否删除（1为删除，2为未删除）（默认为2）")
    private Integer isDelete;
}