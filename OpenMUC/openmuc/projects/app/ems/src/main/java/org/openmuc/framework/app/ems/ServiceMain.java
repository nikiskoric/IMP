package org.openmuc.framework.app.ems;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {})
public final class ServiceMain {

    private static final Logger logger = LoggerFactory.getLogger(ServiceMain.class);
    private static final String APP_NAME = "Service Main App";
    
    private Timer getDataTimer;
    private String currentWeatherData;
    
    
    @Reference
    private WeatherData weatherData;
    
    @Reference
    private MqttClientImpl mqttClient;
    
	@Activate
    private void activate() {
        logger.info("Activating {}", APP_NAME);
        initUpdateTimer();
        mqttClient.connectClient();
        
    }

    @Deactivate
    private void deactivate() {
        logger.info("Deactivating {}", APP_NAME);
        mqttClient.disconnectClient();
        getDataTimer.cancel();
        getDataTimer.purge();
    }
    
    private void initUpdateTimer() {
        getDataTimer = new Timer("Get New Data Timer");

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                fetchNewData();
            }
        };
        getDataTimer.scheduleAtFixedRate(task, (long) 1000, (long) 10000);
    }

    private void fetchNewData() {
    	try {
    
			currentWeatherData = weatherData.fetchWeatherData();
			
			logger.info(currentWeatherData);
            mqttClient.publishMessage(currentWeatherData);
		} catch (Exception e) {
			logger.error("Error fetching or publishing weather data", e);
		}
    }
}
