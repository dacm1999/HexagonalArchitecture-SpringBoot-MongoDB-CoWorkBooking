package com.dacm.hexagonal.common;


import com.dacm.hexagonal.domain.exception.RegisterErrorResponse;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }


    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}

