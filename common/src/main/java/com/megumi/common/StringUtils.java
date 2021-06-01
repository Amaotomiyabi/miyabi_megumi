package com.megumi.common;

/**
 * 2021/2/21
 * 字符串参数有效性检查工具
 *
 * @author miyabi
 * @since 1.0
 */
public class StringUtils {
    /**
     * @param arg 参数
     * @return 非法返回false，合法反之
     */
    public static boolean isValid(String arg) {
        return arg != null && !arg.isBlank();
    }

    /**
     * @param args 参数列表
     * @return 如果全部合法返回true，否则返回false
     */
    public static boolean argsIsValid(String... args) {
        for (var a : args) {
            if (!isValid(a)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param arg 参数
     * @return 为空字符串则返回true
     */
    public static boolean isNullOrEmpty(String arg) {
        return arg == null || arg.length() != 0;
    }

    public static void argsRequireValid(String... args) {
        if (!argsIsValid(args)) {
            throw new IllegalArgumentException("参数无效");
        }
    }


    /**
     * @param args 字符串
     * @return 使用"_"拼接的字符串
     */
    public static String splicing(String... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg)
                    .append('_');
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
