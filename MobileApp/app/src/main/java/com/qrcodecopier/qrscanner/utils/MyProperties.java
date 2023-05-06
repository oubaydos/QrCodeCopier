package com.qrcodecopier.qrscanner.utils;

import com.qrcodecopier.qrscanner.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class MyProperties {

    Properties properties;

    private static MyProperties instance;

    private MyProperties(){
    }

    public static MyProperties getInstance(){
        if (instance == null)
            instance = new MyProperties();
        return instance;
    }

    public String getServerURl(){
        return BuildConfig.SERVER_URL;
    }
}
