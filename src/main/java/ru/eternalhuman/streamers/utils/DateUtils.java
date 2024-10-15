package ru.eternalhuman.streamers.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class DateUtils {

    public static String getTranslatedDate(long millis) {
        if (millis < 500) {
            return "0 сек.";
        }
        final boolean secondsAdd = millis <= 2000;

        final long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days != 0) {
            sb.append(days)
                    .append(" ").append(StringUtils.formatWord((int) days, "день", "дня", "дней")).append(" ");
        }
        if (hours != 0) {
            sb.append(hours)
                    .append(" ").append(StringUtils.formatWord((int) hours, "час", "часа", "часов")).append(" ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(" мин. ");
        }
        if (seconds > 0 || secondsAdd) {
            sb.append(seconds).append(" сек. ");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
