package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.liferay.upgrades.analyzer.project.dependency.exporter.util.ExporterUtil.visitConsumers;

public class GamePlanProjectDependencyExporter implements ProjectDependencyExporter<String> {
    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        StringBuilder sb = new StringBuilder();

        Set<Project> allProjects = new HashSet<>();

        visitConsumers(1, projectsDependencyGraph.getLeaves(), (level, project) -> {
            allProjects.add(project);
            Set<Project> projects = _projectsMapLevels.computeIfAbsent(level, key -> new HashSet<>());

            for (Map.Entry<Integer, Set<Project>> entry : _projectsMapLevels.entrySet()) {

                if (entry.getValue().contains(project)) {
                    if (entry.getKey() <= level) {
                        entry.getValue().remove(project);
                        break;
                    }
                    else {
                        return;
                    }
                }
            }

            projects.add(project);
        });

        sb.append("This project contains " + allProjects.size() + " projects with " +
                _projectsMapLevels.size() + " level(s) of project dependencies.");
        sb.append("\n");

        Comparator<Project> projectsComparator = Comparator.comparingInt(
                (Project p1) -> -p1.getConsumers().size()
        ).thenComparing(
                Project::getName
        );

        int level = 1;

        for (Map.Entry<Integer, Set<Project>> entry : _projectsMapLevels.entrySet()) {
            List<Project> projects = new ArrayList<>(entry.getValue());

            if (projects.size() == 0) {
                continue;
            }

            sb.append("Level " + level++ + " count: " + projects.size());
            sb.append("\n");

            Collections.sort(projects, projectsComparator);

            for (Project project : projects) {
                sb.append("\t");
                sb.append(project.getProjectInfo().getName() + " " + project.getConsumers().size());
                appendDependencies(sb, project);
                sb.append("\n");
            }
        }


        long time = System.currentTimeMillis();

        File gamePlanFile = new File("projects-" + time + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(gamePlanFile))) {
            writer.write(sb.toString());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "Game Plan generated at " + gamePlanFile.getAbsolutePath();
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


    private Map<Integer, Set<Project>> _projectsMapLevels = new TreeMap<>();
}