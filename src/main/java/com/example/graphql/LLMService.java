package com.example.graphql;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LLMService {
    private static final String LLM_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final List<String> chatHistory = new ArrayList<>();

    public static JSONObject generateQueryFromNL(String naturalLanguage, String schema) throws IOException, ParseException {
        chatHistory.add("User: " + naturalLanguage);

        String chatHistoryString = String.join("\n", chatHistory);
        String prompt = "You are an expert in converting natural language descriptions into GraphQL queries. " +
                "Given the conversation history, user request, and the GraphQL API SDL, generate an accurate GraphQL query and provide a brief explanation. " +
                "\nConversation history:\n" + chatHistoryString +
                "\nGraphQL API SDL:\n" + schema +
                "\nUser request:\n" + naturalLanguage + "\nProvide the output in JSON format with 'query' and 'explanation' fields.";

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-4o-mini");
        payload.put("temperature", 0.3);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", prompt));
        payload.put("messages", messages);

        String response = Request.post(LLM_API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .bodyString(payload.toString(), org.apache.hc.core5.http.ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString();

        JSONObject jsonResponse = new JSONObject(response);
        String content = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        JSONObject parsedResponse = new JSONObject(content.replaceAll("```json|```", "").trim());

        chatHistory.add("LLM: " + parsedResponse.getString("query"));
        return parsedResponse;
    }

    // Suggest a natural language edit for the current query
    public static String suggestEditForQuery(String currentQuery, String schema) throws IOException, ParseException {
        // Use the same model to generate suggestions for improving the query
        String prompt = "The user has generated the following GraphQL query based on their natural language request:\n" +
                        currentQuery + "\n" +
                        "Suggest a natural language edit to refine this query or make it more accurate, considering the GraphQL API SDL:\n" + schema;

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-4o-mini");
        payload.put("temperature", 0.3);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", prompt));
        payload.put("messages", messages);

        String response = Request.post(LLM_API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .bodyString(payload.toString(), org.apache.hc.core5.http.ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString();

        JSONObject jsonResponse = new JSONObject(response);
        String content = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

        return content.trim();
    }
}
