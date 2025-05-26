package com.liferay.upgrades.analyzer.project.dependency.analyzer;

import com.liferay.upgrades.analyzer.project.dependency.detector.ProjectDetector;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProjectDependencyAnalyzer {

    public ProjectDependencyAnalyzer(List<ProjectDetector> projectDetectors) {
         this._projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();
         this._projectDetectors = projectDetectors;
    }

    public ProjectsDependencyGraph analyze(String rootProjectPath) {

        try {
            Files.walkFileTree(Paths.get(rootProjectPath), new SimpleFileVisitor<>() {

                @Nonnull
                @Override
                public FileVisitResult preVisitDirectory(
                    @Nonnull Path dir, @Nonnull BasicFileAttributes attrs) throws IOException {

                    String folderName = dir.getFileName().toString();

                    if (_SKIP_FOLDERS.contains(folderName)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Nonnull
                @Override
                public FileVisitResult visitFile(
                    @Nonnull Path file, @Nonnull BasicFileAttributes attrs) throws IOException {

                    String fileName = file.getFileName().toString();

                    for (ProjectDetector projectDetector : _projectDetectors) {
                        if (projectDetector.matches(fileName, file)) {
                            projectDetector.process(file, _projectsDependencyGraphBuilder);
                        }
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (NoSuchFileException noSuchFileExceptionSuchFileException) {
            _logger.error("{} directory is not available", rootProjectPath, noSuchFileExceptionSuchFileException);
        }
        catch (IOException e) {
            _logger.error(e, e);

            throw new RuntimeException(e);
        }

        for (ProjectDetector projectDetector : _projectDetectors) {
            projectDetector.postProcess(_projectsDependencyGraphBuilder);
        }

        return _projectsDependencyGraphBuilder.build();

    }

    private final ProjectsDependencyGraphBuilder _projectsDependencyGraphBuilder;

    private final List<ProjectDetector> _projectDetectors;

    private static final Logger _logger = LogManager.getLogger(ProjectDependencyAnalyzer.class);

    private static final Set<String> _SKIP_FOLDERS = new HashSet<>();

    static {
        _SKIP_FOLDERS.add(".git");
        _SKIP_FOLDERS.add("bin");
        _SKIP_FOLDERS.add("build");
        _SKIP_FOLDERS.add("dist");
        _SKIP_FOLDERS.add("node_modules");
        _SKIP_FOLDERS.add("node_module_cache");
        _SKIP_FOLDERS.add("target");
    }

}
