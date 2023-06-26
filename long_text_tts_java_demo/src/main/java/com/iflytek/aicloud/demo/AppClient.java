package com.iflytek.aicloud.demo;

import com.iflytek.aicloud.common.sign.Hmac256Signature;
import com.iflytek.aicloud.common.util.AuthUtil;
import com.iflytek.aicloud.common.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.security.SignatureException;

/**
 * 调用请求的客户端
 */
public class AppClient {
    private static final Logger logger = LoggerFactory.getLogger(AppClient.class);

    private final String apiKey;
    private final String apiSecret;

    public AppClient(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String doRequest(String requestData, String requestUrl) {
        String authRequestUrl = buildAuthRequestUrl(requestUrl);
        logger.info("request url = {}, data = {}", requestUrl, requestData);
        byte[] bytes = HttpUtil.postBytes(authRequestUrl, requestData);
        String respData = new String(bytes);
        logger.info("response data = {}", respData);
        return respData;
    }

    /**
     * 生成鉴权接口url
     */
    private String buildAuthRequestUrl(String requestUrl){
        Hmac256Signature signature = new Hmac256Signature(this.apiKey, this.apiSecret, requestUrl, "POST");
        try {
            return AuthUtil.generateRequestUrl(signature);
        } catch (MalformedURLException | SignatureException e) {
            throw new RuntimeException("buildAuthRequestUrl failed, message : " + e.getMessage());
        }
    }

}
