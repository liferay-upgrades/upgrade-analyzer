package com.liferay.upgrades.analyzer.project.dependency.collector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;

import java.nio.file.Path;

public interface ProjectCollector {

    public boolean matches(String fileName, Path file);

    public void collect(Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder);

    public default void flush(ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {}
}
