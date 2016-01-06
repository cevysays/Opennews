package com.openetizen.cevysays.opennews.util;

import android.content.Context;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cevyyufindra on 11/22/15.
 */
public class Utility {
    private static Pattern pattern;
    private static Matcher matcher;
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    /**
     * Validate Email with regular expression
     *
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Checks for Null String object
     *
     * @param txt
     * @return true for not null and false for null String object
     */
    public static boolean isNotNull(String txt) {

        return txt != null && txt.trim().length() > 0 ? true : false;

    }

    /**
     * Checks for Null String object
     *
     * @param spinner
     * @return true for not null and false for null String object
     */
    public static boolean isNotNull(String spinner, Context context) {

        if (spinner != "") {
            return true;
        } else {
            Toast.makeText(context, "Kategori tidak boleh kosong!", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    /**
     * Checks for Null String object
     *
     * @param file
     * @return true for not null and false for null String object
     */
    public static boolean isNotNull(File file, Context context) {

        if (file.length() == 0) {
            // empty or doesn't exist
            Toast.makeText(context, "Foto tidak boleh kosong!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
