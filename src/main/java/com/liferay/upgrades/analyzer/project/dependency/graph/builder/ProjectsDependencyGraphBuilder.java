package com.liferay.upgrades.analyzer.project.dependency.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProjectsDependencyGraphBuilder {

    public ProjectsDependencyGraphBuilder addProject(ProjectKey projectKey, Set<ProjectKey> dependencies) {
        Project project = getOrCreate(projectKey);

        for (ProjectKey projectKeyDependency : dependencies) {
            Project dependencyProject = getOrCreate(projectKeyDependency);

            project.addDependency(dependencyProject);
            dependencyProject.addConsumer(project);
        }

        if (!dependencies.isEmpty()) {
            _projectsDependencyGraph.removeLeaf(project);
        }

        return this;
    }

    private Project getOrCreate(ProjectKey projectKey) {
        return  _projects.computeIfAbsent(
                projectKey, key ->  {
                Project newProject = new Project(projectKey);
                    _projectsDependencyGraph.addLeaf(newProject);

                return newProject;
            });
    }

    private Map<ProjectKey, Project> _projects = new HashMap<>();

    private ProjectsDependencyGraph _projectsDependencyGraph = new ProjectsDependencyGraph();

    public ProjectsDependencyGraph build() {
        return _projectsDependencyGraph;
    }
}
