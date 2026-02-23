package com.api.account.utils;

import java.security.SecureRandom;

public class RandomUtil {
    public static String random6Digits() {
        SecureRandom rand = new SecureRandom();
        int number = 100000 + rand.nextInt(900000);
        return String.valueOf(number);
    }
}
