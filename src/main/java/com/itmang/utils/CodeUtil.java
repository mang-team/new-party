package com.itmang.utils;

import java.util.BitSet;
import java.util.Random;

public class CodeUtil {
    private static final int CODE_LENGTH = 6;
    private static final int MAX_VALUE = (int) Math.pow(10, CODE_LENGTH);
    private static final int MIN_VALUE = (int) Math.pow(10, CODE_LENGTH - 1);
    private static final BitSet usedBits = new BitSet(MAX_VALUE + 1);
    private static final Random random = new Random();
    // 生成6位随机数

    public static synchronized String getCode() {
        int code;
        do {
            code = random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE;
        } while (usedBits.get(code));

        usedBits.set(code);
        return String.valueOf(code);
    }

}
