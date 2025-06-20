package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.util.Collections;

/**
 * @author Albert Gomes Cabral
 */
abstract public class BaseStartupProjectDetector implements ProjectDetector {

    protected abstract String getClassName();

    @Override
    public void process(
        Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {

        Project project = ProjectDetectorUtil.getProjectKey(file);

        String detectorKey = String.format("%s=%s", project.getKey(), getClassName());

        project.setKey(detectorKey);
        project.setName(project.getKey());

        projectsDependencyGraphBuilder.addProject(project, Collections.emptySet());
    }

}
