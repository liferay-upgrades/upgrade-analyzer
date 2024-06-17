package com.liferay.upgrades.analyzer.project.dependency.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ProjectsDependencyGraph {


    public Set<Project> getLeaves() {
        return Collections.unmodifiableSet(_leaves);
    }

    protected void addLeaf(Project leaf) {
        _leaves.add(leaf);
    }

    protected void removeLeaf(Project leaf) {
        _leaves.remove(leaf);
    }

    private final Set<Project> _leaves = new HashSet<>();
}
