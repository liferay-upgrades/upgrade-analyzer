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
import java.util.stream.Stream;

public class CsvProjectDependencyExporter implements ProjectDependencyExporter<String> {

    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        StringBuilder sb = new StringBuilder();

        sb.append("Level," +
                "Bundle Name," +
                "Symbolic Name," +
                "Dependencies," +
                "Nº of compile errors before automation," +
                "Nº of compile errors after automation," +
                "Automation suggested fix?," +
                "Nº of errors fixed by suggestions," +
                "Nº of errors fixed (Total)," +
                "Obs");
        sb.append("\n");

        int level = 1;

        Map<Integer, Set<Project>> projectsMapLevels = ExporterUtil.createProjectLevel(projectsDependencyGraph);

        for (Map.Entry<Integer, Set<Project>> entry : projectsMapLevels.entrySet()) {
            List<Project> projects = new ArrayList<>(entry.getValue());

            if (projects.isEmpty()) {
                continue;
            }

            projects.sort(ExporterUtil.getProjectsComparator());

            for (Project project : projects) {
                sb.append(level);
                sb.append(",");
                sb.append(project.getName());
                sb.append(",");
                sb.append(project.getName());
                sb.append(",");
                sb.append(
                        Stream.of(
                                project.getDependencies()
                        ).flatMap(
                                Set::stream
                        ).map(
                                p -> p.getName()
                        ).collect(
                                Collectors.joining( " ")
                        )
                );
                sb.append(",");
                sb.append("1");
                sb.append(",");
                sb.append("0");
                sb.append(",");
                sb.append("FALSE");
                sb.append(",");
                sb.append(",");
                sb.append("0");
                sb.append(",");
                sb.append("\n");
            }

            level++;
        }

        long time = System.currentTimeMillis();

        File csvFile = new File("projects-" + time + ".csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write(sb.toString());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "CSV file generated at " + csvFile.getAbsolutePath();
    }

}