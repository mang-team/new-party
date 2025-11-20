package com.itmang.pojo.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 题目ID处理结果封装
 */
@Data
public class QuestionProcessResult {
    // 有效题目ID列表（存在、未删除、类型匹配）
    private List<String> validQuestionIds = new ArrayList<>();
    // 需要更新选中状态的题目ID列表（isChoose=DISABLE）
    private List<String> toUpdateSelectedIds = new ArrayList<>();
}
