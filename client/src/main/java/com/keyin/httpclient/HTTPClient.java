package com.keyin.httpclient;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HTTPClient {

    private final HttpClient client;

    public HTTPClient() {
        this.client = HttpClient.newHttpClient();
    }

    public static HttpRequest createRequest(String uriString) throws URISyntaxException {
        // Create the URI
        URI uri = new URI(uriString);

        // Build the request using the URI, request method, and headers
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri);

        return builder.build();
    }

    public HttpResponse<String> sendHttpRequest(HttpRequest request) throws IOException, InterruptedException {
        try{
            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the response is successful
            if (response.statusCode() != 200) {
                System.out.printf("❗ (%d) Request failed.", response.statusCode());
                System.out.println("\n");
            }

            return response;
        } catch (IOException | InterruptedException error) {
            System.out.println("⚠️ Connection Error.");
            throw error;
        }
    }

    public static JSONArray parseJson(String jsonString) throws ParseException {
        // Check if the JSON string is the format of an array of objects
        // If not, add square brackets to format it as if it's an array of objects (simpler to parse)
        if (!(jsonString.startsWith("[") && jsonString.endsWith("]"))) {
            jsonString = "[" + jsonString + "]";
        }

        // Try to parse JSON string to JSON array
        JSONParser parser = new JSONParser();

        JSONArray jsonArray = (JSONArray) parser.parse(jsonString);

        return jsonArray;
    }

    public static JSONArray formatJson(List<String> jsonArray) throws JsonProcessingException {
        // Create an array to store the JSON strings after they're formatted
        JSONArray formattedJSONArray = new JSONArray();

        // Create a LinkedHashMap to store key/value pairs
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        // Iterate through the JSON array
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;

            if (jsonObj != null) {
                // Create a TreeMap to store the object properties
                Map<String, Object> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                treeMap.putAll(jsonObj);

                // Making sure the "id" key is at the beginning of the LinkedHashMap... (For readability)
                // Check if "id" key exists in the object properties
                if (treeMap.containsKey("id")) {
                    // Remove the "id" key-value pair from the TreeMap
                    Object idValue = treeMap.remove("id");

                    // Put the "id" key-value pair at the beginning of the LinkedHashMap
                    jsonMap.put("id", idValue);
                }

                // Add the remaining key-value pairs to the LinkedHashMap
                jsonMap.putAll(treeMap);

                // Create an ObjectMapper to convert the LinkedHashMap to JSON
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

                // Create a DefaultPrettyPrinter to format the JSON & add settings for indentation
                DefaultPrettyPrinter printer = new DefaultPrettyPrinter().withObjectIndenter(new DefaultIndenter("\t", "\n"));
                String jsonAsString = objectMapper.writer(printer).writeValueAsString(jsonMap);

                // Add the formatted JSON string to the list
                formattedJSONArray.add(jsonAsString);

            }
        }
        return formattedJSONArray;
    }

    public static void displayJSON(JSONArray formattedJSONArray) {
        // formattedJSONArray could simply be printed as it is, but this loop will increase
        // readability by adding new line after each record, while still being valid JSON.
        int i = 0;
        if (formattedJSONArray.contains("null")) {
            System.out.println("There is no data associated with this endpoint.");
        }
        for (Object formattedJSONString : formattedJSONArray) {
            // Check if the current object is the last object in the list
            // If so, print the JSON string without a comma at the end
            if (i++ == formattedJSONArray.size() - 1) {
                System.out.println(formattedJSONString + "\n");

            } else {
                System.out.println(formattedJSONString + ",\n");
            }
        }
    }

    public void runTask(String uriString) throws IOException, InterruptedException, URISyntaxException, ParseException {
        HttpRequest request = createRequest(uriString);

        System.out.println("\n-------------- ENDPOINT: --------------\n");
        System.out.println(uriString + "\n");

        String responseBody = sendHttpRequest(request).body();

        if (responseBody.equals("null")) {
            System.out.println("❗ There is no data associated with this endpoint.");
        }
        if (!responseBody.equals("null")){
            System.out.println("-------------- RAW DATA: --------------\n");
            System.out.println(responseBody + "\n");

            System.out.println("---------------- JSON: ----------------\n");
            JSONArray jsonArray = parseJson(responseBody);
            JSONArray formattedJSONArray = formatJson(jsonArray);
            displayJSON(formattedJSONArray);

            if (uriString == "http://localhost:3000/treeify/logs") {
                System.out.println("---------------- FORMATTED TREE DATA: ----------------\n");
                jsonArray = parseJson(responseBody);

                JSONArray treeData = new JSONArray();
                for (Object obj : jsonArray) {
                    JSONObject jsonObj = (JSONObject) obj;
                    JSONArray tree = parseJson(jsonObj.get("treedata").toString());
                    treeData.add(tree.get(0));
                }
                formattedJSONArray = formatJson(treeData);
                displayJSON(formattedJSONArray);
            }
        }
    }
}