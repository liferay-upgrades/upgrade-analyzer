package com.liferay.upgrades.analyzer.exporter;

import com.liferay.upgrades.analyzer.project.dependency.exporter.GamePlanProjectDependencyExporter;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GamePlanProjectDependencyExporterTest {

    @Test
    public void testExportGamePlanWithNoDependencies() throws IOException {
        final var expectedMessage = "Game Plan generated at ";

        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new Project("a"),
                        Set.of())
                .addProject(
                        new Project("b"),
                        Set.of())
                .addProject(
                        new Project("c"),
                        Set.of()).build();

        final var result = new GamePlanProjectDependencyExporter().export(projectsDependencyGraph);

        Assertions.assertTrue(result.contains("Game Plan generated at "), expectedMessage);

        Pattern pattern = Pattern.compile("Game Plan generated at (.+\\/(projects-\\d+\\.txt))");
        Matcher matcher = pattern.matcher(result);

        if (matcher.find()){
            String fullPathString = matcher.group(1);

            Path generatedGamePlanFilePath = Paths.get(fullPathString);

            List<String> lines = Files.readAllLines(generatedGamePlanFilePath);

            Assertions.assertNotNull(lines, "The lines read must not be null.");
            Assertions.assertTrue(lines.contains("Level 1 count: 3"));
            Files.delete(generatedGamePlanFilePath);
        }
    }

    @Test
    public void testExportGamePlanWithMoreThanOneDependency() throws IOException {
        final var expectedMessage = "Game Plan generated at ";

        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new Project("search-portlet"),
                        Set.of(new Project("employee-api"), new Project("employee-web")))
                .addProject(
                        new Project("employee-api"),
                        Set.of())
                .addProject(
                        new Project("employee-web"),
                        Set.of(new Project("webservice-core")))
                .addProject(
                        new Project("leave-portlet"),
                        Set.of(new Project("webservice-core")))
                .addProject(
                        new Project("favorite-assets"),
                        Set.of(new Project("employee-api")))
                .addProject(
                        new Project("something-portlet"),
                        Set.of(new Project("employee-api")))
                .addProject(
                        new Project("lorem-ipsum-portlet"),
                        Set.of(new Project("employee-api")))
                .addProject(
                        new Project("spectator-portlet"),
                        Set.of(new Project("webservice-core")))
                .addProject(
                        new Project("employee-portal-language"),
                        Set.of())
                .addProject(
                        new Project("to-do-portlet"),
                        Set.of(new Project("webservice-core"))
                ).build();

        ArrayList<Project> leaves = new ArrayList<>(projectsDependencyGraph.getLeaves());

        Assertions.assertEquals(3, leaves.size());
        Assertions.assertEquals(3, projectsDependencyGraph.getDepth());

        final var result = new GamePlanProjectDependencyExporter().export(projectsDependencyGraph);

        Assertions.assertTrue(result.contains("Game Plan generated at "), expectedMessage);

        Pattern pattern = Pattern.compile("Game Plan generated at (.+\\/(projects-\\d+\\.txt))");
        Matcher matcher = pattern.matcher(result);

        if (matcher.find()){
            String fullPathString = matcher.group(1);

            Path generatedGamePlanFilePath = Paths.get(fullPathString);

            List<String> lines = Files.readAllLines(generatedGamePlanFilePath);

            Assertions.assertNotNull(lines, "The lines read must not be null.");
            Assertions.assertTrue(lines.contains("This project contains 11 projects with 3 level(s) of " +
                    "project dependencies."));
            Assertions.assertTrue(lines.contains("Level 2 count: 7"));
            Assertions.assertTrue(lines.contains("\temployee-web 1 (webservice-core)"));
            Files.delete(generatedGamePlanFilePath);
        }
    }
}
