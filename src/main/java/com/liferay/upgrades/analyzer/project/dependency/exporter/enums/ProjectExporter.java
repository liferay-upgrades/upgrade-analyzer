package com.liferay.upgrades.analyzer.project.dependency.exporter.enums;

import com.liferay.upgrades.analyzer.project.dependency.exporter.*;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;

import java.util.logging.Logger;

public enum ProjectExporter {

    DOT_GRAPH {
        @Override
        public void export(ProjectsDependencyGraph projectsDependencyGraph) {
            _logger.info(
                new DOTProjectDependencyExporter().export(projectsDependencyGraph));
            _logger.info(
                new GamePlanProjectDependencyExporter().export(projectsDependencyGraph));
        }
    },
    GAME_PLAN {
        @Override
        public void export(ProjectsDependencyGraph projectsDependencyGraph) {
            _logger.info(
                new CsvProjectDependencyExporter().export(projectsDependencyGraph));
            _logger.info(
                new GamePlanProjectDependencyExporter().export(projectsDependencyGraph));
        }
    },
    STARTUP_GAME_PLAN {
        @Override
        public void export(ProjectsDependencyGraph projectsDependencyGraph) {
            _logger.info(
                new StartupCsvProjectDependencyExporter().export(projectsDependencyGraph));
            _logger.info(
                new StartupGamePlanProjectDependencyExporter().export(projectsDependencyGraph));
        }
    };

    public abstract void export(ProjectsDependencyGraph projectsDependencyGraph);

    private static final Logger _logger = Logger.getLogger(
        ProjectExporter.class.getName());

}
