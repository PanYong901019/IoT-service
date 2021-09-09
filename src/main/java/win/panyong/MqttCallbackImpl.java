package win.panyong;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCallbackImpl implements MqttCallbackExtended {

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println(reconnect);
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken mqttDeliveryToken) {

    }
}
