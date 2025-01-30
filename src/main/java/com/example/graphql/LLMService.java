package com.example.graphql;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ParseException;
import java.io.IOException;

public class LLMService {
    private static final String LLM_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "<OPENAI_API_KEY>";

    public static String generateQueryFromNL(String naturalLanguage, String schema) throws IOException, ParseException {
        String prompt = "You are an expert in converting natural language descriptions into GraphQL queries. Translate the following natural language description to a valid GraphQL query for the given API SDL. If the description is not matching with the given SDL, output a suitable message:"
                + naturalLanguage  
                + " SDL:"
                + schema;
        
        prompt = prompt.replace("\n", " "); 

        String payload = "{"
                + "\"model\": \"gpt-4o-mini\","
                + "\"messages\": ["
                + "  {\"role\": \"user\", \"content\": \"" + prompt + "\"}"
                + "],"
                + "\"temperature\": 0.7"
                + "}";

        return Request.post(LLM_API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .bodyString(payload, org.apache.hc.core5.http.ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString();
    }
}
