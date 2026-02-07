package com.sport.controller;

import com.sport.domain.Team;
import com.sport.exception.EntityNotFoundException;
import com.sport.exception.ValidationException;
import com.sport.factory.ServiceFactory;
import com.sport.service.interfaces.TeamService;
import com.sport.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller for Team operations.
 * Uses com.sun.net.httpserver.HttpServer (built-in Java HTTP server).
 * Demonstrates DIP: Controller depends on Service interface.
 */
public class TeamController implements HttpHandler {
    
    // DIP: Controller depends on Service interface, not implementation
    private final TeamService teamService;
    
    public TeamController() {
        // Get service from factory (dependency injection)
        this.teamService = ServiceFactory.createTeamService();
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
        if (path.matches("/api/teams/\\d+")) {
            // GET /api/teams/{id}
            Long id = extractIdFromPath(path);
            Team team = teamService.getTeamById(id);
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse(team));
            
        } else if (path.equals("/api/teams")) {
            // GET /api/teams - list all
            String query = exchange.getRequestURI().getQuery();
            
            if (query != null && query.contains("sport=")) {
                // Filter by sport
                String sport = extractQueryParam(query, "sport");
                List<Team> teams = teamService.getTeamsBySport(sport);
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(teams));
                
            } else if (query != null && query.contains("location=")) {
                // Filter by location
                String location = extractQueryParam(query, "location");
                List<Team> teams = teamService.getTeamsByLocation(location);
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(teams));
                
            } else if (query != null && query.contains("sort=")) {
                // Sort teams
                String sortBy = extractQueryParam(query, "sort");
                boolean ascending = !query.contains("order=desc");
                List<Team> teams = teamService.sortTeams(sortBy, ascending);
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(teams));
                
            } else {
                // Get all teams
                List<Team> teams = teamService.getAllTeams();
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(teams));
            }
            
        } else if (path.equals("/api/teams/stats")) {
            // GET /api/teams/stats - statistics
            Map<String, Object> stats = teamService.getTeamStatistics();
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse(stats));
            
        } else {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
        }
    }
    
    private void handlePost(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/api/teams")) {
            // POST /api/teams - create new team
            String requestBody = readRequestBody(exchange);
            Team team = JsonUtil.fromJson(requestBody, Team.class);
            Team created = teamService.createTeam(team);
            sendResponse(exchange, 201, JsonUtil.createSuccessResponse(created));
        } else {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
        }
    }
    
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/teams/\\d+")) {
            // PUT /api/teams/{id} - update team
            Long id = extractIdFromPath(path);
            String requestBody = readRequestBody(exchange);
            Team team = JsonUtil.fromJson(requestBody, Team.class);
            team.setId(id);
            Team updated = teamService.updateTeam(team);
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse(updated));
        } else {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
        }
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/teams/\\d+")) {
            // DELETE /api/teams/{id} - delete team
            Long id = extractIdFromPath(path);
            boolean deleted = teamService.deleteTeam(id);
            if (deleted) {
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse("Team deleted successfully"));
            } else {
                sendResponse(exchange, 404, JsonUtil.createErrorResponse("Team not found"));
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
