package win.panyong.controller;


import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import win.panyong.util.Result;
import win.panyong.util.authority.util.JwtUtil;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(value = "/api", produces = "application/json;charset=UTF-8")
public class IndexController extends BaseController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private IMqttClient mqttClient;

    @RequestMapping(value = "/test")
    String test() throws MqttException {
        Result.Builder resultBuilder = Result.builder();
        mqttClient.publish("hello", "测试".getBytes(StandardCharsets.UTF_8), 1, false);
        resultBuilder.rspCode(OK).rspInfo("success");
        return resultBuilder.buildJsonString();
    }


    @RequestMapping(value = "/test1")
    String test1() throws MqttException {
        Result.Builder resultBuilder = Result.builder();
        mqttClient.subscribe("hello", 2);
        resultBuilder.rspCode(OK).rspInfo("success");
        return resultBuilder.buildJsonString();
    }

}
