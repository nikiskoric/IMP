package apidriver;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.config.ChannelScanInfo;
import org.openmuc.framework.config.DriverInfo;
import org.openmuc.framework.config.ScanException;
import org.openmuc.framework.config.ScanInterruptedException;
import org.openmuc.framework.data.DoubleValue;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.data.Value;
import org.openmuc.framework.driver.spi.ChannelRecordContainer;
import org.openmuc.framework.driver.spi.ChannelValueContainer;
import org.openmuc.framework.driver.spi.Connection;
import org.openmuc.framework.driver.spi.ConnectionException;
import org.openmuc.framework.driver.spi.DriverDeviceScanListener;
import org.openmuc.framework.driver.spi.DriverService;
import org.openmuc.framework.driver.spi.RecordsReceivedListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apidriverlib.ApiDriverApi;
import apidriverlib.ApiDriverSettings;
import apidriverlib.data.ApiDriverData;
import apidriverlib.data.ApiDriverDataCollector;

@Component
public class ApiDriver implements DriverService, Connection {
	
    private static final Logger logger = LoggerFactory.getLogger(ApiDriver.class);
    private String deviceAddress;
    
    private ApiDriverSettings driverSettings = null;
    private ApiDriverApi driverApi = null;
    private ApiDriverDataCollector driverDataCollector = null;

    @Activate
    void activate() {
        logger.info("Activated API driver");
    }
    
    @Override
    public DriverInfo getInfo() {
        String description = "Driver for connecting to an API and retrieving data";
        String deviceAddressSyntax = "Not used";
        String settingSyntax = "For API endpoint, authentication details, and request parameters";
        String channelAddressSyntax = "For specifying API resource paths or query parameters";
        String deviceScanSettingSyntax = "Not used";

        return new DriverInfo("ApiDriver", description, deviceAddressSyntax, settingSyntax, channelAddressSyntax,
                deviceScanSettingSyntax);
    }

	@Override
	public void scanForDevices(String settings, DriverDeviceScanListener listener)
			throws UnsupportedOperationException, ArgumentSyntaxException, ScanException, ScanInterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void interruptDeviceScan() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Connection connect(String deviceAddress, String settings)
			throws ArgumentSyntaxException, ConnectionException {
		
		this.setDeviceAddress(deviceAddress);
		try {
			driverSettings = ApiDriverSettings.getDeviceSettings(settings);
			driverApi = new ApiDriverApi(driverSettings);
			driverDataCollector = new ApiDriverDataCollector(driverApi);
		} catch (IOException e) {
            throw new ConnectionException("Failed to initialize API driver", e);
		}
		return this;
	}
	
	@Override
	public List<ChannelScanInfo> scanForChannels(String settings)
			throws UnsupportedOperationException, ArgumentSyntaxException, ScanException, ConnectionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object read(List<ChannelRecordContainer> containers, Object containerListHandle, String samplingGroup)
			throws UnsupportedOperationException, ConnectionException {

		if (driverApi == null) {
            throw new ConnectionException("API driver is not initialized");
        }
        try {
            for (ChannelRecordContainer container : containers) {
                String channelAddress = container.getChannel().getChannelAddress();
                ApiDriverData data = driverApi.getDataFromChannel(channelAddress);
                driverDataCollector.addRecord(channelAddress, data);
                
                Value value = new DoubleValue((double) data.getTemperature());
                Record record = new Record(value, Instant.now().toEpochMilli());
                container.setRecord(record);
            }
        } catch (IOException | InterruptedException e) {
            throw new ConnectionException("Error fetching data from API", e);
        }
        
        return null;
	}

	@Override
	public void startListening(List<ChannelRecordContainer> containers, RecordsReceivedListener listener)
			throws UnsupportedOperationException, ConnectionException {
		throw new ConnectionException();
	}

	@Override
	public Object write(List<ChannelValueContainer> containers, Object containerListHandle)
			throws UnsupportedOperationException, ConnectionException {
		throw new ConnectionException();
	}

	@Override
	public void disconnect() {
		// does not need to be disconnected
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

}
