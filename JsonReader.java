import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {

    public HashMap<String,String> readJson(String fileName) {

        // Create an instance of ObjectMapper to read JSON file
        ObjectMapper mapper = new ObjectMapper();

        // Specify the path to the JSON file
        var jsonFile = new File(fileName);

        // Create a HashMap to store the data
        HashMap<String, String> polygonMap = new HashMap<String,String>();

        try {
            // Read the JSON file and store the data in the HashMap
            polygonMap = (HashMap<String, String>) mapper.readValue(jsonFile, HashMap.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return polygonMap;

    }
}
