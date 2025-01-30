package com.example.graphql;

import java.io.*;
import java.net.URL;
import javax.net.ssl.*;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public class GraphQLClient {
    private static final String WSO2_GRAPHQL_ENDPOINT = "https://localhost:8243/swapi/1.0.0";
    private static final String ACCESS_TOKEN = "<ACCESS_TOKEN>";

    static {
        disableSSLVerification();
    }

    public static String callGraphQLAPI(String query) throws IOException {
        @SuppressWarnings("deprecation")
        URL url = new URL(WSO2_GRAPHQL_ENDPOINT);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json"); 
        connection.setDoOutput(true);
        // String requestBody = "{ \"query\": \"" + query.replace("\"", "\\\"") + "\" }";
        // System.out.println("Request body: " + requestBody);

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("query", query);
        String requestBody = jsonRequest.toString();
    
        System.out.println("Formatted Request body: " + requestBody);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    
        //  Read response, including errors
        int responseCode = connection.getResponseCode();
        InputStream responseStream = (responseCode >= 200 && responseCode < 300) ?
                                    connection.getInputStream() : connection.getErrorStream();
    
        BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
    
        System.out.println("Response Code: " + responseCode);
        System.out.println("Response: " + response.toString());
    
        return response.toString();
    }

    private static void disableSSLVerification() {
        try {
            TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCertificates, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Disable hostname verification
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

