package com.liferay.upgrades.analyzer.project.dependency.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProjectsDependencyGraphBuilder {

    public ProjectsDependencyGraphBuilder addProject(
        Project projectKey, Set<Project> dependencies) {

        Project project = _getOrCreate(projectKey);

        for (Project projectDependency : dependencies) {
            Project dependencyProject = _getOrCreate(projectDependency);

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

    private Project _getOrCreate(Project project) {
        return  _projects.computeIfAbsent(
                project.getName(), key ->  {
                Project newProject = new Project(project.getKey());
                    _projectsDependencyGraph.addLeaf(newProject);

                return newProject;
            });
    }

    private final Map<String, Project> _projects = new HashMap<>();

    private final ProjectsDependencyGraph _projectsDependencyGraph =
        new ProjectsDependencyGraph();

}
