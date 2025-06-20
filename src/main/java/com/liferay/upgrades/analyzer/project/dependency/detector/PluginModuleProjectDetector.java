package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * @author Albert Gomes Cabral
 */
public class PluginModuleProjectDetector implements ProjectDetector {

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.equals(_FILE_NAME) && _validatePluginModule(file);
    }

    @Override
    public void process(
        Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {

        String basePathFile = _BASE_PLUGIN_PATH + _FILE_NAME;

        String moduleSimplePath = file.toString().substring(
            0, file.toString().length() - basePathFile.length());

        Project project = ProjectDetectorUtil.getProjectKey(Paths.get(moduleSimplePath));

        String detectorKey = String.format(
            "%s=%s", project.getKey(), PluginModuleProjectDetector.class.getSimpleName());

        project.setKey(detectorKey);
        project.setName(project.getKey());

        projectsDependencyGraphBuilder.addProject(project, Collections.emptySet());
    }

    private boolean _validatePluginModule(Path path) {
        String basePath = path.getParent().toString();

        Path lockAndFeelPath = Paths.get(
            basePath.concat("/liferay-look-and-feel.xml"));

        return !lockAndFeelPath.toFile().exists();
    }

    private static final String _FILE_NAME = "liferay-plugin-package.properties";
    private static final String _BASE_PLUGIN_PATH = "/main/webapp/WEB-INF/";

}
