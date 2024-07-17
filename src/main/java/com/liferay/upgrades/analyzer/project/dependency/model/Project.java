package com.liferay.upgrades.analyzer.project.dependency.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class Project {

    private final ProjectKey projectKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return Objects.equals(projectKey, project.projectKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectKey);
    }

    private final Set<Project> dependencies = new HashSet<>();

    private Set<Project> consumers = new HashSet<>();


    public Set<Project> getConsumers() {
        return consumers;
    }

    public void addConsumer(Project consumer) {
        this.consumers.add(consumer);
    }

    public ProjectKey getProjectInfo() {
        return projectKey;
    }

    public Set<Project> getDependencies() {
        return dependencies;
    }

    public Project(ProjectKey projectKey) {
        this.projectKey = projectKey;
    }

    public void addDependency(Project subProject) {
        dependencies.add(subProject);
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\": \"" + projectKey.getName() + '\"' +
                ", \"dependencies\" : " + Arrays.deepToString(consumers.toArray()) +
                '}';
    }

    public String getName() {
        return projectKey.getName();
    }
}
