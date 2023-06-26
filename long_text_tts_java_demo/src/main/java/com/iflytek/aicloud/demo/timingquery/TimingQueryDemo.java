package com.iflytek.aicloud.demo.timingquery;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.iflytek.aicloud.common.config.PropertiesConfig;
import com.iflytek.aicloud.common.handler.ResultHandler;
import com.iflytek.aicloud.common.tool.RequestDataTool;
import com.iflytek.aicloud.demo.AppClient;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimingQueryDemo {
    // 日志工具
    private static final Logger logger = LoggerFactory.getLogger(TimingQueryDemo.class);

    // 关键参数
    private static final String APP_ID = PropertiesConfig.getAppId();
    private static final String API_KEY = PropertiesConfig.getApiKey();
    private static final String API_SECRET = PropertiesConfig.getApiSecret();

    // 定时调度
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

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

    public static void main(String[] args) throws IOException {
        AppClient appClient = new AppClient(API_KEY, API_SECRET);

        // 1.创建任务
        String createUrl = RequestDataTool.getCreateUrl();
        String rawCreateRequestJsonStr = RequestDataTool.getCreateRequestJsonStr();
        // 重构requestData内容,对具体参数重写
        String createRequestData = buildCreateRequestData(JSONObject.parseObject(rawCreateRequestJsonStr));
        // 请求
        String createResp = appClient.doRequest(createRequestData, createUrl);
        // 判断请求结果
        JSONObject createRespObj = JSONObject.parseObject(createResp);
        Object code = JSONPath.eval(createRespObj, "$.header.code");
        if(code != null) {
            if((int)code != 0) {
                // 创建任务失败，打印报错后中止
                logger.error("create task failed, code = {}, message = {}", code, JSONPath.eval(createRespObj, "$.header.message"));
                return ;
            }
        }

        // 取 task id
        String taskId = (String) JSONPath.eval(createRespObj, "$.header.task_id");

        // 2.查询任务
        String queryUrl = RequestDataTool.getQueryUrl();
        String rawQueryRequestJsonStr = RequestDataTool.getQueryRequestJsonStr();
        // 重构requestData内容,对具体参数重写
        String queryRequestData = buildQueryRequestData(JSONObject.parseObject(rawQueryRequestJsonStr), taskId);

        // 定时调度查询任务
        SCHEDULER.scheduleWithFixedDelay(() -> {
            int taskStatus;
            JSONObject queryRespObj;
            // 请求
            String queryResp = appClient.doRequest(queryRequestData, queryUrl);
            // 判断请求结果
            queryRespObj = JSONObject.parseObject(queryResp);
            Object qryCode = JSONPath.eval(queryRespObj, "$.header.code");
            if (qryCode != null) {
                if ((int) qryCode != 0) {
                    logger.error("query task failed, code = {}, message = {}", qryCode, JSONPath.eval(queryRespObj, "$.header.message"));
                    SCHEDULER.shutdown();
                }
            }
            // 判断任务状态
            taskStatus = Integer.parseInt((String) JSONPath.eval(queryRespObj, "$.header.task_status"));
            if (taskStatus == 5) {
                // 处理结果数据
                ResultHandler.respDataPostProcess(queryRespObj);
                // 任务完成，关闭调度器
                SCHEDULER.shutdown();
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    /**
     * 构造请求数据
     * create
     */
    private static String buildCreateRequestData(JSONObject requestData) throws IOException {
        // 重写app_id和request_id
        JSONPath.set(requestData, "$.header.app_id", APP_ID);
        JSONPath.set(requestData, "$.header.request_id", UUID.randomUUID());
        // 请按照需要对其他参数进行重写，本例中选择把结果音频编码格式指定为lame，即可通过主流的音频解码器播放试听
        JSONPath.set(requestData, "$.parameter.dts.audio.encoding", "lame");

        File file = new File(resourcePath + RequestDataTool.getInputFilePath());
        JSONPath.set(requestData, "$.payload.text.text", Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file)));
        return requestData.toString();
    }

    /**
     * 构造请求数据
     * query
     */
    private static String buildQueryRequestData(JSONObject requestData, String taskId) {
        // 参数重写
        JSONPath.set(requestData, "$.header.app_id", APP_ID);
        JSONPath.set(requestData, "$.header.task_id", taskId);
        return requestData.toString();
    }

}
