package fileConversion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.*;

public class FromCSVToOWL {

    // input and output file
    private static String inputFile = "devicesAndChannels.csv";
    private static String outputFile = "outputCSVtoOWL.owl";

    public static void main(String[] args) throws Exception {
        // read CSV file
        InputStream inputStream = FromCSVToOWL.class.getClassLoader().getResourceAsStream(inputFile);
        if (inputStream == null) {
            System.err.println("File not found: " + inputFile);
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        if (line == null) {
            reader.close();
            throw new IllegalArgumentException("CSV file is empty.");
        }

        String[] headers = line.split(",");
        List<Map<String, String>> data = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            Map<String, String> row = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                row.put(headers[i].trim(), i < values.length ? values[i].trim() : "");
            }
            data.add(row);
        }

        reader.close();

        // create OWL file
        Model model = ModelFactory.createDefaultModel();
        String baseURI = "http://example.com/owl#";
        model.setNsPrefix("ex", baseURI);

        for (Map<String, String> row : data) {
            String individualName = row.getOrDefault("id", "Unnamed");
            Resource individual = model.createResource(baseURI + individualName);

            for (Map.Entry<String, String> entry : row.entrySet()) {
                String propertyName = entry.getKey();
                String propertyValue = entry.getValue();

                Property property = model.createProperty(baseURI + propertyName);
                individual.addProperty(property, propertyValue);
            }
        }

        // write OWL file
        try (java.io.FileOutputStream out = new java.io.FileOutputStream(outputFile)) {
            model.write(out, "RDF/XML");
        }

        System.out.println("OWL file created successfully: " + outputFile);
    }
}
