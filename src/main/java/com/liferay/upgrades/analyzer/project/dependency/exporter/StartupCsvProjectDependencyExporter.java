package com.liferay.upgrades.analyzer.project.dependency.exporter;

import java.util.Map;

public class StartupCsvProjectDependencyExporter
    extends BaseStartupProjectDependencyExporter {

    @Override
    protected void appendByCategory(
        Map<String, StringBuilder> categoryMap, String category, String content) {

        categoryMap.computeIfAbsent(
            category, k -> new StringBuilder()).append(content).append(",");
    }

    @Override
    protected String extensionFile() {
        return ".csv";
    }

    @Override
    protected String shortDescription() {
        return "CSV file generated at ";
    }

}