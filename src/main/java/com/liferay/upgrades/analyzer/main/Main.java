package com.liferay.upgrades.analyzer.main;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.ParameterException;

import com.liferay.upgrades.analyzer.project.dependency.analyzer.ProjectDependencyAnalyzer;
import com.liferay.upgrades.analyzer.project.dependency.analyzer.factory.ProjectDependencyAnalyzerFactory;
import com.liferay.upgrades.analyzer.project.dependency.exporter.enums.ProjectExporter;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;

import java.util.Map;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        try {
            ExportOptions exportOptions = _resolveOptions(args);

            for (Map.Entry<String, Boolean> entry : exportOptions.exporters().entrySet()) {
                if (entry.getValue()) {
                    ProjectDependencyAnalyzer projectDependencyAnalyzer =
                        ProjectDependencyAnalyzerFactory.getProjectDependencyAnalyzer(entry.getKey());

                    ProjectsDependencyGraph projectsDependencyGraph =
                        projectDependencyAnalyzer.analyze(exportOptions.directory);

                    if (entry.getKey().equals("dot-graph"))
                        ProjectExporter.DOT_GRAPH.export(projectsDependencyGraph);
                    if (entry.getKey().equals("game-plan"))
                        ProjectExporter.GAME_PLAN.export(projectsDependencyGraph);
                    if (entry.getKey().equals("startup-game-plan"))
                        ProjectExporter.STARTUP_GAME_PLAN.export(projectsDependencyGraph);
                }
            }
        }
        catch (Exception exception) {
            if (exception instanceof ParameterException) {
                log.info(_generateOptionsHelp());
            }
            else throw new RuntimeException(exception);
        }
    }

    private static String _generateOptionsHelp() {
        return "The available options are:\n" +
                "\t--dot-graph or -d to export in the DOT graph format\n" +
                "\t--game-plan or -p to export the game plan\n" +
                "\t--startup-game-plan or -stp to export the startup game plan\n" +
                "\t--folder or -f to specify the path for the liferay workspace (Required)\n" +
                "If just the /path/to/workspace is given, the output will be the same as -p -f /path/to/workspace";
    }

    private static ExportOptions _resolveOptions(String[] args) {
        ExportOptions exportOptions = new ExportOptions();

        JCommander jCommander = JCommander.newBuilder()
                .addObject(exportOptions)
                .build();

        jCommander.parse(args);

        int countUnassigns = 0;

        for (ParameterDescription parameterDescription : jCommander.getParameters()) {
            if (!parameterDescription.isAssigned()) {
                countUnassigns++;
            }
        }

        if (countUnassigns == exportOptions.exporters().size()) {
            exportOptions.gamePlan = true;
        }

        return exportOptions;
    }

    private static class ExportOptions {

        public Map<String, Boolean> exporters() {
            return Map.of(
                "dot-graph", dotGraph, "game-plan", gamePlan,
                "startup-game-plan", startupGamePlan
            );
        }

        @Parameter(
            names = {"-d", "--dot-graph"},
            description = "Export in the DOT graph format"
        )
        boolean dotGraph;

        @Parameter(
            names = {"-p", "--game-plan"},
            description = "Export the game plan"
        )
        boolean gamePlan;

        @Parameter(
            names = {"-stp", "--startup-game-plan"},
            description = "Export the startup game plan"
        )
        boolean startupGamePlan;

        @Parameter(
            names = {"-f", "--folder"},
            description = "Specify the path for the liferay workspace",
            required = true
        )
        String directory;

    }

    private static final Logger log = Logger.getLogger(Main.class.getName());

}