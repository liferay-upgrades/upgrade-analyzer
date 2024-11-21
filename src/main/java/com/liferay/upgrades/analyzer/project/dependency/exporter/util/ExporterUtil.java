package com.liferay.upgrades.analyzer.project.dependency.exporter.util;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public class ExporterUtil {

    public static Comparator<Project> getProjectsComparator() {
        return projectsComparator;
    }

    public static void visitConsumers(int level, Set<Project> projects, BiConsumer<Integer, Project> doVisit) {
        for (Project project : projects) {
            doVisit.accept(level, project);

            visitConsumers(level + 1, project.getConsumers(), doVisit);
        }
    }

    public static int countProjects(Map<Integer, Set<Project>> projectsMapLevels) {
        int count = 0;

        for (Set<Project> projects : projectsMapLevels.values()) {
            count += projects.size();
        }
        return count;
    }

    public static Map<Integer, Set<Project>> createProjectLevel(ProjectsDependencyGraph projectsDependencyGraph) {
        Map<Integer, Set<Project>> projectsMapLevels = new TreeMap<>();

        Set<Project> allProjects = new HashSet<>();

        Stack<Project> currentLevel = new Stack<>();

        projectsDependencyGraph.getLeaves().forEach(currentLevel::push);

        Stack<Project> nextLevel = new Stack<>();

        int level = 1;

        while (!currentLevel.isEmpty()) {
            Project project = currentLevel.pop();

            nextLevel.addAll(project.getConsumers());

            allProjects.add(project);

            if (checkLevels(level, project, projectsMapLevels)) {
                Set<Project> currentLevelProjects = projectsMapLevels.computeIfAbsent(level, key -> new HashSet<>());

                currentLevelProjects.add(project);
            }

            if (currentLevel.isEmpty()) {
                currentLevel.addAll(nextLevel);

                nextLevel.removeAllElements();

                level++;
            }
        }

        return projectsMapLevels;

    }

    private static boolean checkLevels(int level, Project project, Map<Integer, Set<Project>> projectsMapLevels) {
        boolean addToLevel = true;

        for (Map.Entry<Integer, Set<Project>> entry : projectsMapLevels.entrySet()) {

            if (entry.getValue().contains(project)) {
                if (entry.getKey() <= level) {
                    entry.getValue().remove(project);
                }
                else {
                    addToLevel = false;
                }

                break;
            }
        }

        return addToLevel;
    }

   private static Comparator<Project> projectsComparator = Comparator.comparingInt(
            (Project p1) -> -p1.getConsumers().size()
    ).thenComparing(
            Project::getName
    );
}
