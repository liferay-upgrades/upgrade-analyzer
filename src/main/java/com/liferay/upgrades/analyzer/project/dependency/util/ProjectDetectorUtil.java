package com.liferay.upgrades.analyzer.project.dependency.util;

import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ProjectDetectorUtil {

    public static ProjectKey getProjectKey(
        String rawProjectName, Map<String, ProjectKey> projectInfos) {

        String key = ProjectDetectorUtil.normalize(rawProjectName);

        if (key.contains(":")) {
            ProjectKey projectKey = projectInfos.get(key);

            if (projectKey != null) {
                return projectKey;
            }

            String name = key.substring(key.lastIndexOf(":") + 1);

            projectKey = projectInfos.remove(name);

            if (projectKey == null) {
                projectKey = new ProjectKey(name);
            }

            projectKey.setName(name);
            projectKey.setKey(key);
            projectKey.setGroup(key.substring(0, key.lastIndexOf(":")));

            projectInfos.put(key, projectKey);
            projectInfos.put(name, projectKey);

            return projectKey;
        }

        return projectInfos.computeIfAbsent(key, name -> new ProjectKey(key));
    }

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
