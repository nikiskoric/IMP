package org.openmuc.framework.app.ems;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.osgi.service.component.annotations.Component;

@Component(service = {WeatherData.class})
public class WeatherData {
    private static final String API_KEY = "789444d16a5e6ee2d9cea8706f0283f3";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String CITY = "Belgrade";


    public String fetchWeatherData() throws Exception {
        String url = String.format("%s?q=%s&appid=%s", API_URL, CITY, API_KEY);

        URI uri = URI.create(url);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
