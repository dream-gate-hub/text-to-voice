package com.iflytek.aicloud.common.tool;

/**
 * 业务请求数据工具
 */
public class RequestDataTool {
    /**
     * 创建任务的请求地址
     */
    private static final String CREATE_URL = "https://api-dx.xf-yun.com/v1/private/dts_create";

    /**
     * 查询任务的请求地址
     */
    private static final String QUERY_URL = "https://api-dx.xf-yun.com/v1/private/dts_query";

    /**
     * 输入文本文件路径
     */
    private static final String INPUT_FILE_PATH = "input/text/example.txt";

    public static String getCreateUrl() {
        return CREATE_URL;
    }

    public static String getQueryUrl() {
        return QUERY_URL;
    }

    public static String getInputFilePath() {
        return INPUT_FILE_PATH;
    }
    /**
    * 获取Json字符串形式的请求协议
    * 创建任务
    */
    public static String getCreateRequestJsonStr() {
        // 这里给出协议的整体，需要修改的参数值可以先给xxx，之后再通过JSONPath重写即可
        return "{\n" +
                "    \"header\":{\n" +
                "        \"app_id\":\"xxx\",\n" +
                "        \"callback_url\":\"xxx\",\n" +
                "        \"request_id\":\"xxx\"\n" +
                "    },\n" +
                "    \"parameter\":{\n" +
                "        \"dts\":{\n" +
                "            \"vcn\": \"x4_yeting\",\n" +
                "            \"speed\": 50,\n" +
                "            \"volume\": 50,\n" +
                "            \"pitch\": 50,\n" +
                "            \"bgs\": 0,\n" +
                "            \"reg\": 0,\n" +
                "            \"rdn\": 0,\n" +
                "            \"rhy\": 0,\n" +
                "            \"scn\": 0,\n" +
                "            \"pybuf\":{\n" +
                "                \"encoding\":\"utf8\",\n" +
                "                \"compress\":\"raw\",\n" +
                "                \"format\":\"plain\"\n" +
                "            },\n" +
                "            \"audio\":{\n" +
                "                \"encoding\":\"speex-wb\",\n" +
                "                \"sample_rate\":16000,\n" +
                "                \"channels\":1,\n" +
                "                \"bit_depth\":16\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"payload\":{\n" +
                "        \"text\":{\n" +
                "            \"encoding\":\"utf8\",\n" +
                "            \"compress\":\"raw\",\n" +
                "            \"format\":\"plain\",\n" +
                "            \"text\":\"xxx\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    /**
     * 获取Json字符串形式的请求协议
     * 查询任务
     */
    public static String getQueryRequestJsonStr() {
        return "{\n" +
                "    \"header\":{\n" +
                "        \"app_id\":\"xxx\",\n" +
                "        \"task_id\":\"xxx\"\n" +
                "    }\n" +
                "}";
    }

}