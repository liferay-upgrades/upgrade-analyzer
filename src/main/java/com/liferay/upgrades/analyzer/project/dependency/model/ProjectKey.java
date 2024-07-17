package com.liferay.upgrades.analyzer.project.dependency.model;

import java.util.Objects;

public class ProjectKey {

    private String name;
    private String key;
    private String group;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;

    public ProjectKey(String name) {
        this.name = name;
        this.key = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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
}
