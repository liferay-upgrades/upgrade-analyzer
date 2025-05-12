package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.exporter.util.ExporterUtil;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StartupCsvProjectDependencyExporter {

    private final Map<Integer,String> startupLevelsTitle = new HashMap<Integer, String>(){{
        put(0,"Services and APIs");
        put(1,"Utils");
        put(2,"Commons");
        put(3,"Hooks");
        put(4,"Fragments");
        put(5,"Others");
    }};

    public String export(List<List<Project>> uniqueProjects) {
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