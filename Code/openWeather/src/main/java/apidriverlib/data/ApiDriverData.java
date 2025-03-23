package apidriverlib.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiDriverData {

	private String response;
	
	public ApiDriverData(String response) {
		this.response = response;
	}

	public double getTemperature() throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);

        double temperature = rootNode.get("main").get("temp").asDouble();
		
		return temperature;
	}

}
