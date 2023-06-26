package com.iflytek.aicloud.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.iflytek.aicloud.common.util.HttpUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Objects;

/**
 * 处理结果数据的类
 */
public class ResultHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResultHandler.class);

    private static String resourcePath;

    static {
        try {
            resourcePath = Objects.requireNonNull(ResultHandler.class.getResource("/")).toURI().getPath();
            if (resourcePath != null) {
                resourcePath = resourcePath.replaceAll("target/classes", "src/main/resources");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void clear(){
        File resourceRootPath = new File(resourcePath + "output");
        if (resourceRootPath.exists() && resourceRootPath.isDirectory()) {
            try {
                FileUtils.cleanDirectory(resourceRootPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void respDataPostProcess(JSONObject respData){
        clear();
        if (respData == null || respData.isEmpty()) {
            return;
        }

        String taskId = (String) JSONPath.eval(respData, "$.header.task_id");
        String audioBase64 = (String) JSONPath.eval(respData, "$.payload.audio.audio");
        String encoding = (String) JSONPath.eval(respData, "$.payload.audio.encoding");

        byte[] decode = Base64.getDecoder().decode(audioBase64);
        String audioUrl = new String(decode);
        logger.info("audio download url = {}", audioUrl);

        // 下载结果数据并写成文件，本例中选择用 task id 作为文件名称
        byte[] bytes = HttpUtil.getBytes(audioUrl);
        String filePath = resourcePath + "output" + File.separator + taskId + "." + encoding;
        logger.info("audio save path = {}", filePath);
        try {
            FileUtils.writeByteArrayToFile(new File(filePath), bytes, false);
        } catch (IOException e) {
            logger.error("write file failed", e);
        }

    }

}
