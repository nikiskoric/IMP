package org.openmuc.framework.app.ems;

import org.eclipse.paho.client.mqttv3.*;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {MqttClientImpl.class})
public class MqttClientImpl {

    private static final String BROKER = "tcp://localhost:1883";
    private static final String CLIENT_ID = "EMSClient";
    private static final String TOPIC = "weather/topic";
    private static final int QOS = 1;
    
    private MqttClient client;
    private static final Logger logger = LoggerFactory.getLogger(MqttClientImpl.class);
    
    public void connectClient() {
    	try {
			client = new MqttClient(BROKER, CLIENT_ID);
			
	        MqttConnectOptions options = new MqttConnectOptions();
	        options.setCleanSession(false);

	        client.connect(options);
	        logger.info("Client connected!");
	        
		} catch (MqttException e) {
			e.printStackTrace();
		}
    }
    
    public void disconnectClient() {
        try {
			client.disconnect();
	        client.close();
	        logger.info("Client disconnected!");
	        
		} catch (MqttException e) {
			e.printStackTrace();
		}
    }
    
    public void publishMessage(String messageString) {
        try {
            MqttMessage message = new MqttMessage(messageString.getBytes());
            message.setQos(QOS);
            client.publish(TOPIC, message);
            
            logger.info("Message published to topic: " + TOPIC);
	        
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
