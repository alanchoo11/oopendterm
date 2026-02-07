package com.sport.controller;

import com.sport.domain.Player;
import com.sport.exception.EntityNotFoundException;
import com.sport.exception.ValidationException;
import com.sport.factory.ServiceFactory;
import com.sport.service.interfaces.PlayerService;
import com.sport.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class PlayerController implements HttpHandler {

    private final PlayerService playerService;

    public PlayerController() {
        // Используем ServiceFactory для получения сервиса
        this.playerService = ServiceFactory.createPlayerService();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // --- CORS (Разрешаем запросы с сайта) ---
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

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
                case "PUT": // ВОТ ЭТОГО НЕ ХВАТАЛО!
                    handlePut(exchange, path);
                    break;
                case "DELETE": // И ЭТОГО ТОЖЕ
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
            e.printStackTrace(); // Пишем ошибку в консоль сервера
            sendResponse(exchange, 500, JsonUtil.createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        // GET /api/players/stats
        if (path.equals("/api/players/stats")) {
            Map<String, Object> stats = playerService.getPlayerStatistics();
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse(stats));
            return;
        }

        // GET /api/players/{id}
        if (path.matches("/api/players/\\d+")) {
            Integer id = extractIdFromPath(path);
            Player player = playerService.getPlayerById(id);
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse(player));
            return;
        }

        // GET /api/players (весь список)
        if (path.equals("/api/players")) {
            List<Player> players = playerService.getAllPlayers();
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse(players));
            return;
        }

        sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
    }

    private void handlePost(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/api/players")) {
            String requestBody = readRequestBody(exchange);
            Player player = JsonUtil.fromJson(requestBody, Player.class);
            Player created = playerService.createPlayer(player);
            sendResponse(exchange, 201, JsonUtil.createSuccessResponse(created));
        } else {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
        }
    }

    // --- НОВЫЙ МЕТОД ДЛЯ ОБНОВЛЕНИЯ (UPDATE) ---
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/players/\\d+")) {
            Integer id = extractIdFromPath(path);
            String requestBody = readRequestBody(exchange);

            Player player = JsonUtil.fromJson(requestBody, Player.class);
            player.setId(id); // Важно: ID берем из URL

            Player updated = playerService.updatePlayer(player);
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse(updated));
        } else {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
        }
    }

    // --- НОВЫЙ МЕТОД ДЛЯ УДАЛЕНИЯ (DELETE) ---
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/api/players/\\d+")) {
            Integer id = extractIdFromPath(path);
            playerService.deletePlayer(id);
            sendResponse(exchange, 200, JsonUtil.createSuccessResponse("Player deleted successfully"));
        } else {
            sendResponse(exchange, 404, JsonUtil.createErrorResponse("Endpoint not found"));
        }
    }

    // Вспомогательные методы
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

    private Integer extractIdFromPath(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}