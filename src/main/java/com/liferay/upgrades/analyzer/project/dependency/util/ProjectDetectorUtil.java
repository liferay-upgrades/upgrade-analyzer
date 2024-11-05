package com.liferay.upgrades.analyzer.project.dependency.util;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectDetectorUtil {

    public static String normalize(String key) {
        return key.trim().replaceAll("'", "").replaceAll("\"", "");
    }

    public static String readFile(Path path) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(
                path, StandardCharsets.UTF_8)) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
