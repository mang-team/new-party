package com.itmang.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "成员详情返回VO")
public class MemberVO {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "userId", description = "用户id，外键")
    private String userId;

    @Schema(name = "departmentId", description = "部门id，外键")
    private String departmentId;

    @Schema(name = "name", description = "姓名")
    private String name;

    @Schema(name = "sex", description = "性别（2为女，1为男）")
    private Integer sex;

    @Schema(name = "major", description = "专业")
    private String major;

    @Schema(name = "class", description = "班级")
    private String classInfo;

    @Schema(name = "politicalStatus", description = "政治面貌（1为群众，2为共青团员，3为入党积极分子，4为发展对象，5为预备党员，6为正式党员）")
    private Integer politicalStatus;

    @Schema(name = "idCard", description = "身份证")
    private String idCard;

    @Schema(name = "telephone", description = "电话")
    private String telephone;

    @Schema(name = "educationBackground", description = "学历")
    private String educationBackground;

    @Schema(name = "nationality", description = "民族")
    private String nationality;

    @Schema(name = "nativePlace", description = "籍贯")
    private String nativePlace;

    @Schema(name = "dateOfBirth", description = "出生年月")
    private LocalDateTime dateOfBirth;

    @Schema(name = "joinCommunistTime", description = "入团时间")
    private LocalDateTime joinCommunistTime;

    @Schema(name = "submitCommunistTime", description = "提交入党申请书时间")
    private LocalDateTime submitCommunistTime;

    @Schema(name = "contacts", description = "入党培养联系人（成员id），外键")
    private String contacts;

    @Schema(name = "recommendingTime", description = "推优时间")
    private LocalDateTime recommendingTime;

    @Schema(name = "confirmedActiveMemberTime", description = "确定为入党积极分子时间")
    private LocalDateTime confirmedActiveMemberTime;

    @Schema(name = "confirmedDevelopmentTargetTime", description = "确定为发展对象时间")
    private LocalDateTime confirmedDevelopmentTargetTime;

    @Schema(name = "confirmedProbationaryMemberTime", description = "确定为预备党员时间")
    private LocalDateTime confirmedProbationaryMemberTime;

    @Schema(name = "becomeFullMemberTime", description = "转正时间")
    private LocalDateTime becomeFullMemberTime;

    @Schema(name = "isAtSchool", description = "是否在校（在校 1，不在校2）（默认值：1）")
    private Integer isAtSchool;

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