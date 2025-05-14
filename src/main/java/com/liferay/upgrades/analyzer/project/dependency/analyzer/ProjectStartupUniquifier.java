package com.liferay.upgrades.analyzer.project.dependency.analyzer;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ProjectStartupUniquifier {
    public List<List<ProjectKey>> uniquify(ProjectsDependencyGraph projectsDependencyGraph) {
        Stack<ProjectKey> stack = new Stack<>();

        projectsDependencyGraph.getLeaves().forEach(stack::push);

        List<List<ProjectKey>> uniqueProjects = new ArrayList<>();

        uniqueProjects.add(new ArrayList<>());//Services and APIs
        uniqueProjects.add(new ArrayList<>());//Utils
        uniqueProjects.add(new ArrayList<>());//Commons
        uniqueProjects.add(new ArrayList<>());//Hooks
        uniqueProjects.add(new ArrayList<>());//Fragments
        uniqueProjects.add(new ArrayList<>());//Others

        while (!stack.isEmpty()) {
            ProjectKey currentProject = stack.pop();

            boolean isUniqueProject = true;
            for (int i = 0; i < 5 && isUniqueProject; i++) {
                for (int j = 0; j < uniqueProjects.get(i).size() && isUniqueProject; j++) {
                    if (uniqueProjects.get(i).get(j).getName().equals(currentProject.getName()))
                        isUniqueProject = false;
                }
            }
            if (isUniqueProject) {
                String[] moduleNameSplited = currentProject.getName().split("-");
                boolean moduleTypeFound = false;
                for (int i = moduleNameSplited.length-1; i > 0 && !moduleTypeFound; i--) {
                    String maduleNameFragment = moduleNameSplited[i].toUpperCase();
                    if (maduleNameFragment.equals("API") || maduleNameFragment.equals("SERVICE")) {
                        uniqueProjects.get(0).add(currentProject);
                        moduleTypeFound = true;
                    } else if (maduleNameFragment.equals("UTILS") || maduleNameFragment.equals("UTIL")) {
                        uniqueProjects.get(1).add(currentProject);
                        moduleTypeFound = true;
                    } else if (maduleNameFragment.equals("COMMONS") || maduleNameFragment.equals("COMMON")) {
                        uniqueProjects.get(2).add(currentProject);
                        moduleTypeFound = true;
                    } else if (maduleNameFragment.equals("HOOKS") || maduleNameFragment.equals("HOOK")) {
                        uniqueProjects.get(3).add(currentProject);
                        moduleTypeFound = true;
                    } else if (maduleNameFragment.equals("FRAGMENTS") || maduleNameFragment.equals("FRAGMENT")) {
                        uniqueProjects.get(4).add(currentProject);
                        moduleTypeFound = true;
                    }
                }
                if (!moduleTypeFound) uniqueProjects.get(5).add(currentProject);
            }
        }
        return uniqueProjects;
    }
}
