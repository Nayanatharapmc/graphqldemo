package com.example.graphql;
// import java.util.Scanner;
import org.json.JSONObject;

public class DemoApp {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.err.println("Please provide an NL description as an argument.");
                System.exit(1);
            }

            // Receive NL description from arguments
            String naturalLanguage = args[0];

            // Load SDL
            String sdlFile = "api-sdl.graphql";
            String schema = GraphQLSchemaLoader.loadSDL(sdlFile);

            // Convert NL to GraphQL query
            String query = LLMService.generateQueryFromNL(naturalLanguage, schema);

            // Parse the response as a JSON object
            JSONObject jsonResponse = new JSONObject(query);

            // Extract the content from the assistant's message
            String content = jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

            // Extract the GraphQL query between the ```graphql tags
            String graphqlQuery = content.replaceAll("(?s).*```graphql\\s*(.*?)\\s*```.*", "$1");
            System.out.println(graphqlQuery);

            // Call the GraphQL API
            String response = GraphQLClient.callGraphQLAPI(graphqlQuery);
            System.out.println("API Response: " + response);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
