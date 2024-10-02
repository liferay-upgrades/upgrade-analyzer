package com.liferay.upgrades.analyzer.project.dependency.collector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleProjectCollector implements ProjectCollector {
    @Override
    public boolean matches(String fileName, Path file) {

        if (fileName.equals("build.gradle")
                && Files.exists(Paths.get(file.getParent().toString(), "src"))
                && !Files.exists(Paths.get(file.getParent().toString(), "liferay-theme.json"))) {
            return true;
        }

        return false;
    }

    @Override
    public void collect(Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {
        projectsDependencyGraphBuilder.addProject(
                getProjectKey(
                        file.getParent().toUri().getPath(),
                        file.getParent().getFileName().toString()),
                collectProjectDependencies(file));
    }

    private Set<ProjectKey> collectProjectDependencies(Path gradleFile) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader bufferedReader = Files.newBufferedReader(
                gradleFile, StandardCharsets.UTF_8)) {

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        Set<ProjectKey> dependencies = new HashSet<>();

        Matcher matcher = GRADLE_PROJECT_PATTERN.matcher(sb.toString());

        while (matcher.find()) {
            dependencies.add(getProjectKey(matcher.group(1)));
        }

        return dependencies;
    }

    private ProjectKey getProjectKey(String path, String rawProjectName) {
        ProjectKey projectKey = getProjectKey(rawProjectName);

        projectKey.setPath(path);

        return projectKey;
    }
    private ProjectKey getProjectKey(String rawProjectName) {
        String key = rawProjectName.trim().replaceAll("\'", "").replaceAll("\"", "");

        if (key.contains(":")) {
            ProjectKey projectKey = this.projectInfos.get(key);

            if (projectKey != null) {
                return projectKey;
            }

            String name = key.substring(key.lastIndexOf(":") + 1);

            projectKey = this.projectInfos.remove(name);

            if (projectKey == null) {
                projectKey = new ProjectKey(name);
            }

            projectKey.setName(name);
            projectKey.setKey(key);
            projectKey.setGroup(key.substring(0, key.lastIndexOf(":")));

            this.projectInfos.put(key, projectKey);
            this.projectInfos.put(name, projectKey);

            return projectKey;
        }

        return projectInfos.computeIfAbsent(key, name -> new ProjectKey(key));
    }

    private Map<String, ProjectKey> projectInfos = new HashMap<>();

    private static final Pattern GRADLE_PROJECT_PATTERN = Pattern.compile("project.*\\(*[\"'](.*)[\"']\\)");
}
