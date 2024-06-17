package com.liferay.upgrades.analyzer.project.dependency.analyzer;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.ProjectDetails;
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

    public ProjectsDependencyGraph analyze(String rootProjectPath) throws IOException {

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

                if (fileName.equals("build.gradle")  &&  Files.exists(Paths.get(file.getParent().toString(), "src"))) {
                    projectsDependencyGraphBuilder.addProject(
                            getProjectInfo(file.getParent().toUri().getPath(), file.getParent().getFileName().toString()),
                            collectProjectDependencies(file));
                }

                return FileVisitResult.CONTINUE;
            }
        });

        return projectsDependencyGraphBuilder.build();

    }

    private Set<ProjectDetails> collectProjectDependencies(Path gradleFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader bufferedReader = Files.newBufferedReader(
                gradleFile, StandardCharsets.UTF_8)) {

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }

        Set<ProjectDetails> dependencies = new HashSet<>();

        Matcher matcher = GRADLE_PROJECT_PATTERN.matcher(sb.toString());

        while (matcher.find()) {
            dependencies.add(getProjectInfo(matcher.group(1)));
        }

        return dependencies;
    }

    private ProjectDetails getProjectInfo(String path, String rawProjectName) {
        ProjectDetails projectInfo = getProjectInfo(rawProjectName);

        projectInfo.setPath(path);

        return projectInfo;
    }
    private ProjectDetails getProjectInfo(String rawProjectName) {
        String key = rawProjectName.trim().replaceAll("\'", "").replaceAll("\"", "");

        if (key.contains(":")) {
            ProjectDetails projectDetails = this.projectInfos.get(key);

            if (projectDetails != null) {
                return projectDetails;
            }

            String name = key.substring(key.lastIndexOf(":") + 1);

            projectDetails = this.projectInfos.remove(name);

            if (projectDetails == null) {
                projectDetails = new ProjectDetails(name);
            }

            projectDetails.setName(name);
            projectDetails.setKey(key);
            projectDetails.setGroup(key.substring(0, key.lastIndexOf(":")));

            this.projectInfos.put(key, projectDetails);
            this.projectInfos.put(name, projectDetails);

            return projectDetails;
        }

        return projectInfos.computeIfAbsent(key, name -> new ProjectDetails(key));
    }

    private Map<String, ProjectDetails> projectInfos = new HashMap<>();

    private static final Pattern GRADLE_PROJECT_PATTERN = Pattern.compile("project.*\\(*[\"'](.*)[\"']\\)");
}
