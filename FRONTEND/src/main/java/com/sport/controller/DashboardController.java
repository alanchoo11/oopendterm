package com.sport.controller;

import com.sport.factory.ServiceFactory;
import com.sport.service.interfaces.PlayerService;
import com.sport.service.interfaces.TeamService;
import com.sport.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard Controller for providing statistics data.
 * Returns aggregated data for the frontend dashboard.
 */
public class DashboardController implements HttpHandler {
    
    private final TeamService teamService;
    private final PlayerService playerService;
    
    public DashboardController() {
        this.teamService = ServiceFactory.createTeamService();
        this.playerService = ServiceFactory.createPlayerService();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        if (!"GET".equals(method)) {
            sendResponse(exchange, 405, JsonUtil.createErrorResponse("Method not allowed"));
            return;
        }
        
        try {
            if (path.equals("/api/dashboard")) {
                // Get all dashboard statistics
                Map<String, Object> dashboard = buildDashboardData();
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(dashboard));
                
            } else if (path.equals("/api/dashboard/stats")) {
                // Get quick stats only
                Map<String, Object> stats = buildQuickStats();
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(stats));
                
            } else {
                sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, JsonUtil.createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
    
    private Map<String, Object> buildDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Quick stats for cards
        dashboard.put("stats", buildQuickStats());
        
        // Team statistics
        dashboard.put("teamStats", teamService.getTeamStatistics());
        
        // Player statistics
        dashboard.put("playerStats", playerService.getPlayerStatistics());
        
        // Recent teams (all teams sorted by name)
        dashboard.put("recentTeams", teamService.sortTeams("name", true));
        
        // Top players
        dashboard.put("topPlayers", playerService.getTopRatedPlayers(5));
        
        // Free agents
        dashboard.put("freeAgents", playerService.getFreeAgents());
        
        return dashboard;
    }
    
    private Map<String, Object> buildQuickStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total counts
        stats.put("totalTeams", teamService.countTeams());
        stats.put("totalPlayers", playerService.countPlayers());
        
        // For demo purposes, matches and tournaments are static
        // In real app, these would come from respective services
        stats.put("totalMatches", 0);
        stats.put("totalTournaments", 0);
        
        // Average rating
        stats.put("averageRating", Math.round(playerService.calculateAverageRating() * 100.0) / 100.0);
        
        return stats;
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
