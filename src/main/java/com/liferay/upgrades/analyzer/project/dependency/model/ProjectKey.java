package com.liferay.upgrades.analyzer.project.dependency.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProjectKey {

    public ProjectKey(String name) {
        this.name = name;
        this.key = name;
    }

    public ProjectKey(String name, String path) {
        this(name);
        this.path = path;
    }

    public void addConsumer(ProjectKey consumer) {
        this.consumers.add(consumer);
    }

    public Set<ProjectKey> getConsumers() {
        return consumers;
    }

    public void addDependency(ProjectKey subProject) {
        this.dependencies.add(subProject);
    }

    public Set<ProjectKey> getDependencies() {
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
        ProjectKey that = (ProjectKey) o;
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

    private final Set<ProjectKey> dependencies = new HashSet<>();
    private final Set<ProjectKey> consumers = new HashSet<>();

}
