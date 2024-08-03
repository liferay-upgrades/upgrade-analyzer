package com.liferay.upgrades.analyzer.project.dependency.util;

import com.liferay.upgrades.analyzer.project.dependency.model.Project;

import java.util.Set;
import java.util.function.BiConsumer;

public class ExporterUtil {

    public static void visitConsumers(int level, Set<Project> projects, BiConsumer<Integer, Project> doVisit) {
        for (Project project : projects) {
            doVisit.accept(level, project);

            visitConsumers(level + 1, project.getConsumers(), doVisit);
        }
    }
}
