package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonPlusUtilModuleDetector implements ProjectDetector {

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.equals("bnd.bnd") && _validateCommonPlusUtil(file);
    }

    @Override
    public void process(
        Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {

        Project project = ProjectDetectorUtil.getProjectKey(file);

        String detectorKey = String.format(
            "%s=%s", project.getKey(), CommonPlusUtilModuleDetector.class.getSimpleName());

        project.setKey(detectorKey);
        project.setName(project.getKey());

        projectsDependencyGraphBuilder.addProject(project, Collections.emptySet());
    }

    private boolean _validateCommonPlusUtil(Path file) {
        String content = ProjectDetectorUtil.readFile(file);

        if (content.contains("Web-ContextPath") || content.contains("-includeresource:")) {
            return false;
        }

        Matcher matcher = _EXPORT_PACKAGE_PATTERN.matcher(content);

        if (matcher.find()) {
            String group = matcher.group(1);

            if (!group.isBlank()) {
                String[] dependencies = group.split(",");

                return dependencies.length > 1;
            }
        }

        return false;
    }

    private static final Pattern _EXPORT_PACKAGE_PATTERN = Pattern.compile(
        "Export-Package:\\\\(\\s*\\n(?:\\s+.+\\\\s*\\n)*\\s+.+\\n?)");

}
