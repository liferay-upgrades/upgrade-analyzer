package com.liferay.upgrades.analyzer.project.dependency.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ProjectsDependencyGraph {

    protected void addLeaf(ProjectKey leaf) {
        _leaves.add(leaf);
    }

    protected void removeLeaf(ProjectKey leaf) {
        _leaves.remove(leaf);
    }

    public int getDepth() {
        if (depth == -1) {
            depth = _computeDepth(_leaves, 0);
        }

        return depth;
    }

    public Set<ProjectKey> getLeaves() {
        return Collections.unmodifiableSet(_leaves);
    }

    private int _computeDepth(Collection<ProjectKey> projects, int depth) {
        int max = depth;

        for (ProjectKey project : projects) {
            int localDepth = _computeDepth(project.getConsumers(), depth + 1);

            if (localDepth > max) {
                max = localDepth;
            }
        }

        return max;
    }

    private int depth  = -1;
    private final Set<ProjectKey> _leaves = new HashSet<>();

}
