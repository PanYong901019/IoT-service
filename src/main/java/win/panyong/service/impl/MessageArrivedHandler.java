package win.panyong.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import win.panyong.utils.ObjectUtil;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public enum MessageArrivedHandler {
    get("get") {
        @Override
        public void doHandler(IMqttClient client, Map<String, String> blinkerInfo) {
            this.mqttClient = client;
            this.blinkerInfo = blinkerInfo;
            System.out.println(getKey() + ":" + payload.getJSONObject("data").getString(getKey()));
            messageResponse("vAssistant", "{\"state\":\"connect\"}", payload.getString("toDevice"), payload.getString("fromDevice"));
        }
    },
    set("set") {
        @Override
        public void doHandler(IMqttClient client, Map<String, String> blinkerInfo) {
            this.mqttClient = client;
            this.blinkerInfo = blinkerInfo;
            System.out.println(getKey() + ":" + payload.getJSONObject("data").getString(getKey()));
        }
    },
    test1("btn1") {
        @Override
        public void doHandler(IMqttClient client, Map<String, String> blinkerInfo) {
            this.mqttClient = client;
            this.blinkerInfo = blinkerInfo;
            System.out.println(getKey() + ":" + payload.getJSONObject("data").getString(getKey()));
        }
    },
    test2("btn2") {
        @Override
        public void doHandler(IMqttClient client, Map<String, String> blinkerInfo) {
            this.mqttClient = client;
            this.blinkerInfo = blinkerInfo;
            System.out.println(getKey() + ":" + payload.getJSONObject("data").getString(getKey()));
        }
    };
    private final String key;
    protected IMqttClient mqttClient;
    protected Map<String, String> blinkerInfo;
    protected JSONObject payload;
    protected String topic;

    MessageArrivedHandler(String key) {
        this.key = key;
    }

    public static MessageArrivedHandler getByPayload(String topic, String payloadJsonString) {
        JSONObject payload = ObjectUtil.jsonStringToJsonObject(payloadJsonString);
        JSONObject data = payload.getJSONObject("data");
        MessageArrivedHandler handler = Arrays.stream(MessageArrivedHandler.values()).filter(messageArrivedHandler -> data.containsKey(messageArrivedHandler.getKey())).findFirst().orElse(null);
        if (handler != null) {
            handler.setPayload(payload);
            handler.setTopic(topic);
        }
        return handler;
    }

    protected void messageResponse(String deviceType, String dataJson, String fromDevice, String toDevice) {
        try {
            String messageJson = "{\"deviceType\":\"" + deviceType + "\",\"data\":" + dataJson + ",\"fromDevice\":\"" + fromDevice + "\",\"toDevice\":\"" + toDevice + "\"}";
            String[] split = topic.split("/");
            String publishTopic = split[0] + "/"+split[1] + "/"+split[2] + "/"+"srpc/"+split[4] + "/"+split[5] + "/";
            mqttClient.publish(publishTopic, messageJson.getBytes(StandardCharsets.UTF_8), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public String getKey() {
        return key;
    }

    public MessageArrivedHandler setPayload(JSONObject payload) {
        this.payload = payload;
        return this;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public abstract void doHandler(IMqttClient client, Map<String, String> blinkerInfo);
}
