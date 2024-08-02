package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
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

        long time = System.currentTimeMillis();

        File outputFile = new File("graph-" + time + ".dot");

        String result = null;

         try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))){
            writer.write(sb.toString());

             File svgFile = new File("graph-" + time + ".svg");

             ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tsv" +
                     "g", outputFile.getName(), "-o",  svgFile.getName());

            Process process = processBuilder.start();

            int statusCode = process.waitFor();

            if (statusCode == 0 && svgFile.exists()) {
                result =  "Svg file generated at " + svgFile.getAbsolutePath();
            }
            else {
                result =  "run \"dot -Tsvg "+ outputFile.getName() + " -o output.svg\" to generate the svg file";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return result;
    }




    private void addRelationships(Set<String> lines, Project leaf) {
        if (leaf == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\"");
        sb.append(leaf.getName());
        sb.append("\"");

        lines.add(sb.toString());

        for (Project consumer : leaf.getConsumers()) {
            StringBuilder builder = new StringBuilder();

            builder.append("\"");
            builder.append(consumer.getName());
            builder.append("\"");
            builder.append(" -> ");
            builder.append("\"");
            builder.append(leaf.getName());
            builder.append("\"");

            lines.add(builder.toString());

            addRelationships(lines, consumer);
        }
    }
}