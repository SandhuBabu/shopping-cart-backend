package com.shoppingcart.utils;

import java.util.Date;

public class DateUtils {
    public static Long getDateDifference(Date from, Date to) {
        long timeDifferenceInMillis = Math.abs(from.getTime() - to.getTime());

        // Calculate days, hours, minutes, and seconds
        long seconds = timeDifferenceInMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days;
    }
}
