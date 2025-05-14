package com.liferay.upgrades.analyzer.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProjectsDependencyGraphBuilderTest {

    @Test
    public void testADependsOnB() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        Project b = new Project("b");

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new Project("a"),
                        Set.of(b))
                .addProject(
                        b,
                        Set.of()).build();

        ArrayList<Project> leaves = new ArrayList<>(projectsDependencyGraph.getLeaves());

        Assertions.assertEquals(1, leaves.size());
        Assertions.assertEquals(b, leaves.get(0));
        Assertions.assertEquals(2, projectsDependencyGraph.getDepth());
    }

    @Test
    public void testWithNoDependencies() {
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

        Assertions.assertEquals(3, projectsDependencyGraph.getLeaves().size());
        Assertions.assertEquals(1, projectsDependencyGraph.getDepth());
    }

    /*
    * A -> B -> C
    * */
    @Test
    public void testADependsOnBDependsOnC() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new Project("a"),
                        Set.of(new Project("b")))
                .addProject(
                        new Project("b"),
                        Set.of(new Project("c"))
                ).build();

        List<Project> leaves = new ArrayList<>(projectsDependencyGraph.getLeaves());

        while (!leaves.isEmpty()) {
            Assertions.assertEquals(1, leaves.size());

            leaves = new ArrayList<>(leaves.get(0).getConsumers());
        }

        Assertions.assertEquals(3, projectsDependencyGraph.getDepth());
    }

    /*
     * A    -> B
     *      -> C
     * */
    @Test
    public void testADependsOnBandC() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new Project("a"),
                        Set.of(new Project("b"), new Project("c")))
                .addProject(
                        new Project("b"),
                        Set.of())
                .addProject(
                        new Project("c"),
                        Set.of()
                ).build();

        Set<Project> leaves = projectsDependencyGraph.getLeaves();

        Assertions.assertEquals(2, leaves.size());

        for (Project leaf : leaves) {
            Assertions.assertEquals(0, leaf.getDependencies().size());
            Assertions.assertEquals(1, leaf.getConsumers().size());
            Assertions.assertTrue(leaf.getConsumers().contains(new Project("a")));
        }

        Assertions.assertEquals(2, projectsDependencyGraph.getDepth());
    }

    @Test
    public void testComplexDependencies() {
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
                        new Project("employee-portal-language"),
                        Set.of())
                .addProject(
                        new Project("to-do-portlet"),
                        Set.of(new Project("webservice-core"))
                ).build();

        Assertions.assertEquals(3, projectsDependencyGraph.getDepth());
    }

}
