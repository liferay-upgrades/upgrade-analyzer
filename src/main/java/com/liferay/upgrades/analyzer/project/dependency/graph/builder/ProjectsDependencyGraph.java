package com.liferay.upgrades.analyzer.project.dependency.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ProjectsDependencyGraph {

    protected void addLeaf(Project leaf) {
        _leaves.add(leaf);
    }

    protected void removeLeaf(Project leaf) {
        _leaves.remove(leaf);
    }

    public int getDepth() {
        if (depth == -1) {
            depth = _computeDepth(_leaves, 0);
        }

        return depth;
    }

    public Set<Project> getLeaves() {
        return Collections.unmodifiableSet(_leaves);
    }

    private int _computeDepth(Collection<Project> projects, int depth) {
        int max = depth;

        for (Project project : projects) {
            int localDepth = _computeDepth(project.getConsumers(), depth + 1);

            if (localDepth > max) {
                max = localDepth;
            }
        }

        return max;
    }

    private int depth  = -1;
    private final Set<Project> _leaves = new HashSet<>();

}
