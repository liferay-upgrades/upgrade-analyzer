package com.liferay.upgrades.analyzer.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.model.ProjectDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProjectsDependencyGraphBuilderTest {

    @Test
    public void testADependsOnB() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectDetails b = new ProjectDetails("b");

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new ProjectDetails("a"),
                        Set.of(b))
                .addProject(
                        b,
                        Set.of()).build();

        ArrayList<Project> leaves = new ArrayList<>(projectsDependencyGraph.getLeaves());

        Assertions.assertEquals(1, leaves.size());
        Assertions.assertEquals(b, leaves.get(0).getProjectInfo());

    }

    @Test
    public void testWithNoDependencies() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new ProjectDetails("a"),
                        Set.of())
                .addProject(
                        new ProjectDetails("b"),
                        Set.of())
                .addProject(
                        new ProjectDetails("c"),
                        Set.of()).build();

        Assertions.assertEquals(3, projectsDependencyGraph.getLeaves().size());
    }

    /*
    * A -> B -> C
    * */
    @Test
    public void testADependsOnBDependsOnC() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new ProjectDetails("a"),
                        Set.of(new ProjectDetails("b")))
                .addProject(
                        new ProjectDetails("b"),
                        Set.of(new ProjectDetails("c"))
                ).build();

        List<Project> leaves = new ArrayList<>(projectsDependencyGraph.getLeaves());

        int depth = 0;

        while (leaves != null && !leaves.isEmpty()) {
            Assertions.assertEquals(1, leaves.size());

            leaves = new ArrayList<>(leaves.get(0).getConsumers());

            depth++;
        }

        Assertions.assertEquals(3, depth);

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
                        new ProjectDetails("a"),
                        Set.of(new ProjectDetails("b"), new ProjectDetails("c")))
                .addProject(
                        new ProjectDetails("b"),
                        Set.of())
                .addProject(
                        new ProjectDetails("c"),
                        Set.of()
                ).build();

        Set<Project> leaves = projectsDependencyGraph.getLeaves();

        Assertions.assertEquals(2, leaves.size());

        for (Project leaf : leaves) {
            Assertions.assertEquals(0, leaf.getDependencies().size());
            Assertions.assertEquals(1, leaf.getConsumers().size());
            //Assertions.assertEquals(true, leaf.getConsumers().contains(new Project(new ProjectDetails("a"))));
        }

    }

    @Test
    public void testComplexDependencies() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new ProjectDetails("search-portlet"),
                        Set.of(new ProjectDetails("employee-api"), new ProjectDetails("employee-web")))
                .addProject(
                        new ProjectDetails("employee-api"),
                        Set.of())
                .addProject(
                        new ProjectDetails("employee-web"),
                        Set.of(new ProjectDetails("webservice-core")))
                .addProject(
                        new ProjectDetails("leave-portlet"),
                        Set.of(new ProjectDetails("webservice-core")))
                .addProject(
                        new ProjectDetails("favorite-assets"),
                        Set.of(new ProjectDetails("employee-api")))
                .addProject(
                        new ProjectDetails("employee-portal-language"),
                        Set.of())
                .addProject(
                        new ProjectDetails("to-do-portlet"),
                        Set.of(new ProjectDetails("webservice-core"))
                ).build();


                 projectsDependencyGraph.getLeaves();
    }

}
