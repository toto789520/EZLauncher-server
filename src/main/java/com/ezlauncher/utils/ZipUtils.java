package com.ezlauncher.utils;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class ZipUtils {
    public static Path zipDirectory(String sourceDir, String zipFile) throws IOException {
        Path zipPath = Paths.get(zipFile);
        try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            Path sourcePath = Paths.get(sourceDir);
            zipFile(sourcePath, sourcePath.getFileName().toString(), zos);
        }
        return zipPath;
    }

    private static void zipFile(Path fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.toFile().isHidden()) {
            return;
        }
        if (fileToZip.toFile().isDirectory()) {
            if (!fileName.endsWith("/")) {
                fileName += "/";
            }
            zos.putNextEntry(new ZipEntry(fileName));
            zos.closeEntry();
            File[] children = fileToZip.toFile().listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile.toPath(), fileName + childFile.getName(), zos);
                }
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip.toFile())) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
        }
    }
}
