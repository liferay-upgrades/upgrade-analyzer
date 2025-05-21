package com.liferay.upgrades.analyzer.project.dependency.util;

import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ProjectDetectorUtil {

    public static Project getProjectKey(
        String rawProjectName, Map<String, Project> projectInfos) {

        String key = ProjectDetectorUtil.normalize(rawProjectName);

        if (key.contains(":")) {
            Project project = projectInfos.get(key);

            if (project != null) {
                return project;
            }

            String name = key.substring(key.lastIndexOf(":") + 1);

            project = projectInfos.remove(name);

            if (project == null) {
                project = new Project(name);
            }

            project.setName(name);
            project.setKey(key);
            project.setGroup(key.substring(0, key.lastIndexOf(":")));

            projectInfos.put(key, project);
            projectInfos.put(name, project);

            return project;
        }

        return projectInfos.computeIfAbsent(key, name -> new Project(key));
    }

    public static Project getProjectKey(Path file) {
        String path = file.getParent().toUri().getPath();
        String fileName = file.getParent().getFileName().toString();

        String key = ProjectDetectorUtil.normalize(fileName);

        return new Project(key, path);
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
