package com.qrcodecopier.qrscanner.utils;

public final class Utils {

    private Utils(){}

    public static String buildRequestBody(String token, String url){
        return "{\"token\":\"" + token + "\", \"url\":\"" + url + "\"}";
    }
}
