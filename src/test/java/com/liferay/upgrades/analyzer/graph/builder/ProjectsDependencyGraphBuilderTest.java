package com.liferay.upgrades.analyzer.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProjectsDependencyGraphBuilderTest {

    @Test
    public void testADependsOnB() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectKey b = new ProjectKey("b");

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new ProjectKey("a"),
                        Set.of(b))
                .addProject(
                        b,
                        Set.of()).build();

        ArrayList<ProjectKey> leaves = new ArrayList<>(projectsDependencyGraph.getLeaves());

        Assertions.assertEquals(1, leaves.size());
        Assertions.assertEquals(b, leaves.get(0));
        Assertions.assertEquals(2, projectsDependencyGraph.getDepth());
    }

    @Test
    public void testWithNoDependencies() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new ProjectKey("a"),
                        Set.of())
                .addProject(
                        new ProjectKey("b"),
                        Set.of())
                .addProject(
                        new ProjectKey("c"),
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
                        new ProjectKey("a"),
                        Set.of(new ProjectKey("b")))
                .addProject(
                        new ProjectKey("b"),
                        Set.of(new ProjectKey("c"))
                ).build();

        List<ProjectKey> leaves = new ArrayList<>(projectsDependencyGraph.getLeaves());

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
                        new ProjectKey("a"),
                        Set.of(new ProjectKey("b"), new ProjectKey("c")))
                .addProject(
                        new ProjectKey("b"),
                        Set.of())
                .addProject(
                        new ProjectKey("c"),
                        Set.of()
                ).build();

        Set<ProjectKey> leaves = projectsDependencyGraph.getLeaves();

        Assertions.assertEquals(2, leaves.size());

        for (ProjectKey leaf : leaves) {
            Assertions.assertEquals(0, leaf.getDependencies().size());
            Assertions.assertEquals(1, leaf.getConsumers().size());
            Assertions.assertTrue(leaf.getConsumers().contains(new ProjectKey("a")));
        }

        Assertions.assertEquals(2, projectsDependencyGraph.getDepth());
    }

    @Test
    public void testComplexDependencies() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new ProjectKey("search-portlet"),
                        Set.of(new ProjectKey("employee-api"), new ProjectKey("employee-web")))
                .addProject(
                        new ProjectKey("employee-api"),
                        Set.of())
                .addProject(
                        new ProjectKey("employee-web"),
                        Set.of(new ProjectKey("webservice-core")))
                .addProject(
                        new ProjectKey("leave-portlet"),
                        Set.of(new ProjectKey("webservice-core")))
                .addProject(
                        new ProjectKey("favorite-assets"),
                        Set.of(new ProjectKey("employee-api")))
                .addProject(
                        new ProjectKey("employee-portal-language"),
                        Set.of())
                .addProject(
                        new ProjectKey("to-do-portlet"),
                        Set.of(new ProjectKey("webservice-core"))
                ).build();

        Assertions.assertEquals(3, projectsDependencyGraph.getDepth());
    }

}
