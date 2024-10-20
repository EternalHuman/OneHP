package ru.eternalhuman.streamers.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    public static String formatWord(int num, String s1, String s2, String s3) {

        int preLastDigit = num % 100 / 10;

        if (preLastDigit == 1) {
            return s3;
        }

        return switch (num % 10) {
            case 1 -> s1;
            case 2, 3, 4 -> s2;
            default -> s3;
        };
    }
}
