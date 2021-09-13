package win.panyong.controller;


import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import win.panyong.util.Result;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping(value = "/api", produces = "application/json;charset=UTF-8")
public class IndexController extends BaseController {
    @Autowired
    private IMqttClient mqttClient;

    @RequestMapping(value = "/getAuth")
    String test() throws MqttException {
        Result.Builder resultBuilder = Result.builder();
        mqttClient.publish("hello", "测试".getBytes(StandardCharsets.UTF_8), 1, false);
        Map<String, String> result = commonService.getBlinkerAuth();
        resultBuilder.rspCode(OK).rspInfo("success").putData("authInfo", result);
        return resultBuilder.buildJsonString();
    }

}
