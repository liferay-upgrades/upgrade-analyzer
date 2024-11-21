package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.exporter.util.ExporterUtil;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GamePlanProjectDependencyExporter implements ProjectDependencyExporter<String> {
    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        StringBuilder sb = new StringBuilder();

        Map<Integer, Set<Project>> projectsMapLevels = ExporterUtil.createProjectLevel(projectsDependencyGraph);

        sb.append("This project contains " + ExporterUtil.countProjects(projectsMapLevels)+ " projects with " +
                projectsMapLevels.size() + " level(s) of project dependencies.");
        sb.append("\n");

        int level = 1;

        for (Map.Entry<Integer, Set<Project>> entry : projectsMapLevels.entrySet()) {
            List<Project> projects = new ArrayList<>(entry.getValue());

            if (projects.size() == 0) {
                continue;
            }

            sb.append("Level " + level++ + " count: " + projects.size());
            sb.append("\n");

            Collections.sort(projects, ExporterUtil.getProjectsComparator());

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


}