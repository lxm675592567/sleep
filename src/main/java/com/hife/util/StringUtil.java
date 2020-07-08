package com.hife.util;

import java.util.Objects;

/**
 * 自实现字符串工具类
 */
public class StringUtil {
    private StringUtil() {
    }

    /**
     * 下划线
     */
    public final static String UNDER_lINE = "_";

    /**
     * 百分比
     */
    public final static String PERCENT = "%";

    /**
     * 竖线
     */
    public final static String VERTICAL_LINE = "|";

    /**
     * 正斜线
     */
    public final static String SLASH = "/";

    /**
     * 点
     */
    public final static String POINT = ".";

    /**
     * 竖线正则
     */
    public final static String REG_VERTICAL_LINE = "[|]";

    /**
     * 点正则
     */
    public final static String REG_POINT = "[.]";

    /**
     * 分号
     */
    public final static String SEMICOLON = ";";

    /**
     * 字符串0
     */
    public final static String ZERO_STR = "0";

    /**
     * 空字符串
     */
    public final static String EMPTY_STR = "";
    /**
     * 空格
     */
    public final static String BLANK_SPACE = " ";
    /**
     * 逗号
     */
    public final static String COMMA = ",";

    public static <T> boolean stringIsNull(T t) {
        return t == null || t.toString().trim().isEmpty();
    }

    public static boolean stringIsNull(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static <T> boolean stringIsNotNull(T t) {
        return !stringIsNull(t);
    }

    public static boolean stringIsNotNull(String str) {
        return !stringIsNull(str);
    }

    /*public static String join(String[] arr, String join) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : arr) {
            stringBuilder.append(str).append(join);
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }*/

    public static String join(String join, String... arr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : arr) {
            stringBuilder.append(str).append(join);
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }

    public static String formatExpression(String str) {
        return Objects.requireNonNull(str, "表达式为空").replaceAll("[＞﹥]", ">")
                .replaceAll("[＜﹤]", "<")
                .replaceAll("[≤≦]", "<=")
                .replaceAll("[≧≥]", ">=")
                .replaceAll(BLANK_SPACE, EMPTY_STR).trim();
    }
}
