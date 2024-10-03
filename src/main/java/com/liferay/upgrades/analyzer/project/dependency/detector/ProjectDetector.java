package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;

import java.nio.file.Path;

public interface ProjectDetector {

    public boolean matches(String fileName, Path file);

    public void detect(Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder);

    public default void finalize(ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {}
}
