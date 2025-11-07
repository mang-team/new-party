package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Data
@Schema(description = "更新成员请求DTO")
public class MemberUpdateDTO {

    @NotBlank(message = "id不能为空")
    @Schema(name = "id", description = "id")
    private String id;



    @NotBlank(message = "用户id不能为空")
    @Schema(name = "用户id", description = "user_id")
    private String userId;

    @NotBlank(message = "部门ID不能为空")
    @Schema(description = "部门ID", required = true)
    private String departmentId;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名", required = true)
    private String name;

    @NotNull(message = "性别不能为空")
    @Schema(description = "性别（1为男，2为女）", required = true)
    private Integer sex;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "班级")
    private String classInfo;


    @NotNull(message = "政治面貌不能为空")
    @Schema(description = "政治面貌（1群众,2共青团员,3入党积极分子,4发展对象,5预备党员,6正式党员）", required = true)
    private Integer politicalStatus;

    @NotBlank(message = "身份证号不能为空")
    @Schema(description = "身份证号", required = true)
    private String idCard;

    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号", required = true)

    private String telephone;

    @Schema(description = "学历")
    private String educationBackground;


    @NotBlank(message = "民族不能为空")
    @Schema(description = "民族")
    private String nationality;

    @NotBlank(message = "籍贯不能为空")
    @Schema(description = "籍贯")
    private String nativePlace;

    @NotBlank(message = "出生日期不能为空")
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



//    @Schema(description = "是否删除标记")
//    private Integer isDelete;




}