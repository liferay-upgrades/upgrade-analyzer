package com.liferay.upgrades.analyzer.main;

import com.liferay.upgrades.analyzer.project.dependency.analyzer.GradleProjectDependencyAnalyzer;
import com.liferay.upgrades.analyzer.project.dependency.exporter.DOTProjectDependencyExporter;
import com.liferay.upgrades.analyzer.project.dependency.exporter.GamePlanProjectDependencyExporter;
import com.liferay.upgrades.analyzer.project.dependency.exporter.JSONProjectDependencyExporter;
import com.liferay.upgrades.analyzer.project.dependency.exporter.ReverseDependencyTreeProjectDependencyExporter;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        GradleProjectDependencyAnalyzer gradleProjectDependencyAnalyzer = new GradleProjectDependencyAnalyzer();

        ProjectsDependencyGraph projectsDependencyGraph = gradleProjectDependencyAnalyzer.analyze(args[0]);

        // System.out.println("// DOT Language //");
        // System.out.println(new DOTProjectDependencyExporter().export(projectsDependencyGraph));

        // System.out.println("// JSON //");
        // System.out.println(new JSONProjectDependencyExporter().export(projectsDependencyGraph));

        System.out.println("// Game Plan //");
        System.out.println(new GamePlanProjectDependencyExporter().export(projectsDependencyGraph));

        // System.out.println("// Reverse Dependency Tree //");
        // System.out.println(new ReverseDependencyTreeProjectDependencyExporter().export(projectsDependencyGraph));
    }

    private static String[] resolve() {

        return null;
    }

    private class Option {

    }
}