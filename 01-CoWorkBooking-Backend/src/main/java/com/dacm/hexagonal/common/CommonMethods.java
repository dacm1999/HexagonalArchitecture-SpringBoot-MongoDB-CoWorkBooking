package com.dacm.hexagonal.common;


public class CommonMethods {

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }


    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}

