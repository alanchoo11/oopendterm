package com.sport.controller;

import com.sport.domain.Player;
import com.sport.exception.EntityNotFoundException;
import com.sport.exception.ValidationException;
import com.sport.factory.ServiceFactory;
import com.sport.service.interfaces.PlayerService;
import com.sport.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller for Player operations.
 * Uses com.sun.net.httpserver.HttpServer (built-in Java HTTP server).
 * Demonstrates DIP: Controller depends on Service interface.
 */
public class PlayerController implements HttpHandler {
    
    // DIP: Controller depends on Service interface, not implementation
    private final PlayerService playerService;
    
    public PlayerController() {
        // Get service from factory (dependency injection)
        this.playerService = ServiceFactory.createPlayerService();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange, path);
                    break;
                case "PUT":
                    handlePut(exchange, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendResponse(exchange, 405, JsonUtil.createErrorResponse("Method not allowed"));
            }
        } catch (EntityNotFoundException e) {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse(e.getMessage()));
        } catch (ValidationException e) {
            sendResponse(exchange, 400, JsonUtil.createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            sendResponse(exchange, 500, JsonUtil.createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
    
    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/players/\\d+")) {
            // GET /api/players/{id}
            Long id = extractIdFromPath(path);
            Player player = playerService.getPlayerById(id);
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse(player));
            
        } else if (path.equals("/api/players")) {
            // GET /api/players - list all with optional filters
            String query = exchange.getRequestURI().getQuery();
            
            if (query != null && query.contains("teamId=")) {
                // Filter by team
                Long teamId = Long.parseLong(extractQueryParam(query, "teamId"));
                List<Player> players = playerService.getPlayersByTeam(teamId);
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(players));
                
            } else if (query != null && query.contains("position=")) {
                // Filter by position
                String position = extractQueryParam(query, "position");
                List<Player> players = playerService.getPlayersByPosition(position);
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(players));
                
            } else if (query != null && query.contains("top=")) {
                // Get top rated players
                int limit = Integer.parseInt(extractQueryParam(query, "top"));
                List<Player> players = playerService.getTopRatedPlayers(limit);
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(players));
                
            } else if (query != null && query.contains("sort=")) {
                // Sort players
                String sortBy = extractQueryParam(query, "sort");
                boolean ascending = !query.contains("order=desc");
                List<Player> players = playerService.sortPlayers(sortBy, ascending);
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(players));
                
            } else if (query != null && query.contains("freeAgents=true")) {
                // Get free agents
                List<Player> players = playerService.getFreeAgents();
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(players));
                
            } else {
                // Get all players
                List<Player> players = playerService.getAllPlayers();
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(players));
            }
            
        } else if (path.equals("/api/players/stats")) {
            // GET /api/players/stats - statistics
            Map<String, Object> stats = playerService.getPlayerStatistics();
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse(stats));
            
        } else {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
        }
    }
    
    private void handlePost(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/api/players")) {
            // POST /api/players - create new player
            String requestBody = readRequestBody(exchange);
            Player player = JsonUtil.fromJson(requestBody, Player.class);
            Player created = playerService.createPlayer(player);
            sendResponse(exchange, 201, JsonUtil.createSuccessResponse(created));
        } else {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
        }
    }
    
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/players/\\d+")) {
            // PUT /api/players/{id} - update player
            Long id = extractIdFromPath(path);
            String requestBody = readRequestBody(exchange);
            Player player = JsonUtil.fromJson(requestBody, Player.class);
            player.setId(id);
            Player updated = playerService.updatePlayer(player);
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse(updated));
        } else {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
        }
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/players/\\d+")) {
            // DELETE /api/players/{id} - delete player
            Long id = extractIdFromPath(path);
            boolean deleted = playerService.deletePlayer(id);
            if (deleted) {
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse("Player deleted successfully"));
            } else {
                sendResponse(exchange, 404, JsonUtil.createErrorResponse("Player not found"));
            }
        } else {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
        }
    }
    
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
    
    private Long extractIdFromPath(String path) {
        String[] parts = path.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
    
    private String extractQueryParam(String query, String paramName) {
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue[0].equals(paramName) && keyValue.length > 1) {
                return keyValue[1];
            }
        }
        return "";
    }
}
