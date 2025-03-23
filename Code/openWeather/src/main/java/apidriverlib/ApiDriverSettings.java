package apidriverlib;

import java.io.IOException;

public class ApiDriverSettings {
	 
    private final String apiURL;
    private final String apiKey;
    private final String city;
    private final String units;
    private final Long fetchingPeriodMinutes;

	public ApiDriverSettings(String apiURL, String apikey, String city, String units, Long fetchingPeriodMinutes) {
		this.apiURL = apiURL;
		this.apiKey = apikey;
		this.city = city;
		this.units = units;
		this.fetchingPeriodMinutes = fetchingPeriodMinutes;
	}
	
	public static ApiDriverSettings getDeviceSettings(String deviceSettings) throws IOException {
		
		String [] settings = {"apiKey", "apiURL", "city", "units", "fetchingPeriodMinutes"};
		
		for(String set : settings) {
			if(!deviceSettings.contains(set + "=")) {
				throw new IOException("Missing some device settings!");
			}
		}
		
		String apiURL = "";
		String apiKey = "";
		String city = "";
		String units = "";
		Long fetchingPeriodMinutes = 0L;
		
		String [] parts = deviceSettings.split(";");
		for(String part : parts) {
			String [] oneKeyValue = part.split("=");
			String key = oneKeyValue[0].trim();
			String value = oneKeyValue[1].trim();
			
			if(key.equals("apiURL")) {
				apiURL = value;
			}
			if(key.equals("apiKey")) {
				apiKey = value;
			}
			if(key.equals("city")) {
				city = value;
			}
			if(key.equals("units")) {
				units = value;
			}
			if(key.equals("fetchingPeriodMinutes")) {
				fetchingPeriodMinutes = Long.parseLong(value);
			}
		}
		return new ApiDriverSettings(apiURL, apiKey, city, units, fetchingPeriodMinutes);
	}

	public String getApiURL() {
		return apiURL;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getCity() {
		return city;
	}

	public String getUnits() {
		return units;
	}

	public Long getFetchingPeriodMinutes() {
		return fetchingPeriodMinutes;
	}
	
}
