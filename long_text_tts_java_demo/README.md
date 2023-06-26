# long_tts_java_demo

本demo为大文本语音合成的java示例项目。

本demo包括主动查询任务状态和被动接收回调两种场景的示例代码。



##项目结构说明

com.iflytek.aicloud.common下为通用的类，包括属性配置、结果处理、加密签名、工具等等。

com.iflytek.aicloud.demo下为调用服务流程的示例代码，其中callback包下实现了一个SpringBoot服务，可以以接收回调的方式完成流程；timingquery包下实现了一个普通程序，通过主动轮询任务状态的方式完成流程。



##自定义内容

demo中有部分配置和内容可以(或需要)由用户自行指定或定义，包括但不限于如下：

1.文本文件输入路径。因为文本内容较大，本例单独将文本放置在了classpath:input/text目录下的文件中

2.音频文件写回路径。本例调用服务后得到的结果音频会写到classpath:output目录下

3.关键配置。appId、apiKey、apiSecret定义在classpath:config.properties中
