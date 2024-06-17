package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.util.Set;
import java.util.function.BiConsumer;

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


    private void visitConsumers(int level, Set<Project> projects, BiConsumer<Integer, Project> doVisit) {
        for (Project project : projects) {
            doVisit.accept(level, project);

            visitConsumers(level + 1, project.getConsumers(), doVisit);
        }
    }

}
