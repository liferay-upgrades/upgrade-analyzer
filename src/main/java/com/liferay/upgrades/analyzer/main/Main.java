package com.liferay.upgrades.analyzer.main;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterDescription;
import com.liferay.upgrades.analyzer.project.dependency.analyzer.ProjectDependencyAnalyzer;
import com.liferay.upgrades.analyzer.project.dependency.analyzer.ProjectStartupUniquifier;
import com.liferay.upgrades.analyzer.project.dependency.deployer.LocalShell;
import com.liferay.upgrades.analyzer.project.dependency.deployer.ModuleDeployer;
import com.liferay.upgrades.analyzer.project.dependency.detector.GradleProjectDetector;
import com.liferay.upgrades.analyzer.project.dependency.detector.JSPortletProjectDetector;
import com.liferay.upgrades.analyzer.project.dependency.detector.MavenProjectDetector;
import com.liferay.upgrades.analyzer.project.dependency.detector.ThemeProjectDetector;
import com.liferay.upgrades.analyzer.project.dependency.exporter.*;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        ExportOptions exportOptions = _resolveOptions(args);

        if (!exportOptions.gamePlan && !exportOptions.dotGraph &&
                !exportOptions.startupGamePlan && !exportOptions.moduleDeployer) {
            StringBuilder sb = new StringBuilder();

            sb.append("The available options are:\n");
            sb.append("\t--dot-graph or -d to export in the DOT graph format\n");
            sb.append("\t--game-plan or -p to export the game plan\n");
            sb.append("\t--startup-game-plan or -stp to export the startup game plan\n");
            sb.append("\t--module-deploy or -md to deploy a module and its submodules\n");
            sb.append("\tIn the -md option you need to specify the path to the module you want to deploy, ");
            sb.append("ie. -md -f /path/to/workspace/modules/lorem-ipsum-module\n");
            sb.append("\t--folder or -f to specify the path for the liferay workspace\n");
            sb.append("If just the /path/to/workspace is given, the output will be the same as -p -f /path/to/workspace");

            System.out.println(sb);

            return;
        }

        if (exportOptions.moduleDeployer) {
            ModuleDeployer moduleDeployer = new ModuleDeployer();

            String script = moduleDeployer.scriptFactory(
                Paths.get(exportOptions.directory + "/build.gradle"));

            try {
                LocalShell.executeCommand(script);
            }
            catch (IOException ex) {
                log.severe("Error = " + ex.getMessage());
            }
        }
        else {
            ProjectDependencyAnalyzer projectDependencyAnalyzer =
                new ProjectDependencyAnalyzer(
                    List.of(
                        new GradleProjectDetector(), new MavenProjectDetector(),
                        new ThemeProjectDetector(), new JSPortletProjectDetector()));

            ProjectsDependencyGraph projectsDependencyGraph =
                projectDependencyAnalyzer.analyze(exportOptions.directory);

            if  (exportOptions.gamePlan) {
                System.out.println(
                    new GamePlanProjectDependencyExporter().export(projectsDependencyGraph));
                System.out.println(
                    new CsvProjectDependencyExporter().export(projectsDependencyGraph));
            }

            if (exportOptions.dotGraph) {
                System.out.println(
                    new DOTProjectDependencyExporter().export(projectsDependencyGraph));
                System.out.println(
                    new CsvProjectDependencyExporter().export(projectsDependencyGraph));
            }

            if (exportOptions.startupGamePlan) {
                List<List<Project>> uniqueProjects =
                    new ProjectStartupUniquifier().uniquify(projectsDependencyGraph);

                System.out.println(
                    new StartupGamePlanProjectDependecyExporter().export(uniqueProjects));
                System.out.println(
                    new StartupCsvProjectDependencyExporter().export(uniqueProjects));
            }
        }

    }

    private static ExportOptions _resolveOptions(String[] args) {
        ExportOptions exportOptions = new ExportOptions();

        JCommander jCommander = JCommander.newBuilder()
                .addObject(exportOptions)
                .build();

        jCommander.parse(args);

        int countUnassigns = 0;

        for (ParameterDescription parameterDescription : jCommander.getParameters()) {
            if (!parameterDescription.isAssigned() && (!parameterDescription.getNames().contains("-md")
                    || !parameterDescription.getNames().contains("--module-deploy"))) {
                countUnassigns++;
            }
        }

        if (countUnassigns == exportOptions.exporters().size()) {
            exportOptions.gamePlan = true;
        }

        return exportOptions;
    }

    private static class ExportOptions {

        @Parameter(names = {"-dot-graph", "-d"})
        boolean dotGraph;

        @Parameter(names = {"--game-plan", "-p"})
        boolean gamePlan;

        @Parameter(names = {"--startup-game-plan", "-stp"})
        boolean startupGamePlan;

        @Parameter(names = {"--module-deploy", "-md"})
        boolean moduleDeployer;

        @Parameter(names = {"--folder", "-f"}, required = true)
        String directory;

        public Map<String, Boolean> exporters() {
            return Map.of(
                    "dot-graph", dotGraph, "game-plan", gamePlan,
                    "startup-game-plan", startupGamePlan
            );
        }

    }

    private static final Logger log = Logger.getLogger(Main.class.getName());

}