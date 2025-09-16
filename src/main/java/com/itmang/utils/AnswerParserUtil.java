package com.itmang.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 答案解析工具类，用于处理不同类型题目的答案字符串解析
 */
public class AnswerParserUtil {
    private static final Logger log = LoggerFactory.getLogger(AnswerParserUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private AnswerParserUtil() {
        // 私有构造函数，防止实例化
    }

    /**
     * 解析多选题答案（嵌套JSON数组格式）
     * 示例输入: "[[\"A\",\"B\"],[\"C\",\"D\",\"E\"]]"
     * 示例输出: [["A","B"], ["C","D","E"]]
     */
    public static List<List<String>> parseMultipleChoiceAnswers(String answerString) {
        if (answerString == null || answerString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(
                    answerString,
                    new TypeReference<List<List<String>>>() {}
            );
        } catch (Exception e) {
            log.error("解析多选题答案失败: {}", answerString, e);
            // 尝试使用备用解析方法
            return parseMultipleChoiceAnswersFallback(answerString);
        }
    }

    /**
     * 解析单选题或判断题答案（普通JSON数组格式）
     * 示例输入: "[\"A\",\"B\",\"C\"]"
     * 示例输出: ["A","B","C"]
     */
    public static List<String> parseSingleChoiceAnswers(String answerString) {
        if (answerString == null || answerString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            // 移除可能的前后括号
            String cleanedString = removeBrackets(answerString);
            return Arrays.stream(cleanedString.split(","))
                    .map(s -> s.replace("\"", "").trim())
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("解析单选题/判断题答案失败: {}", answerString, e);
            return Collections.emptyList();
        }
    }

    /**
     * 解析填空题答案（字符串数组格式）
     * 示例输入: ["答案1","答案2"]
     * 示例输出: ["答案1", "答案2"]
     */
    public static List<String> parseFillBlankAnswers(String answerString) {
        return parseSingleChoiceAnswers(answerString);
    }

    /**
     * 将答案列表转换为JSON字符串
     */
    public static String toJsonString(Object answers) {
        try {
            return objectMapper.writeValueAsString(answers);
        } catch (Exception e) {
            log.error("转换答案为JSON字符串失败", e);
            return "[]";
        }
    }

    // 辅助方法：移除字符串前后的括号
    private static String removeBrackets(String input) {
        String result = input.trim();
        if (result.startsWith("[") && result.endsWith("]")) {
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }

    // 多选题答案的备用解析方法（当标准JSON解析失败时使用）
    private static List<List<String>> parseMultipleChoiceAnswersFallback(String answerString) {
        try {
            List<List<String>> result = new ArrayList<>();
            // 简单分割处理，可能不适用于所有情况
            String[] questionAnswers = answerString.replace("[", "").replace("]", "").split("\\],\\[");

            for (String questionAnswer : questionAnswers) {
                if (!questionAnswer.trim().isEmpty()) {
                    List<String> answers = Arrays.stream(questionAnswer.split(","))
                            .map(s -> s.replace("\"", "").trim())
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                    result.add(answers);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("备用方法解析多选题答案失败: {}", answerString, e);
            return Collections.emptyList();
        }
    }
}