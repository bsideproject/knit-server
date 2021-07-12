package com.project.knit.utils;

import java.util.Base64;

public class StringUtils {
    public static String decodeString(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        return decodedString;
    }
}
