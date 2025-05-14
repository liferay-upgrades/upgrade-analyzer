package com.liferay.upgrades.analyzer.project.dependency.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Project {

    public Project(String name) {
        this.name = name;
        this.key = name;
    }

    public Project(String name, String path) {
        this(name);
        this.path = path;
    }

    public void addConsumer(Project consumer) {
        this.consumers.add(consumer);
    }

    public Set<Project> getConsumers() {
        return consumers;
    }

    public void addDependency(Project subProject) {
        this.dependencies.add(subProject);
    }

    public Set<Project> getDependencies() {
        return dependencies;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project that = (Project) o;
        return Objects.equals(name, that.name) && Objects.equals(key, that.key) && Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, key, group);
    }

    @Override
    public String toString() {
        return String.format(
            "{\n \"name\": \"%s\",\n \"dependencies\": %s \n}",
            name, Arrays.deepToString(consumers.toArray()));
    }

    private String group;
    private String key;
    private String path;
    private String name;

    private final Set<Project> dependencies = new HashSet<>();
    private final Set<Project> consumers = new HashSet<>();

}
