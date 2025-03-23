package fileConversion;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.InputStream;
import java.io.OutputStream;

public class FromOWLToXML {

	// input and output file
    private static String inputFile = "devicesAndChannels.owl";
    private static String outputFile = "outputOWLtoXML.xml";
	
    public static void main(String[] args) {
    	// read OWL file
        try (InputStream input = FromOWLToXML.class.getClassLoader().getResourceAsStream(inputFile);
             OutputStream output = new java.io.FileOutputStream(outputFile)) {
            
            if (input == null) {
                System.err.println("OWL file not found in resources folder: " + inputFile);
                return;
            }

            // Jena model
            Model model = ModelFactory.createDefaultModel();

            // read OWL file
            model.read(input, null, "RDF/XML");

            // create XML file
            model.write(output, "RDF/XML");
            
            System.out.println("XML file created successfully: " + outputFile);
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
