package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HookModuleProjectDetector implements ProjectDetector {

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.equals("liferay-hook.xml") && _validateHookModule(file);
    }

    @Override
    public void process(
        Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {

        Path hookModulePath = _getRootPath(file, 4);

        Project project = ProjectDetectorUtil.getProjectKey(hookModulePath);

        String detectorKey = String.format(
            "%s=%s", project.getKey(), HookModuleProjectDetector.class.getSimpleName());

        project.setKey(detectorKey);
        project.setName(project.getKey());

        projectsDependencyGraphBuilder.addProject(project, Collections.emptySet());
    }

    public Path _getRootPath(Path path, int levels) {
        Path current = path;

        for (int i = 0; i < levels && current != null; i++) {
            current = current.getParent();
        }

        return current;
    }

    private boolean _validateHookModule(Path file) {
        String hookContent = ProjectDetectorUtil.readFile(file);

        Matcher matcher = _LIFERAY_HOOK_PATTERN.matcher(hookContent);

        return matcher.find();
    }

    private static final Pattern _LIFERAY_HOOK_PATTERN = Pattern.compile(
        "liferay-hook_([0-9._*])+.dtd");

}
