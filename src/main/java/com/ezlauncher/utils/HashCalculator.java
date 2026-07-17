package com.ezlauncher.utils;

import java.nio.file.*;
import java.security.MessageDigest;
import java.util.stream.Stream;

public class HashCalculator {
    public static String calculateSHA256(String path) throws Exception {
        Path rootPath = Paths.get(path);
        if (!Files.exists(rootPath)) {
            return "";
        }

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (Stream<Path> paths = Files.walk(rootPath)) {
            paths
                .filter(Files::isRegularFile)
                .sorted()
                .forEach(file -> {
                    try {
                        byte[] fileBytes = Files.readAllBytes(file);
                        digest.update(fileBytes);
                    } catch (Exception e) {
                        throw new RuntimeException("Erreur lors du calcul du hash", e);
                    }
                });
        }
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
