package apidriver;

import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.config.ParseException;
import org.openmuc.framework.core.datamanager.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apidriverlib.ApiDriverSettings;

import org.openmuc.framework.core.datamanager.DataManagerAccessor;
import org.openmuc.framework.data.Flag;
import org.openmuc.framework.data.FutureValue;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.data.Value;
import org.openmuc.framework.data.ValueType;
import org.openmuc.framework.dataaccess.Channel;
import org.openmuc.framework.dataaccess.ChannelState;
import org.openmuc.framework.dataaccess.DataLoggerNotAvailableException;
import org.openmuc.framework.dataaccess.DeviceState;
import org.openmuc.framework.dataaccess.ReadRecordContainer;
import org.openmuc.framework.dataaccess.RecordListener;
import org.openmuc.framework.dataaccess.WriteValueContainer;
import org.openmuc.framework.driver.spi.ChannelRecordContainer;
import org.openmuc.framework.driver.spi.ConnectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

public class ApiDriverTest {

    private static final Logger logger = LoggerFactory.getLogger(ApiDriverTest.class);
    private static DataManager dataManager;

    @BeforeAll
    static void setup() throws IOException, ParserConfigurationException, TransformerException, ParseException {
        dataManager = new DataManager();
        DataManagerAccessor.activateWithConfig(dataManager, "channelsApi.xml");
    }

    @Test
    void testChannelLoading() {
        Channel channel = dataManager.getChannel("temperature");

        Assertions.assertEquals("weather", channel.getChannelAddress(), "ChannelAddress incorrect for " + channel.getChannelAddress());
        logger.info("Loaded channel: {}", channel.getId());
    }
    
    @Test
    void testParsingChannelData() throws IOException {
    	String settingData = "apiURL=https://api.openweathermap.org/data/2.5/;" +
            "apiKey=789444d16a5e6ee2d9cea8706f0283f3;" +
            "city=Belgrade;" +
            "units=metric;" +
            "fetchingPeriodMinutes=10;";
    	
    	ApiDriverSettings apiSettings = ApiDriverSettings.getDeviceSettings(settingData);
    	
        Assertions.assertEquals("https://api.openweathermap.org/data/2.5/", apiSettings.getApiURL(), "URL is not correct");
        Assertions.assertEquals("Belgrade", apiSettings.getCity(), "city is not correct");
        
        logger.info("Channel settings parsed");
    }
    
    List<ChannelRecordContainer> getListOfContainers() {
        return List.of(
                (new DummyChannelRecordContainer(new ChannelSelector("temperature", "weather"))));
    }
    
    @Test
    void testCallingApi() {
    	String settingData = "apiURL=https://api.openweathermap.org/data/2.5/;" +
                "apiKey=789444d16a5e6ee2d9cea8706f0283f3;" +
                "city=Belgrade;" +
                "units=metric;" +
                "fetchingPeriodMinutes=10;";
        String deviceAddress = "someAddress";
        	
        ApiDriver apiDriver = new ApiDriver();
        try {
			apiDriver.connect(deviceAddress, settingData);
			Assertions.assertNotNull(apiDriver.getInfo(), "Driver info should not be null.");

	        List<ChannelRecordContainer> containers = getListOfContainers();
	        Assertions.assertFalse(containers.isEmpty(), "Channel list should not be empty.");
	        
	        apiDriver.read(containers, null, null);
	        
	        for (ChannelRecordContainer container : containers) {
	            Record record = container.getRecord();
	            Assertions.assertNotNull(record, "Record should not be null for channel: " + container.getChannelAddress());
	            logger.info("Channel: {}, Value: {} K", container.getChannelAddress(), record.getValue());
	        }
	        	
		} catch (ArgumentSyntaxException | ConnectionException e) {
			e.printStackTrace();
		}

    }
    
    static class ChannelSelector {
        String id;
        String address;

        public ChannelSelector(String id, String address) {
            this.id = id;
            this.address = address;
        }

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

    }
    
    static class DummyChannelRecordContainer implements ChannelRecordContainer {
        Record record = null;
        String channelAddress = null;
        String channelId = null;

        DummyChannelRecordContainer(ChannelSelector channelAddress) {
            this.channelAddress = channelAddress.getAddress();
            this.channelId = channelAddress.getId();
        }

        @Override
        public String getChannelAddress() {
            return this.channelAddress;
        }

        @Override
        public Object getChannelHandle() {
            return null;
        }

        @Override
        public void setChannelHandle(Object handle) {

        }

        @Override
        public ChannelRecordContainer copy() {
            return null;
        }

        @Override
        public Record getRecord() {
            return this.record;
        }

        @Override
        public void setRecord(Record record) {
            this.record = record;
        }

        @Override
        public Channel getChannel() {
            return new Channel() {
                @Override
                public String getId() {
                    return channelId;
                }

                @Override
                public String getChannelAddress() {
                    return channelAddress;
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public String getSettings() {
                    return null;
                }

                @Override
                public String getLoggingSettings() {
                    return null;
                }

                @Override
                public String getUnit() {
                    return null;
                }

                @Override
                public ValueType getValueType() {
                    return null;
                }

                @Override
                public double getScalingFactor() {
                    return 0;
                }

                @Override
                public int getSamplingInterval() {
                    return 0;
                }

                @Override
                public int getSamplingTimeOffset() {
                    return 0;
                }

                @Override
                public int getSamplingTimeout() {
                    return 0;
                }

                @Override
                public int getLoggingInterval() {
                    return 0;
                }

                @Override
                public int getLoggingTimeOffset() {
                    return 0;
                }

                @Override
                public String getDriverName() {
                    return null;
                }

                @Override
                public String getDeviceAddress() {
                    return null;
                }

                @Override
                public String getDeviceName() {
                    return null;
                }

                @Override
                public String getDeviceDescription() {
                    return null;
                }

                @Override
                public ChannelState getChannelState() {
                    return null;
                }

                @Override
                public DeviceState getDeviceState() {
                    return null;
                }

                @Override
                public void addListener(RecordListener recordListener) {

                }

                @Override
                public void removeListener(RecordListener recordListener) {

                }

                @Override
                public boolean isConnected() {
                    return false;
                }

                @Override
                public Record getLatestRecord() {
                    return null;
                }

                @Override
                public void setLatestRecord(Record record) {

                }

                @Override
                public Flag write(Value value) {
                    return null;
                }

                @Override
                public void writeFuture(List<FutureValue> list) {

                }

                @Override
                public WriteValueContainer getWriteContainer() {
                    return null;
                }

                @Override
                public Record read() {
                    return null;
                }

                @Override
                public ReadRecordContainer getReadContainer() {
                    return null;
                }

                @Override
                public Record getLoggedRecord(long l) throws DataLoggerNotAvailableException, IOException {
                    return null;
                }

                @Override
                public List<Record> getLoggedRecords(long l) throws DataLoggerNotAvailableException, IOException {
                    return null;
                }

                @Override
                public List<Record> getLoggedRecords(long l, long l1) throws DataLoggerNotAvailableException, IOException {
                    return null;
                }
            };
        }
    }

}
