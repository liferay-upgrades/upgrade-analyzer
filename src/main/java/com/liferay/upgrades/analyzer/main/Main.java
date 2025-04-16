package com.liferay.upgrades.analyzer.main;

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
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        ExportOptions exportOptions = resolveOptions(args);

        if (!exportOptions.gamePlan && !exportOptions.dotGraph && !exportOptions.moduleDeployer  && !exportOptions.startupGamePlan) {
            StringBuilder sb = new StringBuilder();

            sb.append("The available options are:\n");
            sb.append("\t--dot-graph or -d to export in the DOT graph format\n");
            sb.append("\t--game-plan or -p to export the game plan\n");
            sb.append("\t--startup-game-plan or -stp to export the startup game plan\n");
            sb.append("\t--module-deploy or -md to deploy a module and its submodules\n");
            sb.append("\tIn the -md option you need to specify the path to the module you want to deploy, ie. -md /path/to/workspace/modules/lorem-ipsum-module\n");
            sb.append("\t--folder or -f to specify the path for the liferay workspace\n");
            sb.append("If just the /path/to/workspace is given, the output will be the same as -p -f /path/to/workspace");

            System.out.println(sb);

            return;
        }
        if(exportOptions.moduleDeployer){
            LocalShell localShell = new LocalShell();
            ModuleDeployer moduleDeployer = new ModuleDeployer();
            String script = moduleDeployer.scriptFactory(Paths.get(exportOptions.directory + "/build.gradle"));
            try {
                localShell.executeCommand(script);
            } catch (IOException ex) {
                log.severe("Error = " + ex.getMessage());
            }
        } else {
            ProjectDependencyAnalyzer gradleProjectDependencyAnalyzer = new ProjectDependencyAnalyzer(
                    List.of(new GradleProjectDetector(), new MavenProjectDetector(), new ThemeProjectDetector(), new JSPortletProjectDetector()));

            ProjectsDependencyGraph projectsDependencyGraph = gradleProjectDependencyAnalyzer.analyze(exportOptions.directory);

            if  (exportOptions.gamePlan) {
                System.out.println(new GamePlanProjectDependencyExporter().export(projectsDependencyGraph));
                System.out.println(new CsvProjectDependencyExporter().export(projectsDependencyGraph));
            }

            if (exportOptions.dotGraph) {
                System.out.println(new DOTProjectDependencyExporter().export(projectsDependencyGraph));
                System.out.println(new CsvProjectDependencyExporter().export(projectsDependencyGraph));
            }

            if(exportOptions.startupGamePlan){
                List<List<Project>> uniqueProjects = new ProjectStartupUniquifier().uniquify(projectsDependencyGraph);
                System.out.println(new StartupGamePlanProjectDependecyExporter().export(uniqueProjects));
                System.out.println(new StartupCsvProjectDependencyExporter().export(uniqueProjects));
            }


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
            } else if (arg.equals("--module-deploy") || arg.equals("-md")){
                exportOptions.moduleDeployer=true;
                exportOptions.directory = args[i + 1];
                i++;
            } else if (arg.equals("--startup-game-plan") || arg.equals("-stp")){
                exportOptions.startupGamePlan =true;
            }
        }

        return exportOptions;
    }

    private static class ExportOptions {

        boolean dotGraph;

        boolean gamePlan;

        boolean startupGamePlan;

        boolean moduleDeployer;

        String directory;

    }

    private static final Logger log = Logger.getLogger(LocalShell.class.getName());

}