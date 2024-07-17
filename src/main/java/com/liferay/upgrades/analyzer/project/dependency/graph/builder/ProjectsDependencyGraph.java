package com.liferay.upgrades.analyzer.project.dependency.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.util.Collection;
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

    public int getDepth() {
        if (depth == -1) {
            depth = computeDepth(_leaves, 0);
        }

        return depth;
    }

    private int computeDepth(Collection<Project> projects, int depth) {
        int max = depth;

        for (Project project : projects) {
            int localDepth = computeDepth(project.getConsumers(), depth + 1);

            if (localDepth > max) {
                max = localDepth;
            }
        }

        return max;
    }

    private final Set<Project> _leaves = new HashSet<>();
    private int depth = -1;
}
