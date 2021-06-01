package com.megumi.util;


import java.util.Random;

/**
 * 2021/2/23
 *
 * @author miyabi
 * @since 1.0
 */
public class VerificationCode {

    private final static int CODE_LENGTH = 6;

    public static String getCode() {
        var random = new Random();
        var code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            var temp = random.nextInt(10);
            code.append(temp);
        }
        return code.toString();
    }
}
