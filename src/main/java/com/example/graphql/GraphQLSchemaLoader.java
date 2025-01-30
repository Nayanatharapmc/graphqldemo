package com.example.graphql;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class GraphQLSchemaLoader {
    public static String loadSDL(String sdlFile) {
        try {
            InputStream inputStream = GraphQLSchemaLoader.class.getClassLoader().getResourceAsStream(sdlFile);
            if (inputStream == null) {
                throw new RuntimeException("SDL file not found in resources.");
            }
            String sdl = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return sdl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the SDL file: " + sdlFile, e);
        }
    }
}
