package com.liferay.upgrades.analyzer.project.dependency.exporter.util;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public class ExporterUtil {

    public static int countProjects(
        Map<Integer, Set<Project>> projectsMapLevels) {

        int count = 0;

        for (Set<Project> projects : projectsMapLevels.values()) {
            count += projects.size();
        }
        return count;
    }

    public static Map<Integer, Set<Project>> createProjectLevel(
        ProjectsDependencyGraph projectsDependencyGraph) {

        Map<Integer, Set<Project>> projectsMapLevels = new TreeMap<>();

        Set<Project> allProjects = new HashSet<>();

        Stack<Project> currentLevel = new Stack<>();

        long startTime = System.currentTimeMillis();

        projectsDependencyGraph.getLeaves().forEach(currentLevel::push);

        Set<Project> nextLevel = new HashSet<>();

        int level = 1;

        while (!currentLevel.isEmpty()) {
            Project project = currentLevel.pop();

            nextLevel.addAll(project.getConsumers());

            allProjects.add(project);

            if (_checkLevels(level, project, projectsMapLevels)) {
                Set<Project> currentLevelProjects = projectsMapLevels.computeIfAbsent(
                        level, key -> new HashSet<>());

                currentLevelProjects.add(project);
            }

            if (currentLevel.isEmpty()) {
                currentLevel.addAll(nextLevel);

                nextLevel.clear();

                level++;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Time Elapsed to create the project levels: " + (System.currentTimeMillis() - startTime) + "ms");
        }

        return projectsMapLevels;

    }

    public static Comparator<Project> getProjectsComparator() {
        return _projectsComparator;
    }

    public static void visitConsumers(
            int level, Set<Project> projects, BiConsumer<Integer, Project> doVisit) {

        for (Project project : projects) {
            doVisit.accept(level, project);

            visitConsumers(level + 1, project.getConsumers(), doVisit);
        }
    }

    private static boolean _checkLevels(
            int level, Project project, Map<Integer, Set<Project>> projectsMapLevels) {

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

   private static final Comparator<Project> _projectsComparator = Comparator.comparingInt(
        (Project p1) -> -p1.getConsumers().size()
    ).thenComparing(
        Project::getName
    );

    private static final Logger logger = LogManager.getLogger(ExporterUtil.class);

}