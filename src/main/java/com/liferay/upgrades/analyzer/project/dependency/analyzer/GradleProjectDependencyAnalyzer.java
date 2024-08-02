package com.liferay.upgrades.analyzer.project.dependency.analyzer;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleProjectDependencyAnalyzer {
    private final ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder;

    public GradleProjectDependencyAnalyzer() {
         projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();
    }

    public ProjectsDependencyGraph analyze(String rootProjectPath) {

        try {
            Files.walkFileTree(Paths.get(rootProjectPath), new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.getFileName().toString().equals("src")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = file.getFileName().toString();

                    if (fileName.equals("build.gradle") && Files.exists(Paths.get(file.getParent().toString(), "src"))) {
                        projectsDependencyGraphBuilder.addProject(
                                getProjectInfo(
                                        file.getParent().toUri().getPath(),
                                        file.getParent().getFileName().toString()),
                                collectProjectDependencies(file));
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (NoSuchFileException noSuchFileExceptionSuchFileException) {
            System.out.println(rootProjectPath + " directory is not available");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return projectsDependencyGraphBuilder.build();

    }

    private Set<ProjectKey> collectProjectDependencies(Path gradleFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader bufferedReader = Files.newBufferedReader(
                gradleFile, StandardCharsets.UTF_8)) {

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }

        Set<ProjectKey> dependencies = new HashSet<>();

        Matcher matcher = GRADLE_PROJECT_PATTERN.matcher(sb.toString());

        while (matcher.find()) {
            dependencies.add(getProjectInfo(matcher.group(1)));
        }

        return dependencies;
    }

    private ProjectKey getProjectInfo(String path, String rawProjectName) {
        ProjectKey projectInfo = getProjectInfo(rawProjectName);

        projectInfo.setPath(path);

        return projectInfo;
    }
    private ProjectKey getProjectInfo(String rawProjectName) {
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
