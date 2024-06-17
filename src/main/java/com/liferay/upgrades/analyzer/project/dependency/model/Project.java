package com.liferay.upgrades.analyzer.project.dependency.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Project {

    private final ProjectDetails projectDetails;
    private final Set<Project> dependencies = new HashSet<>();

    private Set<Project> consumers = new HashSet<>();


    public Set<Project> getConsumers() {
        return consumers;
    }

    public void addConsumer(Project consumer) {
        this.consumers.add(consumer);
    }

    public ProjectDetails getProjectInfo() {
        return projectDetails;
    }

    public Set<Project> getDependencies() {
        return dependencies;
    }

    public Project(ProjectDetails projectDetails) {
        this.projectDetails = projectDetails;
    }

    public void addDependency(Project subProject) {
        dependencies.add(subProject);
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\": \"" + projectDetails.getName() + '\"' +
                ", \"dependencies\" : " + Arrays.deepToString(consumers.toArray()) +
                '}';
    }

    public String getKey() {
        return projectDetails.getName();
    }
}
