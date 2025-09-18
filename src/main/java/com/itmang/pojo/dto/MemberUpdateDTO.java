package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "更新成员请求DTO")
public class MemberUpdateDTO {

    @NotBlank(message = "成员ID不能为空")
    @Schema(description = "成员ID", required = true)
    private String id;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "部门ID")
    private String departmentId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别（1为男，2为女）")
    private Integer sex;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "班级")
    private String classInfo;

    @Schema(description = "政治面貌（1群众,2共青团员,3入党积极分子,4发展对象,5预备党员,6正式党员）")
    private Integer politicalStatus;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "手机号")
    private String telephone;

    @Schema(description = "学历")
    private String educationBackground;

    @Schema(description = "民族")
    private String nationality;

    @Schema(description = "籍贯")
    private String nativePlace;

    @Schema(description = "出生日期")
    private LocalDateTime dateOfBirth;

    @Schema(description = "入团时间")
    private LocalDateTime joinCommunistTime;

    @Schema(description = "提交入党申请书时间")
    private LocalDateTime submitCommunistTime;

    @Schema(description = "入党培养联系人ID")
    private String contacts;

    @Schema(description = "推优时间")
    private LocalDateTime recommendingTime;

    @Schema(description = "确定为入党积极分子时间")
    private LocalDateTime confirmedActiveMemberTime;

    @Schema(description = "确定为发展对象时间")
    private LocalDateTime confirmedDevelopmentTargetTime;

    @Schema(description = "确定为预备党员时间")
    private LocalDateTime confirmedProbationaryMemberTime;

    @Schema(description = "转正时间")
    private LocalDateTime becomeFullMemberTime;

    @Schema(description = "是否在校（1在校，2不在校）")
    private Integer isAtSchool;

    @Schema(description = "是否删除（1删除，2未删除）")
    private Integer isDelete;

    @Schema(description = "修改人")
    private String updateBy;
}