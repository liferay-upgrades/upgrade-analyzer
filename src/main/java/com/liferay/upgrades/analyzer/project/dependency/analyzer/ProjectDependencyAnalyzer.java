package com.liferay.upgrades.analyzer.project.dependency.analyzer;

import com.liferay.upgrades.analyzer.project.dependency.collector.ProjectCollector;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ProjectDependencyAnalyzer {
    private final ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder;

    public ProjectDependencyAnalyzer(List<ProjectCollector> projectCollectors) {
         this.projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();
         this.projectCollectors = projectCollectors;
    }

    public ProjectsDependencyGraph analyze(String rootProjectPath) {

        try {
            Files.walkFileTree(Paths.get(rootProjectPath), new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String folderName = dir.getFileName().toString();

                    if (_SKIP_FOLDERS.contains(folderName)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = file.getFileName().toString();

                    for (ProjectCollector projectCollector : projectCollectors) {
                        if (projectCollector.matches(fileName, file)) {
                            projectCollector.collect(file, projectsDependencyGraphBuilder);
                        }
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (NoSuchFileException noSuchFileExceptionSuchFileException) {
            System.out.println(rootProjectPath + " directory is not available");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (ProjectCollector projectCollector : projectCollectors) {
            projectCollector.flush(projectsDependencyGraphBuilder);
        }

        return projectsDependencyGraphBuilder.build();

    }

    private final List<ProjectCollector> projectCollectors;

    private static final Set<String> _SKIP_FOLDERS = new HashSet<>();

    static {
        _SKIP_FOLDERS.add("bin");
        _SKIP_FOLDERS.add("build");
        _SKIP_FOLDERS.add("node_modules");

    }
}
