package fileConversion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FromCSVToXML {

	// input and output file
    private static String inputFile = "devicesAndChannels.csv";
    private static String outputFile = "outputCSVtoXML.xml";

    public static void main(String[] args) throws Exception {
        // read CSV file
        InputStream inputStream = FromCSVToXML.class.getClassLoader().getResourceAsStream(inputFile);
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

        // create XML file
        String rootElementName = inputFile.replaceFirst("[.][^.]+$", "");
        String rowElementName = rootElementName.substring(0, 1).toLowerCase() + rootElementName.substring(1);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<" + rootElementName + ">\n");

        for (Map<String, String> row : data) {
            writer.write("  <" + rowElementName + ">\n");
            for (Map.Entry<String, String> entry : row.entrySet()) {
                writer.write("    <" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">\n");
            }
            writer.write("  </" + rowElementName + ">\n");
        }

        writer.write("</" + rootElementName + ">\n");
        writer.close();

        System.out.println("XML file created successfully: " + outputFile);
    }
}
