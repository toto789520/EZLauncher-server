package com.ezlauncher.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CheckHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
            String player = params.get("player");
            String clientHash = params.get("hash");

            if (player == null) {
                sendError(exchange, 400, "Paramètre 'player' manquant");
                return;
            }

            // Vérifie la whitelist
            boolean whitelisted = true;
            if (Files.exists(Paths.get("whitelist.json"))) {
                String whitelistJson = new String(Files.readAllBytes(Paths.get("whitelist.json")));
                Gson gson = new Gson();
                try {
                    List<WhitelistEntry> whitelist = gson.fromJson(whitelistJson, new TypeToken<List<WhitelistEntry>>(){}.getType());
                    whitelisted = whitelist.stream().anyMatch(e -> e.name.equalsIgnoreCase(player));
                } catch (Exception e) {
                    // Si le parsing échoue, on vérifie avec une recherche basique
                    whitelisted = whitelistJson.contains("\"" + player + "\"");
                }
            }

            // Calcule le hash du fichier mode_client.zip (ou du dossier si le fichier n'existe pas)
            String serverHash = "";
            if (Files.exists(Paths.get("mode_client.zip"))) {
                serverHash = com.ezlauncher.utils.HashCalculator.calculateSHA256("mode_client.zip");
            } else if (Files.exists(Paths.get("mode_client"))) {
                // Si le fichier n'existe pas, on zippe le dossier et on calcule le hash du zip
                com.ezlauncher.utils.ZipUtils.zipDirectory("mode_client", "mode_client.zip");
                serverHash = com.ezlauncher.utils.HashCalculator.calculateSHA256("mode_client.zip");
            }

            boolean hashMatches = clientHash != null && clientHash.equals(serverHash);

            String response = String.format(
                "{\"whitelisted\": %s, \"hashMatches\": %s, \"upToDate\": %s, \"downloadUrl\": \"/api/download\", \"serverHash\": \"%s\"}",
                whitelisted, hashMatches, hashMatches, serverHash
            );

            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendError(exchange, 500, "Erreur interne : " + e.getMessage());
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length > 1) {
                result.put(keyValue[0], keyValue[1]);
            } else {
                result.put(keyValue[0], "");
            }
        }
        return result;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = String.format("{\"error\": \"%s\"}", message);
        sendResponse(exchange, statusCode, response);
    }
}

class WhitelistEntry {
    public String name;
    public String uuid;
}
