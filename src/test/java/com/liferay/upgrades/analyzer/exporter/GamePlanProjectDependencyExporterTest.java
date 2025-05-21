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
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GamePlanProjectDependencyExporterTest {

    @Test
    public void testExportGamePlan() throws IOException {
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

        Pattern pattern = Pattern.compile("Game Plan generated at (.+\\/(projects-\\d+\\.txt))");
        Matcher matcher = pattern.matcher(result);

        Assertions.assertTrue(result.contains("Game Plan generated at "), expectedMessage);

        if (matcher.find()){
            String fullPathString = matcher.group(1);

            Path generatedGamePlanFilePath = Paths.get(fullPathString);

            List<String> lines = Files.readAllLines(generatedGamePlanFilePath);

            Assertions.assertNotNull(lines, "The lines read must not be null.");
            Assertions.assertTrue(lines.contains("Level 1 count: 3"));
            Files.delete(generatedGamePlanFilePath);
        }
    }
}
