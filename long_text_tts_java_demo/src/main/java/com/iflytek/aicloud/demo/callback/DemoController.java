package com.iflytek.aicloud.demo.callback;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.iflytek.aicloud.common.config.PropertiesConfig;
import com.iflytek.aicloud.common.handler.ResultHandler;
import com.iflytek.aicloud.common.tool.RequestDataTool;
import com.iflytek.aicloud.demo.AppClient;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@RestController
public class DemoController {
    // 日志工具
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    // 关键参数
    private static final String APP_ID = PropertiesConfig.getAppId();
    private static final String API_KEY = PropertiesConfig.getApiKey();
    private static final String API_SECRET = PropertiesConfig.getApiSecret();

    // 资源路径
    private static String resourcePath;

    // 初始化 resourcePath
    static {
        try {
            resourcePath = Objects.requireNonNull(AppClient.class.getResource("/")).toURI().getPath();
            if (resourcePath != null) {
                resourcePath = resourcePath.replaceAll("target/classes", "src/main/resources");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/create")
    void createTask() throws IOException {
        AppClient appClient = new AppClient(API_KEY, API_SECRET);

        String createUrl = RequestDataTool.getCreateUrl();
        String rawCreateRequestJsonStr = RequestDataTool.getCreateRequestJsonStr();
        // 重构requestData内容,对具体参数重写
        String createRequestData = buildCreateRequestData(JSONObject.parseObject(rawCreateRequestJsonStr));
        // 请求
        String createResp = appClient.doRequest(createRequestData, createUrl);
        // 处理结果数据
        JSONObject createRespObj = JSONObject.parseObject(createResp);
        Object code = JSONPath.eval(createRespObj, "$.header.code");
        if(code != null) {
            if((int)code != 0) {
                logger.error("create task failed, code = {}, message = {}", code, JSONPath.eval(createRespObj, "$.header.message"));
            }
            else {
                logger.info("create task succeed, waiting for callback");
            }
        }
    }

    @PostMapping("/callback")
    void callback(@RequestBody JSONObject callbackJson) {
        logger.info("the callback is triggered, request body = {}", callbackJson);
        Object code = JSONPath.eval(callbackJson, "$.header.code");
        if (code != null) {
            if ((int) code != 0) {
                logger.error("callback task failed, code = {}, message = {}", code, JSONPath.eval(callbackJson, "$.header.message"));
            }
        }
        // 判断任务状态
        int taskStatus = Integer.parseInt((String) JSONPath.eval(callbackJson, "$.header.task_status"));
        if (taskStatus == 5) {
            // 任务完成，处理结果
            ResultHandler.respDataPostProcess(callbackJson);
        }
    }

    @Value("${biz.callback.url}")
    String callbackUrl;

    /**
     * 构造请求数据
     * create
     */
    private String buildCreateRequestData(JSONObject requestData) throws IOException {
        // 重写app_id和request_id，回调地址
        JSONPath.set(requestData, "$.header.app_id", APP_ID);
        JSONPath.set(requestData, "$.header.request_id", UUID.randomUUID());
        JSONPath.set(requestData, "$.header.callback_url", callbackUrl);
        // 请按照需要对其他参数进行重写，本例中选择把结果音频编码格式指定为lame，即可通过主流的音频解码器播放试听
        JSONPath.set(requestData, "$.parameter.dts.audio.encoding", "lame");

        File file = new File(resourcePath + RequestDataTool.getInputFilePath());
        JSONPath.set(requestData, "$.payload.text.text", Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file)));
        return requestData.toString();
    }
}
