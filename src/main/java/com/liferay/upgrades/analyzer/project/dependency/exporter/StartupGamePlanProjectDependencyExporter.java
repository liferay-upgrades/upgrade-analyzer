package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.exporter.util.StartupGamePlanUtil;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class StartupGamePlanProjectDependencyExporter
    implements ProjectDependencyExporter<String> {

    @Override
    public String export(ProjectsDependencyGraph projectsDependencyGraph) {
        List<List<Project>> uniqueProjects =
            StartupGamePlanUtil.uniquify(projectsDependencyGraph);

        return _export(uniqueProjects);
    }

    private String _export(List<List<Project>> uniqueProjects) {
        StringBuilder sb = new StringBuilder();

        sb.append("Startup Gameplay levels ");
        sb.append("\n\n");

        for (int i = 0; i < 6; i++) {
            if (!uniqueProjects.get(i).isEmpty()) {
                sb.append(_STARTUP_LEVELS_TITLE.get(i));
                sb.append(" modules");
                sb.append("\n");

                for (Project project : uniqueProjects.get(i)) {
                    sb.append(project.getName());
                    sb.append("\n");
                }

                sb.append("\n");
            }
        }

        File gamePlanFile = new File(
            "projects-" + System.currentTimeMillis() + ".txt");

        try (BufferedWriter writer =
                 new BufferedWriter(new FileWriter(gamePlanFile))) {

            writer.write(sb.toString());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "Startup Game Plan generated at " + gamePlanFile.getAbsolutePath();
    }

    private static final Map<Integer,String> _STARTUP_LEVELS_TITLE = new HashMap<>();

    static {
        _STARTUP_LEVELS_TITLE.put(0, "Services and APIs");
        _STARTUP_LEVELS_TITLE.put(1, "Utils");
        _STARTUP_LEVELS_TITLE.put(2, "Commons");
        _STARTUP_LEVELS_TITLE.put(3, "Hooks");
        _STARTUP_LEVELS_TITLE.put(4, "Fragments");
        _STARTUP_LEVELS_TITLE.put(5, "Others");
    }

}