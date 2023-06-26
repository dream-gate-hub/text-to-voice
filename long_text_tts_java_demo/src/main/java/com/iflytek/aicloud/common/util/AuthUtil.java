package com.iflytek.aicloud.common.util;

import com.iflytek.aicloud.common.sign.Hmac256Signature;
import okhttp3.HttpUrl;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SignatureException;
import java.util.Objects;

/**
 * 生成签名后的url工具类
 */
public class AuthUtil {
    private static final String ALGORITHM = "hmac-sha256";

    public static String generateAuthorization(Hmac256Signature signature) throws SignatureException {
        return generateAuthorization(signature, ALGORITHM);
    }

    public static String generateAuthorization(Hmac256Signature signature, String algorithm) throws SignatureException {
        return String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", signature.getId(), algorithm, "host date request-line", signature.getSignature());
    }

    public static String generateRequestUrl(Hmac256Signature signature) throws MalformedURLException, SignatureException {
        URL url = new URL(signature.getUrl());
        String authorization = generateAuthorization(signature);
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().addQueryParameter("authorization", CryptUtil.base64Encode(authorization)).addQueryParameter("date", signature.getTs()).addQueryParameter("host", url.getHost()).build();
        return httpUrl.toString();
    }
}
