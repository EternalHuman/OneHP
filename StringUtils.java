package ru.eternalhuman.streamers.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    public static String formatWord(int num, String s1, String s2, String s3) {

        int preLastDigit = num % 100 / 10;

        if (preLastDigit == 1) {
            return s3;
        }

        switch (num % 10) {
            case 1:
                return s1;
            case 2:
            case 3:
            case 4:
                return s2;
            default:
                return s3;
        }
    }
}
