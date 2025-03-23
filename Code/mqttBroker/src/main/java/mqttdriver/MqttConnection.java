package mqttdriver;

import java.util.Date;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.config.ChannelScanInfo;
import org.openmuc.framework.config.ScanException;
import org.openmuc.framework.data.ByteArrayValue;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.driver.spi.ChannelRecordContainer;
import org.openmuc.framework.driver.spi.ChannelValueContainer;
import org.openmuc.framework.driver.spi.Connection;
import org.openmuc.framework.driver.spi.ConnectionException;
import org.openmuc.framework.driver.spi.RecordsReceivedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttConnection implements Connection, MqttCallback {

	  private static final Logger logger = LoggerFactory.getLogger(MqttConnection.class);

	    public MqttClient mqttClient;
	    private List<ChannelRecordContainer> containers;
	    private RecordsReceivedListener listener;

	    public MqttConnection(String brokerUrl, String settings) throws MqttException {
	        mqttClient = new MqttClient(brokerUrl, MqttClient.generateClientId());
	        mqttClient.setCallback(this);
	    }

	    public void connect(String settings) throws MqttException {
	        MqttConnectOptions options = new MqttConnectOptions();
	        options.setCleanSession(true);
	        mqttClient.connect(options);
	        logger.info("Connected to MQTT broker at {}", mqttClient.getServerURI());
	    }

	    @Override
	    public void connectionLost(Throwable cause) {
	        logger.warn("Connection lost with MQTT broker: {}", cause.getMessage());
	        if (listener != null) {
	            listener.connectionInterrupted("mqtt", this);
	        }
	    }

	    @Override
	    public void messageArrived(String topic, MqttMessage message) throws Exception {
	        logger.info("Message arrived on topic {}: {}", topic, new String(message.getPayload()));

	        long timestamp = new Date().getTime();
	        for (ChannelRecordContainer container : containers) {
	            if (container.getChannelAddress().equals(topic)) {
	                container.setRecord(new Record(new ByteArrayValue(message.getPayload()), timestamp));
	                break;
	            }
	        }
	        if (listener != null) {
	            listener.newRecords(containers);
	        }
	    }

	    @Override
	    public void deliveryComplete(IMqttDeliveryToken token) {
	        logger.debug("Message delivery complete for token: {}", token.getMessageId());
	    }

	    @Override
	    public List<ChannelScanInfo> scanForChannels(String settings)
	            throws UnsupportedOperationException, ArgumentSyntaxException, ScanException, ConnectionException {
	        throw new UnsupportedOperationException("Channel scanning not supported.");
	    }

	    @Override
	    public Object read(List<ChannelRecordContainer> containers, Object containerListHandle, String samplingGroup)
	            throws UnsupportedOperationException, ConnectionException {
	        throw new UnsupportedOperationException("Read operation not supported.");
	    }

	    @Override
	    public void startListening(List<ChannelRecordContainer> containers, RecordsReceivedListener listener)
	            throws UnsupportedOperationException, ConnectionException {
	        if (!mqttClient.isConnected()) {
	            throw new ConnectionException("MQTT client is not connected.");
	        }

	        this.containers = containers;
	        this.listener = listener;

	        for (ChannelRecordContainer container : containers) {
	            try {
	                mqttClient.subscribe(container.getChannelAddress());
	                logger.info("Subscribed to topic: {}", container.getChannelAddress());
	            } catch (MqttException e) {
	                throw new ConnectionException("Failed to subscribe to topic: " + container.getChannelAddress(), e);
	            }
	        }
	    }

	    @Override
	    public Object write(List<ChannelValueContainer> containers, Object containerListHandle)
	            throws UnsupportedOperationException, ConnectionException {
	        for (ChannelValueContainer container : containers) {
	            if (container.getValue() != null) {
	                String topic = container.getChannelAddress();
	                String payload = container.getValue().asString();

	                try {
	                    mqttClient.publish(topic, new MqttMessage(payload.getBytes()));
	                    logger.info("Published message to topic {}: {}", topic, payload);
	                    container.setFlag(org.openmuc.framework.data.Flag.VALID);
	                } catch (MqttException e) {
	                    throw new ConnectionException("Failed to publish message to topic: " + topic, e);
	                }
	            } else {
	                container.setFlag(org.openmuc.framework.data.Flag.CANNOT_WRITE_NULL_VALUE);
	            }
	        }
	        return null;
	    }

	    @Override
	    public void disconnect() {
	        if (mqttClient.isConnected()) {
	            try {
	                mqttClient.disconnect();
	                logger.info("Disconnected from MQTT broker.");
	            } catch (MqttException e) {
	                logger.warn("Error while disconnecting: {}", e.getMessage());
	            }
	        }
	    }

}
