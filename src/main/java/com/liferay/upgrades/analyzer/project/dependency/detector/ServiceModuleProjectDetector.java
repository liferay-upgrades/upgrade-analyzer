package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.util.Collections;

public class ServiceModuleProjectDetector implements ProjectDetector {

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.equals("bnd.bnd") && _validateServiceXML(file);
    }

    @Override
    public void process(
        Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {

        Project project = ProjectDetectorUtil.getProjectKey(file);

        String detectorKey = String.format(
            "%s=%s", project.getKey(), ServiceModuleProjectDetector.class.getSimpleName());

        project.setKey(detectorKey);
        project.setName(project.getKey());

        projectsDependencyGraphBuilder.addProject(project, Collections.emptySet());
    }

    private boolean _validateServiceXML(Path file) {
        String serviceXMLContent = ProjectDetectorUtil.readFile(file);

        return !serviceXMLContent.isBlank() &&
                serviceXMLContent.contains("Liferay-Service: true");
    }

}
