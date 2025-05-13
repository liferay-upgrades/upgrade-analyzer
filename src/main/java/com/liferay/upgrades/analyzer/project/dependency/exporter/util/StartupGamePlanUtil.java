package com.liferay.upgrades.analyzer.project.dependency.exporter.util;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StartupGamePlanUtil {

    public static List<List<Project>> uniquify(
        ProjectsDependencyGraph projectsDependencyGraph) {

        Stack<Project> stack = new Stack<>();

        projectsDependencyGraph.getLeaves().forEach(stack::push);

        List<List<Project>> uniqueProjects = new ArrayList<>();

        uniqueProjects.add(new ArrayList<>());//Services and APIs
        uniqueProjects.add(new ArrayList<>());//Utils
        uniqueProjects.add(new ArrayList<>());//Commons
        uniqueProjects.add(new ArrayList<>());//Hooks
        uniqueProjects.add(new ArrayList<>());//Fragments
        uniqueProjects.add(new ArrayList<>());//Others

        while (!stack.isEmpty()) {
            Project currentProject = stack.pop();

            boolean isUniqueProject = true;

            for (int i = 0; i < 5 && isUniqueProject; i++) {
                for (int j = 0; j < uniqueProjects.get(i).size() && isUniqueProject; j++) {
                    if (uniqueProjects.get(i).get(j).getName().equals(currentProject.getName()))
                        isUniqueProject = false;
                }
            }

            if (isUniqueProject) {
                String[] moduleNameSplit = currentProject.getName().split("-");

                boolean moduleTypeFound = false;

                for (int i = moduleNameSplit.length-1; i > 0 && !moduleTypeFound; i--) {
                    String moduleNameFragment = moduleNameSplit[i].toUpperCase();

                    switch (moduleNameFragment) {
                        case "API":
                        case "SERVICE":
                            uniqueProjects.get(0).add(currentProject);
                            moduleTypeFound = true;
                            break;
                        case "UTILS":
                        case "UTIL":
                            uniqueProjects.get(1).add(currentProject);
                            moduleTypeFound = true;
                            break;
                        case "COMMONS":
                        case "COMMON":
                            uniqueProjects.get(2).add(currentProject);
                            moduleTypeFound = true;
                            break;
                        case "HOOKS":
                        case "HOOK":
                            uniqueProjects.get(3).add(currentProject);
                            moduleTypeFound = true;
                            break;
                        case "FRAGMENTS":
                        case "FRAGMENT":
                            uniqueProjects.get(4).add(currentProject);
                            moduleTypeFound = true;
                            break;
                    }
                }

                if (!moduleTypeFound) uniqueProjects.get(5).add(currentProject);
            }
        }

        return uniqueProjects;
    }

}
