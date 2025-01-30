package com.example.graphql;

import java.io.IOException;
import java.util.Scanner;

import org.apache.hc.core5.http.ParseException;
import org.json.JSONObject;

public class DemoApp {
    public static void main(String[] args) {
        try {
            // Load SDL (GraphQL schema)
            String sdlFile = "api-sdl.graphql"; 
            String schema = GraphQLSchemaLoader.loadSDL(sdlFile);

            // Start chatbot interface
            String query = chatInterface(schema);
            if (query == null) {
                System.out.println("Exiting the chatbot.");
                System.exit(0);
            }

            // After executing or editing the query, the API is called
            String response = GraphQLClient.callGraphQLAPI(query);
            System.out.println("API Response: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String chatInterface(String schema) {
        Scanner scanner = new Scanner(System.in);
        String currentQuery = "";
        while (true) {
            System.out.print("Enter a natural language query (or type 'exit' to stop): ");
            String userInput = scanner.nextLine();
            if ("exit".equalsIgnoreCase(userInput)) {
                scanner.close();
                return null;
            }

            try {
                // Generate initial GraphQL query from the user's input
                JSONObject response = LLMService.generateQueryFromNL(userInput, schema);
                String graphqlQuery = response.getString("query");
                String explanation = response.getString("explanation");

                // Display the generated query and explanation
                System.out.println("\nGenerated GraphQL Query:\n" + graphqlQuery);
                System.out.println("\nExplanation:\n" + explanation);

                currentQuery = graphqlQuery;  // Update the current query

                while(true) {
                    // Prompt user for next action
                    System.out.print("\nType 'edit' to refine, 'execute' to run, 'continue' for a new query, or 'exit' to stop: ");
                    String action = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(action)) {
                        scanner.close();
                        return null;  // Exit the loop
                    } else {
                        if ("edit".equalsIgnoreCase(action)) {
                            // Ask the user if they want to manually edit or get a suggestion for edit
                            System.out.print("Type 'manual' to manually edit or 'suggest' to get an edit suggestion: ");
                            String editChoice = scanner.nextLine();
                            
                            if ("manual".equalsIgnoreCase(editChoice)) {
                                // Prompt the user to manually edit the query
                                System.out.println("Current Query: " + currentQuery);
                                System.out.print("Edit the query and submit: ");
                                String editedQuery = scanner.nextLine();
                                if (!editedQuery.isEmpty()) {
                                    currentQuery = editedQuery;  // Use the manually edited query
                                }
                            } else if ("suggest".equalsIgnoreCase(editChoice)) {
                                // Get an edit suggestion from LLM
                                String suggestion = LLMService.suggestEditForQuery(currentQuery, schema);
                                System.out.println("Suggested Edit: " + suggestion);
                                System.out.print("Accept the suggestion or modify it manually: ");
                                String userInputForEdit = scanner.nextLine();
                                if (!userInputForEdit.isEmpty()) {
                                    currentQuery = userInputForEdit;  // Use the modified suggestion
                                } else {
                                    currentQuery = suggestion;  // Use the suggestion as it is
                                }
                            }
                        } else if ("execute".equalsIgnoreCase(action)) {
                            // Execute the query and return the result
                            System.out.println("\nExecuting GraphQL Query... (Simulation)\n");
                            GraphQLClient.callGraphQLAPI(currentQuery);
                            return currentQuery;  // Return the query for API call in main
                        } else if ("continue".equalsIgnoreCase(action)) {
                            // Continue with a new natural language query
                            continue;
                        } else if ("exit".equalsIgnoreCase(action)) {
                            scanner.close();
                            return null;  // Exit the loop
                        }
                        }
                    }
                } catch (IOException | ParseException e) {
                    System.err.println("Error communicating with LLM API: " + e.getMessage());
            }
        }
    }
}
