package com.liferay.upgrades.analyzer.project.dependency.exporter;

import java.util.Map;

public class StartupGamePlanProjectDependencyExporter extends BaseStartupProjectDependencyExporter {

    @Override
    protected void appendByCategory(
        Map<String, StringBuilder> categoryMap, String category, String content) {

        categoryMap.computeIfAbsent(
            category, k -> new StringBuilder()).append("\t").append(content).append("\n");
    }

    @Override
    protected String extensionFile() {
        return ".txt";
    }

    @Override
    protected String shortDescription() {
        return "Startup Game Plan generated at ";
    }

}