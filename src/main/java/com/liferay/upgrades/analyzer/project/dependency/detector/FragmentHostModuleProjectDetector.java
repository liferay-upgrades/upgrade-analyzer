package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.util.Collections;

public class FragmentHostModuleProjectDetector implements ProjectDetector {

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.equals("bnd.bnd") && _validateFragmentHostModule(file);
    }

    @Override
    public void process(
        Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {

        Project project = ProjectDetectorUtil.getProjectKey(file);

        String keyWithDetector = String.format(
            "%s=%s", project.getKey(), FragmentHostModuleProjectDetector.class.getSimpleName());

        project.setKey(keyWithDetector);
        project.setName(project.getKey());

        projectsDependencyGraphBuilder.addProject(
            project, Collections.emptySet());
    }

    private boolean _validateFragmentHostModule(Path file) {
        String bndContent = ProjectDetectorUtil.readFile(file);

        return !bndContent.isBlank() && bndContent.contains("Fragment-Host:");
    }

}
