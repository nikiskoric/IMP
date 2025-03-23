package apidriverlib;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import apidriverlib.data.ApiDriverData;

public class ApiDriverApi {
	
    private final String apiURL;
    private final String apiKey;
    private final String city;
	
	public ApiDriverApi(ApiDriverSettings settings) {
		apiURL = settings.getApiURL();
		apiKey = settings.getApiKey();
		city = settings.getCity();
	}
	
	public ApiDriverData getDataFromChannel(String channel) throws IOException, InterruptedException {
		
		String url = String.format("%s%s?q=%s&appid=%s", apiURL, channel, city, apiKey);

        URI uri = URI.create(url);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		return new ApiDriverData(response.body());
	}
}
