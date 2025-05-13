package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.exporter.util.StartupGamePlanUtil;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StartupCsvProjectDependencyExporter
    implements ProjectDependencyExporter<String> {

    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        List<List<Project>> uniqueProjects =
            StartupGamePlanUtil.uniquify(projectsDependencyGraph);

        return _export(uniqueProjects);
    }

    private String _export(List<List<Project>> uniqueProjects) {
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

        for (int i = 0; i < 6; i++) {
            if(!uniqueProjects.get(i).isEmpty()) {
                for (Project project : uniqueProjects.get(i)) {
                    sb.append(startupLevelsTitle.get(i));
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
            }
        }

        File csvFile = new File("projects-" + System.currentTimeMillis() + ".csv");

        try (BufferedWriter writer =
                 new BufferedWriter(new FileWriter(csvFile))) {

            writer.write(sb.toString());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "CSV file generated at " + csvFile.getAbsolutePath();
    }

    private static final Map<Integer,String> startupLevelsTitle = new HashMap<>();

    static {
        startupLevelsTitle.put(0,"Services and APIs");
        startupLevelsTitle.put(1,"Utils");
        startupLevelsTitle.put(2,"Commons");
        startupLevelsTitle.put(3,"Hooks");
        startupLevelsTitle.put(4,"Fragments");
        startupLevelsTitle.put(5,"Others");
    }

}