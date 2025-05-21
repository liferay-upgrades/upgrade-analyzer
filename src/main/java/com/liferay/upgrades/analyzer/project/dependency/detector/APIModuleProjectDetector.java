package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIModuleProjectDetector implements ProjectDetector {

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.contains("build.gradle") && _validateAPIModule(file);
    }

    @Override
    public void process(
        Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {

        Matcher matcher = _BUILD_SERVICE_API.matcher(
            ProjectDetectorUtil.readFile(file));

        Project project = null;

        if (matcher.find()) {
            String packageName = matcher.group(1);

            String keyWithDetector =
                String.format(
                    "%s=%s", packageName.split("/")[0], APIModuleProjectDetector.class.getSimpleName());

            project = new Project(keyWithDetector);
        }

        projectsDependencyGraphBuilder.addProject(project, Collections.emptySet());
    }

    private boolean _validateAPIModule(Path file) {
        String buildGradleContent = ProjectDetectorUtil.readFile(file);

        Matcher matcher = _BUILD_SERVICE_API.matcher(buildGradleContent);

        return !buildGradleContent.isBlank() && matcher.find();
    }

    private static final Pattern _BUILD_SERVICE_API = Pattern.compile(
        "buildService\\s+\\{\\s*apiDir+\\s=\\s\"../(.+)+\\n*}");

}
