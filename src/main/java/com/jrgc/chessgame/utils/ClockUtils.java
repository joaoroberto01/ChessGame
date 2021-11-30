package com.jrgc.chessgame.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockUtils {
    public static String clockFormat(long seconds){
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    public static String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return simpleDateFormat.format(new Date());
    }

    public static String toFormattedDate(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        try {
            Date date = simpleDateFormat.parse(dateString);
            return new SimpleDateFormat("dd/MM/yyyy").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTimestamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH.mm.ss");
        return simpleDateFormat.format(new Date());
    }
}
