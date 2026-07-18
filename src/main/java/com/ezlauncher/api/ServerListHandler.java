package com.ezlauncher.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ServerListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Properties props = new Properties();
            props.load(Files.newInputStream(Paths.get("server.properties")));
            String motd = props.getProperty("motd", "Un serveur Minecraft");

            String version = "1.19.2-forge-43.2.0";

            String smallIcon = "/server-icon.png";
            String bigIcon = Files.exists(Paths.get("server-icon-big.png")) ? "/server-icon-big.png" : null;

            // Calcule le hash du fichier mode_client.zip (ou du dossier si le fichier n'existe pas)
            String hash = "";
            if (Files.exists(Paths.get("mode_client.zip"))) {
                hash = com.ezlauncher.utils.HashCalculator.calculateSHA256("mode_client.zip");
            } else if (Files.exists(Paths.get("mode_client"))) {
                // Si le fichier n'existe pas, on zippe le dossier et on calcule le hash du zip
                com.ezlauncher.utils.ZipUtils.zipDirectory("mode_client", "mode_client.zip");
                hash = com.ezlauncher.utils.HashCalculator.calculateSHA256("mode_client.zip");
            }

            boolean isPrivate = Files.exists(Paths.get("whitelist.json"));

            String serverIp = exchange.getLocalAddress().getAddress().getHostAddress();

            String response = String.format(
                "{\"servers\": [{\"ip\": \"%s\", \"port\": 25565, \"name\": \"%s\", \"motd\": \"%s\", \"version\": \"%s\", \"smallIcon\": \"%s\", \"bigIcon\": %s, \"hash\": \"%s\", \"isPrivate\": %s}]}",
                serverIp,
                "Mon Serveur",
                escapeJson(motd),
                version,
                smallIcon,
                bigIcon != null ? "\"" + bigIcon + "\"" : "null",
                hash,
                isPrivate
            );

            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendError(exchange, 500, "Erreur interne : " + e.getMessage());
        }
    }

    private String escapeJson(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
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
