package com.example.graphql;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ParseException;
import java.io.IOException;

public class LLMConnectionTest {
    private static final String LLM_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    public static String generateResponse(String prompt) throws IOException, ParseException {
        String payload = "{"
                + "\"model\": \"gpt-4o-mini\","
                + "\"messages\": ["
                + "  {\"role\": \"user\", \"content\": \"" + prompt + "\"}"
                + "],"
                + "\"temperature\": 0.7"
                + "}";

        return Request.post(LLM_API_URL)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .bodyString(payload, org.apache.hc.core5.http.ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString();
    }

    public static void main(String[] args) {
        try {
            String response = generateResponse("Give the graphQL query to find all the users in the database. You have given the SDL for the graphQL API. The SDL is as follows: type User { id: ID! name: String! email: String! } type Query { users: [User!]! }. Give only the query without ");
            System.out.println("API Response: " + response);
        } catch (IOException | ParseException e) {
            System.err.println("Error connecting to the LLM API: " + e.getMessage());
        }
    }
}
