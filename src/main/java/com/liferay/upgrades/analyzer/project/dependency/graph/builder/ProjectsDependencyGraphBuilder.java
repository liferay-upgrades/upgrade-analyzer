package com.liferay.upgrades.analyzer.project.dependency.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.model.ProjectDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProjectsDependencyGraphBuilder {

    public ProjectsDependencyGraphBuilder addProject(ProjectDetails projectDetails, Set<ProjectDetails> dependencies) {
        Project project = getOrCreate(projectDetails);

        for (ProjectDetails projectDetailsDependency : dependencies) {
            Project dependencyProject = getOrCreate(projectDetailsDependency);

            project.addDependency(dependencyProject);
            dependencyProject.addConsumer(project);
        }

        if (!dependencies.isEmpty()) {
            _projectsDependencyGraph.removeLeaf(project);
        }

        return this;
    }

    private Project getOrCreate(ProjectDetails projectDetails) {
        return  _projects.computeIfAbsent(
                projectDetails, key ->  {
                Project newProject = new Project(projectDetails);
                    _projectsDependencyGraph.addLeaf(newProject);

                return newProject;
            });
    }

    private Map<ProjectDetails, Project> _projects = new HashMap<>();

    private ProjectsDependencyGraph _projectsDependencyGraph = new ProjectsDependencyGraph();

    public ProjectsDependencyGraph build() {
        return _projectsDependencyGraph;
    }
}
