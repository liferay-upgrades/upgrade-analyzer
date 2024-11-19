package com.liferay.upgrades.analyzer.project.dependency.analyzer;

import com.liferay.upgrades.analyzer.project.dependency.detector.ProjectDetector;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ProjectDependencyAnalyzer {
    private final ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder;

    public ProjectDependencyAnalyzer(List<ProjectDetector> projectDetectors) {
         this.projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();
         this.projectDetectors = projectDetectors;
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

                    for (ProjectDetector projectDetector : projectDetectors) {
                        if (projectDetector.matches(fileName, file)) {
                            projectDetector.process(file, projectsDependencyGraphBuilder);
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

        for (ProjectDetector projectDetector : projectDetectors) {
            projectDetector.postProcess(projectsDependencyGraphBuilder);
        }

        return projectsDependencyGraphBuilder.build();

    }

    private final List<ProjectDetector> projectDetectors;

    private static final Set<String> _SKIP_FOLDERS = new HashSet<>();

    static {
        _SKIP_FOLDERS.add("bin");
        _SKIP_FOLDERS.add("build");
        _SKIP_FOLDERS.add("node_modules");
    }
}
