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
 */
public class DashboardController implements HttpHandler {

    private final TeamService teamService;
    private final PlayerService playerService;

    public DashboardController() {
        // Используем твою Factory для получения сервисов
        this.teamService = ServiceFactory.createTeamService();
        this.playerService = ServiceFactory.createPlayerService();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 1. ВАЖНО: Добавляем CORS заголовки, чтобы React видел данные
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

        // 2. Обрабатываем preflight-запрос (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Дашборд работает только на чтение (GET)
        if (!"GET".equals(method)) {
            sendResponse(exchange, 405, JsonUtil.createErrorResponse("Method not allowed"));
            return;
        }

        try {
            if (path.equals("/api/dashboard")) {
                // Полная статистика
                Map<String, Object> dashboard = buildDashboardData();
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(dashboard));

            } else if (path.equals("/api/dashboard/stats")) {
                // Только быстрые цифры (верхние карточки)
                Map<String, Object> stats = buildQuickStats();
                sendResponse(exchange, 200, JsonUtil.createSuccessResponse(stats));

            } else {
                sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Логируем ошибку в консоль сервера
            sendResponse(exchange, 500, JsonUtil.createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    private Map<String, Object> buildDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();

        // Основные цифры
        dashboard.put("stats", buildQuickStats());

        // Данные для графиков
        dashboard.put("teamStats", teamService.getTeamStatistics());
        dashboard.put("playerStats", playerService.getPlayerStatistics());

        // Недавние команды (сортируем по ID по убыванию, чтобы видеть новые, или по имени)
        // Если в ServiceImpl есть сортировка "id", лучше использовать false (descending)
        dashboard.put("recentTeams", teamService.sortTeams("name", true));

        // Топ игроков
        dashboard.put("topPlayers", playerService.getTopRatedPlayers(5));

        // Свободные агенты
        dashboard.put("freeAgents", playerService.getFreeAgents());

        return dashboard;
    }

    private Map<String, Object> buildQuickStats() {
        Map<String, Object> stats = new HashMap<>();

        // Реальные данные из БД
        stats.put("totalTeams", teamService.countTeams());
        stats.put("totalPlayers", playerService.countPlayers());

        // Заглушки (пока нет функционала матчей)
        stats.put("totalMatches", 0);
        stats.put("totalTournaments", 0);

        // Средний рейтинг (округляем до 2 знаков)
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