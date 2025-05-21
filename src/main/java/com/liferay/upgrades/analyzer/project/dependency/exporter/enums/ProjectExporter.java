package com.liferay.upgrades.analyzer.project.dependency.exporter.enums;

import com.liferay.upgrades.analyzer.project.dependency.exporter.*;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;

public enum ProjectExporter {

    DOT_GRAPH {
        @Override
        public void export(ProjectsDependencyGraph projectsDependencyGraph) {
            System.out.println(
                new DOTProjectDependencyExporter().export(projectsDependencyGraph));
            System.out.println(
                new GamePlanProjectDependencyExporter().export(projectsDependencyGraph));
        }
    },
    GAME_PLAN {
        @Override
        public void export(ProjectsDependencyGraph projectsDependencyGraph) {
            System.out.println(
                new CsvProjectDependencyExporter().export(projectsDependencyGraph));
            System.out.println(
                new GamePlanProjectDependencyExporter().export(projectsDependencyGraph));
        }
    },
    STARTUP_GAME_PLAN {
        @Override
        public void export(ProjectsDependencyGraph projectsDependencyGraph) {
            System.out.println(new StartupCsvProjectDependencyExporter().export(projectsDependencyGraph));
            System.out.println(new StartupGamePlanProjectDependecyExporter());
        }
    };

    public abstract void export(ProjectsDependencyGraph projectsDependencyGraph);

}
