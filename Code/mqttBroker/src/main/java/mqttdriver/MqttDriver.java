package mqttdriver;

import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.config.DriverInfo;
import org.openmuc.framework.config.ScanException;
import org.openmuc.framework.config.ScanInterruptedException;
import org.openmuc.framework.driver.spi.Connection;
import org.openmuc.framework.driver.spi.ConnectionException;
import org.openmuc.framework.driver.spi.DriverDeviceScanListener;
import org.openmuc.framework.driver.spi.DriverService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MqttDriver implements DriverService {

	  private static final Logger logger = LoggerFactory.getLogger(MqttDriver.class);

	  @Activate
	    void activate() {
	        logger.info("Activated MQTT driver");
	    }
	  
	  @Override
	  public DriverInfo getInfo() {
	      String description = "Driver for connecting to MQTT broker and publishing/subscribing to topics";
	      String deviceAddressSyntax = "Not used";
	      String settingSyntax = "For MQTT broker address, username, and password info";
	      String channelAddressSyntax = "For specifying MQTT topics";
	      String deviceScanSettingSyntax = "Not used";

	      return new DriverInfo("MqttDriver", description, deviceAddressSyntax, settingSyntax, channelAddressSyntax,
	              deviceScanSettingSyntax);
	  }

	    @Override
	    public void scanForDevices(String settings, DriverDeviceScanListener listener)
	            throws UnsupportedOperationException, ArgumentSyntaxException, ScanException, ScanInterruptedException {
	        throw new UnsupportedOperationException("Device scanning not supported for MQTT driver.");
	    }

	    @Override
	    public void interruptDeviceScan() throws UnsupportedOperationException {
	        // No operation needed as scan is not supported
	    }

	    @Override
	    public Connection connect(String deviceAddress, String settings)
	            throws ArgumentSyntaxException, ConnectionException {
	        try {
	        	if(deviceAddress == null) 
	        		throw new ArgumentSyntaxException("Device address is null");
	        	
	            MqttConnection connection = new MqttConnection(deviceAddress, settings);
				connection.connect(settings);
				return connection;
	        } catch (Exception e) {
	            logger.error("Failed to create MQTT connection: {}", e.getMessage());
	            throw new ConnectionException("Could not establish MQTT connection.", e);
	        }
	    }

}
