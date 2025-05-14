package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.util.Collections;

import org.json.JSONObject;

public class JSPortletProjectDetector implements ProjectDetector {

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.equals("package.json") && _validateJsonContent(file);
    }

    @Override
    public void process(Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {
        projectsDependencyGraphBuilder.addProject(
                _getProjectKey(file),  Collections.emptySet());
    }

    private Project _getProjectKey(Path file) {
        String path = file.getParent().toUri().getPath();
        String fileName = file.getParent().getFileName().toString();

        String key = ProjectDetectorUtil.normalize(fileName);

        return new Project(key, path);
    }

    private boolean _validateJsonContent(Path contentPath) {
            String jsonContent = ProjectDetectorUtil.readFile(contentPath);

        if (jsonContent.isEmpty()) {
            return false;
        }

        JSONObject jsonObject = new JSONObject(jsonContent);

        if (jsonObject.has("portlet")) {
            String jsonPortlet = jsonObject.get("portlet").toString();

            return jsonPortlet.contains("com.liferay.portlet.display-category");
        }

        return false;
    }

}
