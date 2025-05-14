package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class StartupGamePlanProjectDependecyExporter {

    public String export(List<List<ProjectKey>> uniqueProjects) {
        StringBuilder sb = new StringBuilder();
        sb.append("Startup Gameplay levels ");
        sb.append("\n\n");
        for (int i = 0; i < 6; i++) {
            if(!uniqueProjects.get(i).isEmpty()) {
                sb.append(startupLevelsTitle.get(i));
                sb.append(" modules");
                sb.append("\n");
                for (ProjectKey project : uniqueProjects.get(i)) {
                    sb.append(project.getName());
                    sb.append("\n");
                }
                sb.append("\n");
            }
        }

        long time = System.currentTimeMillis();

        File gamePlanFile = new File("projects-" + time + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(gamePlanFile))) {
            writer.write(sb.toString());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "Startup Game Plan generated at " + gamePlanFile.getAbsolutePath();
    }

    private final Map<Integer,String> startupLevelsTitle = new HashMap<Integer, String>(){{
        put(0,"Services and APIs");
        put(1,"Utils");
        put(2,"Commons");
        put(3,"Hooks");
        put(4,"Fragments");
        put(5,"Others");
    }};
}