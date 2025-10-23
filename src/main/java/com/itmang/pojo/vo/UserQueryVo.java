package com.itmang.pojo.vo;

import com.itmang.pojo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName:UserVo
 * Package:com.itmang.pojo.vo
 * Description:
 *
 * @Author: 绯雾sama
 * @Create:2025/10/23 18:05
 * Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQueryVo extends User {
    private String name;
    private String departmentName;
    private Integer sex;
    private String major;
    private String classInfo;
    private Integer politicalStatus;
    private String idCard;
    private String telephone;
    private String educationBackground;
    private String nationality;
    private String nativePlace;
    private LocalDateTime dateOfBirth;
    private LocalDateTime joinCommunistTime;
    private LocalDateTime submitCommunistTime;
    private String contacts;
    private LocalDateTime recommendingTime;
    private LocalDateTime confirmedActiveMemberTime;
    private LocalDateTime confirmedDevelopmentTargetTime;
    private LocalDateTime confirmedProbationaryMemberTime;
    private LocalDateTime becomeFullMemberTime;
    private Integer isAtSchool;
}
