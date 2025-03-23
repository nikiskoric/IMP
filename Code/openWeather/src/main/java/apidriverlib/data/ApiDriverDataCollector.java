package apidriverlib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apidriverlib.ApiDriverApi;

public class ApiDriverDataCollector {

    private final Map<String, List<ApiDriverData>> channelRecords;
    private ApiDriverApi apiDriver;

    public ApiDriverDataCollector(ApiDriverApi apiDriver) {
        this.setApiDriver(apiDriver);
        this.channelRecords = new HashMap<>();
    }

    public void addRecord(String channelId, ApiDriverData data) {
        channelRecords
            .computeIfAbsent(channelId, k -> new ArrayList<>())
            .add(data);
    }

    public List<ApiDriverData> getRecords(String channelId) {
        return channelRecords.getOrDefault(channelId, new ArrayList<>());
    }

    public Map<String, List<ApiDriverData>> getAllRecords() {
        return new HashMap<>(channelRecords);
    }

	public ApiDriverApi getApiDriver() {
		return apiDriver;
	}

	public void setApiDriver(ApiDriverApi apiDriver) {
		this.apiDriver = apiDriver;
	}
}
