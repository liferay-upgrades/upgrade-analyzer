package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import static com.liferay.upgrades.analyzer.project.dependency.exporter.util.ExporterUtil.visitConsumers;

public class ReverseDependencyTreeProjectDependencyExporter implements ProjectDependencyExporter<String>{

    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        StringBuilder consumersTree = new StringBuilder();

        visitConsumers(0, projectsDependencyGraph.getLeaves(), (level, project) -> {
            consumersTree.append("\t".repeat(level));
            consumersTree.append(project.getProjectInfo().getName());
            consumersTree.append("\n");

        });

        return consumersTree.toString();
    }




}
