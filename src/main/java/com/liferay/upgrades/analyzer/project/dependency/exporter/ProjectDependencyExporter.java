package com.liferay.upgrades.analyzer.project.dependency.exporter;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;

public interface ProjectDependencyExporter<T> {

    public T export(ProjectsDependencyGraph projectsDependencyGraph);

}
