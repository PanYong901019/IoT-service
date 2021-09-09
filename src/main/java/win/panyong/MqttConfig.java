package win.panyong;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
public class MqttConfig {
    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    @Autowired
    private IMqttClient mqttClient;

    @Bean
    public IMqttClient mqttClient(@Value("${mqtt.host}") String mqttHost, @Value("${mqtt.client_id}") String clientId, @Value("${mqtt.username}") String mqttUserName, @Value("${mqtt.password}") String mqttPassword, @Value("${mqtt.topics}") String topics) throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(20);
        options.setAutomaticReconnect(true);
//        options.setUserName(mqttUserName);
//        options.setPassword(mqttPassword.toCharArray());
        IMqttClient client = new MqttClient(mqttHost, clientId);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                try {
                    client.subscribe(topics.split(","));
                } catch (MqttException e) {
                    logger.error("mqtt订阅异常:" + e);
                }
                logger.info("重新链接成功");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken mqttDeliveryToken) {
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                System.out.println("topic:" + topic + "|payload:" + new String(mqttMessage.getPayload()));
            }
        });
        client.connect(options);
        return client;
    }

    @PreDestroy
    public void destoryMqttClient() throws MqttException {
        mqttClient.disconnect();
    }
}
