package com.ezlauncher.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloadHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Path modpackPath = Paths.get("mode_client.zip");
            if (!Files.exists(modpackPath)) {
                // Si le fichier n'existe pas, on zippe le dossier mode_client/
                modpackPath = com.ezlauncher.utils.ZipUtils.zipDirectory("mode_client", "mode_client.zip");
            }

            byte[] fileBytes = Files.readAllBytes(modpackPath);
            exchange.getResponseHeaders().set("Content-Type", "application/zip");
            exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"mode_client.zip\"");
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        } catch (Exception e) {
            sendError(exchange, 500, "Erreur lors du téléchargement : " + e.getMessage());
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = String.format("{\"error\": \"%s\"}", message);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
