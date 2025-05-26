package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.detector.*;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseStartupProjectDependencyExporter implements ProjectDependencyExporter<String> {

    protected abstract void appendByCategory(
        Map<String, StringBuilder> categoryMap, String category, String content);

    protected abstract String extensionFile();

    protected abstract String shortDescription();

    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        StringBuilder sb = new StringBuilder();

        sb.append("Startup Game Plan with the proposed levels").append("\n\n");

        Map<String, StringBuilder> categoryMap = new LinkedHashMap<>();

        categoryMap.put("Services and APIs", new StringBuilder());
        categoryMap.put("Commons and Utils", new StringBuilder());
        categoryMap.put("Hooks", new StringBuilder());
        categoryMap.put("Fragment-Hosts", new StringBuilder());
        categoryMap.put("Others (Including themes and web modules)", new StringBuilder());

        for (Project project : projectsDependencyGraph.getLeaves()) {
            String projectCategory = project.getName().split("=")[1];
            String projectName = project.getName().split("=")[0];

            if (projectCategory.equals(APIModuleProjectDetector.class.getSimpleName()) ||
                    projectCategory.equals(ServiceModuleProjectDetector.class.getSimpleName())) {
                appendByCategory(categoryMap, "Services and APIs", projectName);
            }
            else if (projectCategory.equals(CommonPlusUtilModuleDetector.class.getSimpleName())) {
                appendByCategory(categoryMap, "Commons and Utils", projectName);
            }
            else if (projectCategory.equals(HookModuleProjectDetector.class.getSimpleName())) {
                appendByCategory(categoryMap, "Hooks", projectName);
            }
            else if (projectCategory.equals(FragmentHostModuleProjectDetector.class.getSimpleName())) {
                appendByCategory(categoryMap, "Fragment-Hosts", projectName);
            }
            else {
                appendByCategory(
                    categoryMap, "Others (Including themes and web modules)", projectName);
            }
        }

        int counter = 1;

        for (Map.Entry<String, StringBuilder> entry : categoryMap.entrySet()) {
            sb.append(counter).append(".").append(entry.getKey()).append(":\n");
            sb.append(entry.getValue()).append("\n");
            counter++;
        }

        File file = new File(
            "projects-" + System.currentTimeMillis() + extensionFile());

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(sb.toString());
        }
        catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        return String.format("%s %s", shortDescription(), file.getAbsolutePath());
    }

}
