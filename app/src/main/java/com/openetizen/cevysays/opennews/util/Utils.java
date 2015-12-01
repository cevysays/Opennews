package com.openetizen.cevysays.opennews.util;

/**
 * Created by cevyyufindra on 11/30/15.
 */
public class Utils {

    public static boolean isInitialized = false;

    public static String convertCalendar(int month){
        String monthString = "";

        switch (month-1){
            case 0 :
                monthString = "Januari";
                break;
            case 1 :
                monthString = "Februari";
                break;
            case 2 :
                monthString = "Maret";
                break;
            case 3 :
                monthString = "April";
                break;
            case 4 :
                monthString = "Mei";
                break;
            case 5 :
                monthString = "Juni";
                break;
            case 6 :
                monthString = "Juli";
                break;
            case 7 :
                monthString = "Agustus";
                break;
            case 8 :
                monthString = "September";
                break;
            case 9 :
                monthString = "Oktober";
                break;
            case 10 :
                monthString = "November";
                break;
            case 11 :
                monthString = "Desember";
                break;
        }


        return  monthString;
    }
}
