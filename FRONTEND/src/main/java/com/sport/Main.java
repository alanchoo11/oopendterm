package com.sport;

import com.sport.controller.DashboardController;
import com.sport.controller.PlayerController;
import com.sport.controller.TeamController;
import com.sport.domain.Player;
import com.sport.domain.Team;
import com.sport.util.DBConnection;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main application class.
 * Starts the HTTP server and demonstrates Reflection API.
 */
public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Sport Management System");
        System.out.println("   Starting up...");
        System.out.println("========================================\n");

        // Demonstrate Reflection API - analyze entity classes
        demonstrateReflection();

        // Test database connection
        System.out.println("\n--- Testing Database Connection ---");
        boolean dbConnected = DBConnection.testConnection();
        if (!dbConnected) {
            System.err.println("WARNING: Could not connect to database.");
            System.err.println("Please ensure PostgreSQL is running and database is configured.");
            System.err.println("Continuing with in-memory data only...\n");
        }

        // Start HTTP server
        System.out.println("\n--- Starting HTTP Server ---");
        try {
            startHttpServer();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Demonstrates Java Reflection API.
     * Inspects entity classes and prints their fields.
     */
    private static void demonstrateReflection() {
        System.out.println("--- Reflection API Demonstration ---");
        System.out.println("Analyzing entity classes at runtime...\n");

        // Analyze Team class
        System.out.println("Class: " + Team.class.getName());
        System.out.println("Package: " + Team.class.getPackage().getName());
        System.out.println("Fields:");

        Field[] teamFields = Team.class.getDeclaredFields();
        for (Field field : teamFields) {
            System.out.printf("  - %s: %s%n",
                    field.getName(),
                    field.getType().getSimpleName());
        }

        System.out.println();

        // Analyze Player class
        System.out.println("Class: " + Player.class.getName());
        System.out.println("Package: " + Player.class.getPackage().getName());
        System.out.println("Fields:");

        Field[] playerFields = Player.class.getDeclaredFields();
        for (Field field : playerFields) {
            System.out.printf("  - %s: %s%n",
                    field.getName(),
                    field.getType().getSimpleName());
        }

        // Demonstrate creating instance via reflection
        System.out.println("\n--- Creating instance via Reflection ---");
        try {
            Team team = Team.class.getDeclaredConstructor().newInstance();
            System.out.println("Created empty Team instance: " + team);

            // Set field value via reflection
            Field nameField = Team.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(team, "Reflection FC");
            System.out.println("Set name via reflection: " + team.getName());

        } catch (Exception e) {
            System.err.println("Reflection error: " + e.getMessage());
        }

        System.out.println("\n--- End of Reflection Demo ---");
    }

    /**
     * Starts the HTTP server with all controllers.
     */
    private static void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Register controllers
        server.createContext("/api/teams", new TeamController());
        server.createContext("/api/players", new PlayerController());
        server.createContext("/api/dashboard", new DashboardController());

        // Thread pool for handling requests
        server.setExecutor(Executors.newFixedThreadPool(10));

        server.start();

        System.out.println("Server started successfully!");
        System.out.println("Listening on port: " + PORT);
        System.out.println("\nAvailable endpoints:");
        System.out.println("  GET  http://localhost:" + PORT + "/api/dashboard");
        System.out.println("  GET  http://localhost:" + PORT + "/api/dashboard/stats");
        System.out.println("  GET  http://localhost:" + PORT + "/api/teams");
        System.out.println("  GET  http://localhost:" + PORT + "/api/teams/{id}");
        System.out.println("  GET  http://localhost:" + PORT + "/api/teams/stats");
        System.out.println("  POST http://localhost:" + PORT + "/api/teams");
        System.out.println("  PUT  http://localhost:" + PORT + "/api/teams/{id}");
        System.out.println("  DELETE http://localhost:" + PORT + "/api/teams/{id}");
        System.out.println("  GET  http://localhost:" + PORT + "/api/players");
        System.out.println("  GET  http://localhost:" + PORT + "/api/players/{id}");
        System.out.println("  GET  http://localhost:" + PORT + "/api/players/stats");
        System.out.println("  POST http://localhost:" + PORT + "/api/players");
        System.out.println("  PUT  http://localhost:" + PORT + "/api/players/{id}");
        System.out.println("  DELETE http://localhost:" + PORT + "/api/players/{id}");
        System.out.println("\nPress Ctrl+C to stop the server.");
    }
}
