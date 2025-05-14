package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.exporter.util.ExporterUtil;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GamePlanProjectDependencyExporter implements ProjectDependencyExporter<String> {

    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        StringBuilder sb = new StringBuilder();

        Map<Integer, Set<Project>> projectsMapLevels = ExporterUtil.createProjectLevel(projectsDependencyGraph);

        sb.append("This project contains ")
            .append(ExporterUtil.countProjects(projectsMapLevels))
            .append(" projects with ")
            .append(projectsMapLevels.size())
            .append(" level(s) of project dependencies.");
        sb.append("\n");

        int level = 1;

        for (Map.Entry<Integer, Set<Project>> entry : projectsMapLevels.entrySet()) {
            List<Project> projects = new ArrayList<>(entry.getValue());

            if (projects.isEmpty()) {
                continue;
            }

            sb.append("Level ").append(level++)
                .append(" count: ")
                .append(projects.size());
            sb.append("\n");

            projects.sort(ExporterUtil.getProjectsComparator());

            for (Project project : projects) {
                sb.append("\t");
                sb.append(project.getName())
                    .append(" ")
                    .append(project.getConsumers().size());
                _appendDependencies(sb, project);
                sb.append("\n");
            }
        }

        File gamePlanFile = new File("projects-" + System.currentTimeMillis() + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(gamePlanFile))) {
            writer.write(sb.toString());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "Game Plan generated at " + gamePlanFile.getAbsolutePath();
    }

    private void _appendDependencies(StringBuilder sb, Project project) {
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

}