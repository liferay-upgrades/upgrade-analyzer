package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.util.Set;
import java.util.TreeSet;

public class DOTProjectDependencyExporter implements ProjectDependencyExporter<String> {
    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph Projects {");

        //for now remove duplications using a Set
        Set<String> lines = new TreeSet<>();

        projectsDependencyGraph.getLeaves().forEach(
                leaf -> addRelationships(lines, leaf));

        lines.forEach(line -> {
            sb.append("\n\t");
            sb.append(line);
        });

        sb.append("\n");
        sb.append("}");

        return sb.toString();
    }

    private void addRelationships(Set<String> lines, Project leaf) {
        if (leaf == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\"");
        sb.append(leaf.getKey());
        sb.append("\"");

        lines.add(sb.toString());

        for (Project consumer : leaf.getConsumers()) {
            StringBuilder builder = new StringBuilder();

            builder.append("\"");
            builder.append(consumer.getKey());
            builder.append("\"");
            builder.append(" -> ");
            builder.append("\"");
            builder.append(leaf.getKey());
            builder.append("\"");

            lines.add(builder.toString());

            addRelationships(lines, consumer);
        }
    }
}