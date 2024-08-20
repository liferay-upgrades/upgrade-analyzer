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

import static com.liferay.upgrades.analyzer.project.dependency.exporter.util.ExporterUtil.visitConsumers;

public class CsvProjectDependencyExporter implements ProjectDependencyExporter<String> {
    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {

        visitConsumers(1, projectsDependencyGraph.getLeaves(), (level, project) -> {
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

        StringBuilder sb = new StringBuilder();

        sb.append("Level,Module Name,Errors Before,Errors After");
        sb.append("\n");

        Comparator<Project> projectsComparator = Comparator.comparingInt(
                (Project p1) -> -p1.getConsumers().size()
        ).thenComparingInt(
                (Project p2) -> -p2.getConsumers().size()
        );

        int level = 1;

        for (Map.Entry<Integer, Set<Project>> entry : _projectsMapLevels.entrySet()) {
            List<Project> projects = new ArrayList<>(entry.getValue());

            if (projects.size() == 0) {
                continue;
            }

            Collections.sort(projects, projectsComparator);

            for (Project project : projects) {
                sb.append(level);
                sb.append(",");
                sb.append(project.getName());
                sb.append(",");
                sb.append(",");
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


    private Map<Integer, Set<Project>> _projectsMapLevels = new TreeMap<>();
}