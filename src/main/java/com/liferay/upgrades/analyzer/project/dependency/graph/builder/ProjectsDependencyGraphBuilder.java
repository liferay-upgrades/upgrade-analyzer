package com.liferay.upgrades.analyzer.project.dependency.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProjectsDependencyGraphBuilder {

    public ProjectsDependencyGraphBuilder addProject(
        ProjectKey projectKey, Set<ProjectKey> dependencies) {

        ProjectKey project = _getOrCreate(projectKey);

        for (ProjectKey projectKeyDependency : dependencies) {
            ProjectKey dependencyProject = _getOrCreate(projectKeyDependency);

            project.addDependency(dependencyProject);
            dependencyProject.addConsumer(project);
        }

        if (!dependencies.isEmpty()) {
            _projectsDependencyGraph.removeLeaf(project);
        }

        return this;
    }

    public ProjectsDependencyGraph build() {
        return _projectsDependencyGraph;
    }

    private ProjectKey _getOrCreate(ProjectKey projectKey) {
        return  _projects.computeIfAbsent(
                projectKey.getName(), key ->  {
                ProjectKey newProject = new ProjectKey(projectKey.getKey());
                    _projectsDependencyGraph.addLeaf(newProject);

                return newProject;
            });
    }

    private final Map<String, ProjectKey> _projects = new HashMap<>();

    private final ProjectsDependencyGraph _projectsDependencyGraph =
        new ProjectsDependencyGraph();

}
