package win.panyong;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import win.panyong.service.CommonService;
import win.panyong.service.impl.MessageArrivedHandler;

import javax.annotation.PreDestroy;
import java.util.Map;

@Configuration
public class MqttConfig {
    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    @Autowired
    private IMqttClient mqttClient;


    @Bean
    public IMqttClient mqttClient(CommonService commonService) throws MqttException {
        Map<String, String> blinkerInfo = commonService.getBlinkerAuth();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(30);
        options.setAutomaticReconnect(true);
        options.setUserName(blinkerInfo.get("userName"));
        options.setPassword(blinkerInfo.get("password").toCharArray());
        String host = "tcp://" + blinkerInfo.get("host") + ":" + blinkerInfo.get("port");
        IMqttClient client = new MqttClient(host, blinkerInfo.get("mqttClientId"), new MemoryPersistence());
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                try {
                    client.subscribe(blinkerInfo.get("subscribeTopic"));
                } catch (MqttException e) {
                    logger.error("mqtt订阅异常:" + e);
                }
                logger.info("MQTT连接成功");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken mqttDeliveryToken) {
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                System.out.println(topic + "--" + new String(mqttMessage.getPayload()));
                MessageArrivedHandler.getByPayload(topic, new String(mqttMessage.getPayload())).doHandler(client, blinkerInfo);
            }
        });
        client.connect(options);
        return client;
    }


//    @Bean
//    public IMqttClient mqttClient(@Value("${mqtt.host}") String mqttHost, @Value("${mqtt.client_id}") String clientId, @Value("${mqtt.username}") String mqttUserName, @Value("${mqtt.password}") String mqttPassword, @Value("${mqtt.topics}") String topics) throws MqttException {
//        MqttConnectOptions options = new MqttConnectOptions();
//        options.setCleanSession(true);
//        options.setConnectionTimeout(60);
//        options.setKeepAliveInterval(30);
//        options.setAutomaticReconnect(true);
////        options.setUserName(mqttUserName);
////        options.setPassword(mqttPassword.toCharArray());
//        IMqttClient client = new MqttClient(mqttHost, clientId, new MemoryPersistence());
//        client.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//                try {
//                    client.subscribe(topics.split(","));
//                } catch (MqttException e) {
//                    logger.error("mqtt订阅异常:" + e);
//                }
//                logger.info("MQTT连接成功");
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken mqttDeliveryToken) {
//            }
//
//            @Override
//            public void connectionLost(Throwable throwable) {
//
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
//                System.out.println("topic:" + topic + "|payload:" + new String(mqttMessage.getPayload()));
//            }
//        });
//        client.connect(options);
//        return client;
//    }

    @PreDestroy
    public void destoryMqttClient() throws MqttException {
        mqttClient.disconnect();
        logger.info("MQTT断开连接");
    }
}
