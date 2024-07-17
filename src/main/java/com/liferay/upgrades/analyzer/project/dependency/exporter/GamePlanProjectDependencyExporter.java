package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class GamePlanProjectDependencyExporter implements ProjectDependencyExporter<String> {
    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        StringBuilder sb = new StringBuilder();

        visitConsumers(1, projectsDependencyGraph.getLeaves(), (level, project) -> {
            Set<Project> projects = _projectsMapLevels.computeIfAbsent(level, key -> new HashSet<>());

            for (Map.Entry<Integer, Set<Project>> entry : _projectsMapLevels.entrySet()) {

                if (entry.getValue().contains(project)) {
                    if (entry.getKey() < level) {
                        entry.getValue().remove(project);
                    }

                    return;
                }
            }

            projects.add(project);
        });

        sb.append("This project contains " +
                _projectsMapLevels.size() + " level(s) of project dependencies.");
        sb.append("\n");

        Comparator<Project> projectsComparator = Comparator.comparingInt(
                (Project p1) -> -p1.getConsumers().size()
        ).thenComparingInt(
                (Project p2) -> -p2.getConsumers().size()
        );

        for (Map.Entry<Integer, Set<Project>> entry : _projectsMapLevels.entrySet()) {
            sb.append("Level " + entry.getKey() + " count: " + entry.getValue().size());
            sb.append("\n");

            List<Project> projects = new ArrayList<>(entry.getValue());

            Collections.sort(projects, projectsComparator);

            for (Project project : projects) {
                sb.append("\t");
                sb.append(project.getProjectInfo().getName() + " " + project.getConsumers().size());
                appendDependencies(sb, project);
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    private void appendDependencies(StringBuilder sb, Project project) {
        Set<Project> dependencies = project.getDependencies();

        if (dependencies.isEmpty()) {
            return;
        }

        sb.append(" (");

        sb.append(
            dependencies.stream().map(
                    Project::getName
            ).collect(
                    Collectors.joining(", ")
            )
        );

        sb.append(")");
    }

    private void visitConsumers(int level, Set<Project> projects, BiConsumer<Integer, Project> doVisit) {
        for (Project project : projects) {
            doVisit.accept(level, project);

            visitConsumers(level + 1, project.getConsumers(), doVisit);
        }
    }

    private Map<Integer, Set<Project>> _projectsMapLevels = new TreeMap<>();
}