package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleProjectDetector implements ProjectDetector {

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
    public void process(Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {
        projectsDependencyGraphBuilder.addProject(
                getProjectKey(
                        file.getParent().toUri().getPath(),
                        file.getParent().getFileName().toString()),
                collectProjectDependencies(file));
    }

    private Set<Project> collectProjectDependencies(Path gradleFile) {
        Set<Project> dependencies = new HashSet<>();

        Matcher matcher = GRADLE_PROJECT_PATTERN.matcher(
            ProjectDetectorUtil.readFile(gradleFile));

        while (matcher.find()) {
            dependencies.add(getProjectKey(matcher.group(1)));
        }

        return dependencies;
    }

    private Project getProjectKey(String path, String rawProjectName) {
        Project project = getProjectKey(rawProjectName);

        project.setPath(path);

        return project;
    }
    
    private Project getProjectKey(String rawProjectName) {
       return ProjectDetectorUtil.getProjectKey(rawProjectName, projectInfos);
    }

    private final Map<String, Project> projectInfos = new HashMap<>();

    private static final Pattern GRADLE_PROJECT_PATTERN = Pattern.compile(
        "project.*\\(*[\"'](.*)[\"']\\)");

}
