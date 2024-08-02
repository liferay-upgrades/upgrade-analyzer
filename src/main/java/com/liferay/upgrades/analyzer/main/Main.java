package com.liferay.upgrades.analyzer.main;

import com.liferay.upgrades.analyzer.project.dependency.analyzer.GradleProjectDependencyAnalyzer;
import com.liferay.upgrades.analyzer.project.dependency.exporter.DOTProjectDependencyExporter;
import com.liferay.upgrades.analyzer.project.dependency.exporter.GamePlanProjectDependencyExporter;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;

public class Main {
    public static void main(String[] args) {
        ExportOptions exportOptions = resolveOptions(args);

        if (!exportOptions.gamePlan && !exportOptions.dotGraph) {
            StringBuilder sb = new StringBuilder();

            sb.append("The available options are:\n");
            sb.append("\t--dot-graph or -d to export in the DOT graph format\n");
            sb.append("\t--game-plan or -p to export the game plan\n");
            sb.append("\t--folder or -f to specify the path for the liferay workspace\n");
            sb.append("If just the /path/to/workspace is given, the output will be the same as -d /path/to/workspace -g");

            System.out.println(sb.toString());

            return;
        }

        GradleProjectDependencyAnalyzer gradleProjectDependencyAnalyzer = new GradleProjectDependencyAnalyzer();

        ProjectsDependencyGraph projectsDependencyGraph = gradleProjectDependencyAnalyzer.analyze(exportOptions.directory);

        if  (exportOptions.gamePlan) {
            System.out.println("// Game Plan //");
            System.out.println(new GamePlanProjectDependencyExporter().export(projectsDependencyGraph));
        }

        if (exportOptions.dotGraph) {
            System.out.println(new DOTProjectDependencyExporter().export(projectsDependencyGraph));
        }
    }


    private static ExportOptions resolveOptions(String[] args) {
        ExportOptions exportOptions = new ExportOptions();

        if (args.length == 1 && !args[0].isBlank()) {
            exportOptions.gamePlan = true;
            exportOptions.directory = args[0];
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("--dot-graph") || arg.equals("-d")) {
                exportOptions.dotGraph = true;
            }
            else if (arg.equals("--game-plan") || arg.equals("-p")) {
                exportOptions.gamePlan = true;
            } else if (arg.equals("--folder") || arg.equals("-f")) {
                exportOptions.directory = args[i + 1];
                i++;
            }
        }

        return exportOptions;
    }

    private static class ExportOptions {

        boolean dotGraph;

        boolean gamePlan;

        String directory;

    }
}