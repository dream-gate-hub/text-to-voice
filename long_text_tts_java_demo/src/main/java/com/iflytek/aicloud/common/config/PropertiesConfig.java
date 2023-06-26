package com.iflytek.aicloud.common.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Properties;

/**
 * 配置类
 */
public class PropertiesConfig {
    private static final String APP_ID;
    private static final String API_KEY;
    private static final String API_SECRET;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(
                    Objects.requireNonNull(PropertiesConfig.class.getResource("/")).toURI().getPath() + "config.properties"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("加载配置文件失败");
        }
        APP_ID = properties.getProperty("appId");
        API_SECRET = properties.getProperty("apiSecret");
        API_KEY = properties.getProperty("apiKey");
    }

    public static String getAppId() {
        return APP_ID;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getApiSecret() {
        return API_SECRET;
    }
}
