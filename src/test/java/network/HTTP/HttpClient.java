package network.HTTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {
    public static String authorization = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9yZVNlcnZlciIsImxvZ2luIjoicm9vdCIsImV4cCI6MTY4ODA2ODM0MiwiaWF0IjoxNjg4MDY0NzQyLCJqdGkiOiIxYTYxNzkxZi03MGY0LTQ1MGMtYjQ4OC03MWQ4ZmIxYWRmMzYifQ.OAxDmgWvetawncThleZ7z8l8C_V87jv_S1Ats0lQ4qE";

    public static void sendGetHTTPRequest(String url) {
        try {
            // Create URL object
            URL apiUrl = new URL(url);

            // Create HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", authorization);

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Print the response
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Body: " + response.toString());

            // Disconnect the connection
            connection.disconnect();

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void sendHttpPostRequest(String url, String method, String jsonBody) {
        try {
            // Create URL object
            URL apiUrl = new URL(url);

            // Create HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            // Set the request method to POST
            connection.setRequestMethod(method);

            // Set the content type to JSON
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", authorization);

            // Enable output and input streams
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Write the JSON body to the request
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            outputStream.close();

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Print the response
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Body: " + response.toString());

            // Disconnect the connection
            connection.disconnect();

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
