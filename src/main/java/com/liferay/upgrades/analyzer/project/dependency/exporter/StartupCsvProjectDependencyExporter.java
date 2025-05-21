package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.detector.APIModuleProjectDetector;
import com.liferay.upgrades.analyzer.project.dependency.detector.FragmentHostModuleProjectDetector;
import com.liferay.upgrades.analyzer.project.dependency.detector.ServiceModuleProjectDetector;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class StartupCsvProjectDependencyExporter implements ProjectDependencyExporter<String> {

    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        StringBuilder sb = new StringBuilder();

        sb.append("Startup Game Plan with the proposed levels").append("\n\n");

        Map<String, StringBuilder> categoryMap = new LinkedHashMap<>();

        categoryMap.put("Service and API", new StringBuilder());
        categoryMap.put("Fragment Host", new StringBuilder());
        categoryMap.put("Others", new StringBuilder());

        for (Project project : projectsDependencyGraph.getLeaves()) {
            String projectCategory = project.getName().split("=")[1];
            String projectName = project.getName().split("=")[0];

            if (projectCategory.equals(APIModuleProjectDetector.class.getSimpleName()) ||
                    projectCategory.equals(ServiceModuleProjectDetector.class.getSimpleName())) {

                _appendByCategory(categoryMap, "Service and API", projectName);
            }
            else if (projectCategory.equals(FragmentHostModuleProjectDetector.class.getSimpleName())) {
                _appendByCategory(categoryMap, "Fragment Host", projectName);
            }
            else {
                _appendByCategory(categoryMap, "Others", projectName);
            }
        }

        for (Map.Entry<String, StringBuilder> entry : categoryMap.entrySet()) {
            sb.append(entry.getKey()).append(":\n\n");
            sb.append(entry.getValue()).append("\n");
        }

        File csvFile = new File("projects-" + System.currentTimeMillis() + ".csv");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile))) {
            bufferedWriter.write(sb.toString());
        }
        catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        return "CSV file generated at " + csvFile.getAbsolutePath();
    }

    public static void _appendByCategory(
        Map<String, StringBuilder> categoryMap, String category, String content) {

        categoryMap.computeIfAbsent(
            category, k -> new StringBuilder()).append(content).append("\n");
    }

}