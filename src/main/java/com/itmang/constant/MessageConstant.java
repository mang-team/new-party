package com.itmang.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {

    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String ACCOUNT_OR_PASSWORD_ERROR = "账号或密码错误";
    public static final String ACCOUNT_LOCKED = "账号被锁定";
    public static final String ACCOUNT_EXISTS = "账号已存在";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String LOGIN_SUCCESS = "登录成功";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";
    public static final String LOGOUT_SUCCESS = "退出成功";
    public static final String REGISTER_SUCCESS = "注册成功";
    public static final String ALREAD_EXISTS = "已存在";
    public static final String USER_NOT_FOUND = "找不到该用户";
    public static final String INSUFFICIENT_PERMISSIONS = "权限不足";
    public static final String DATA_NOT_EXISTS = "资料不存在";
    public static final String DATA_ALREADY_RELEASED = "资料已经发布";
    public static final String DATA_PART_DELETED = "部分资料删除成功";
    public static final String DATA_FAIL_DELETED = "资料删除失败";
    public static final String DATA_HAVE_ALREADY_RELEASED = "资料已是发布状态";
    public static final String DATA_HAVE_NOT_READ = "资料已不可读状态";
    public static final String DATA_ALREADY_STUDIED = "资料已学习过";
    public static final String DATA_NOT_READABLE = "资料不可读";
    public static final String SITUATION_NOT_EXISTS = "没有学习记录";
    public static final String USER_PART_ADD_FAILED = "用户部分添加失败";
    public static final String QUESTION_PART_ADD_FAILED = "题目部分添加失败";
    public static final String EXAMINATION_INFORMATION_FAIL_DELETED = "考试信息删除失败";
    public static final String EXAMINATION_INFORMATION_PART_DELETED = "考试信息部分删除成功";
    public static final String EXAMINATION_INFORMATION_NOT_EXIST = "考试信息不存在";
    public static final String USER_ADD_FAILED = "用户添加失败";
    public static final String QUESTION_BANK_EXIST = "题目已存在";
    public static final String QUESTION_PART_DELETED = "题目部分删除成功";
    public static final String QUESTION_DELETED_FAIL = "题目删除失败";
    public static final String QUESTION_BANK_NOT_EXIST = "题目不存在";
    public static final String ADD_PART_PAPER = "考卷部分添加成功";
    public static final String ADD_PAPER_FAIL = "考卷添加失败";
    public static final String PAPER_HAVE_ALREADY_SUBMIT = "考卷已提交";
    public static final String PAPER_NOT_EXIST = "考卷不存在";
    public static final String PAPER_HAVE_RETURN = "考卷已被打回";
    public static final String DELETE_PART_PAPER = "考卷部分删除成功";
    public static final String DELETE_PAPER_FAIL = "考卷删除失败";
    public static final String ID_CANNOT_BE_NULL = "ID不能为空";
    public static final String PAPER_NOT_SUBMIT = "考卷未提交";


    //部门
    public static final String DEPARTMENT_NOT_EXISTS = "部门不存在或已被删除";
    public static final String DEPARTMENT_NAME_EXISTS = "部门名称已存在";
    public static final String FATHER_DEPARTMENT_NOT_EXISTS = "上级部门不存在或已被删除";
    public static final String CANNOT_SET_SELF_AS_PARENT = "不能将本部门设为上级部门";
    public static final String DEPARTMENT_IDS_EMPTY = "部门ID列表不能为空";
    public static final String DEPARTMENT_HAS_CHILDREN = "该部门下有子级部门，不能删除";
    public static final String DEPARTMENT_EMPTY = "部门信息不能为空";
    public static final String DEPARTMENT_NAME_EMPTY = "部门名称不能为空";
    public static final String DEPARTMENT_NAME_TOO_LONG = "部门名称长度不能超过20个字符";


    public static final String DEPARTMENT_HAS_USERS = "该部门下有成员，不能删除";
    public static final String DEPARTMENT_ID_EMPTY = "部门id不为空";
    public static final String DEPARTMENT_FAIL_DELETED = "部门删除失败，所选部门均无法删除";
    public static final String DEPARTMENT_PART_DELETED = "部分部门删除成功，部分部门无法删除";
    public static final String QUESTION_ADD_FAILED = "题目添加失败";
    public static final String EXAMINATION_TIME_ERROR = "考试时间错误";
    public static final String EXAMINATION_SCORE_ERROR = "未设置分值";
    public static final String EXAMINATION_SCORE_ERROR_2 = "分值设置错误";

    //部门成员
    public static final String MEMBER_ID_CANNOT_BE_NULL = "成员ID不能为空";

    public static final String USER_ID_CANNOT_BE_NULL = "用户ID不能为空";
    public static final String DEPARTMENT_ID_CANNOT_BE_NULL = "部门ID不能为空";
    public static final String NAME_CANNOT_BE_NULL = "姓名不能为空";
    public static final String SEX_CANNOT_BE_NULL = "性别不能为空";
    public static final String ID_CARD_CANNOT_BE_NULL = "身份证号不能为空";
    public static final String TELEPHONE_CANNOT_BE_NULL = "手机号不能为空";

    public static final String ID_CARD_FORMAT_INCORRECT = "身份证号格式不正确";
    public static final String SEX_VALUE_MUST_BE_1_OR_2 = "性别值必须是1或2";
    public static final String POLITICAL_STATUS_VALUE_MUST_BE_1_TO_6 = "政治面貌值必须在1-6之间";
    public static final String IS_AT_SCHOOL_VALUE_MUST_BE_1_OR_2 = "是否在校值必须是1或2";

    public static final String MEMBER_INFORMATION_ALREADY_DELETED = "成员信息先前已被删除";
    public static final String MEMBER_INFORMATION_FAIL_DELETED = "删除成员信息失败";
    public static final String MEMBER_INFORMATION_PART_DELETED = "未被删除的成员信息删除成功";

    public static final String MEMBER_NOT_EXISTS = "成员不存在";

    public static final String PARAMETER_ERROR = "参数错误";
    public static final String DATA_INVALID = "资料不存在";
    public static final String POINTS_NOT_NUMBER = "积分不是整形";
    public static final String STATUS_NOT_NUMBER = "状态不是整形";
    public static final String EXAMINATION_INFORMATION_START_TIME_ERROR = "考试已经开始或结束";
    public static final String SCORE_NOT_NUMBER = "分数不是数字";
    public static final String PAPER_IS_NULL = "考卷为空";


}
