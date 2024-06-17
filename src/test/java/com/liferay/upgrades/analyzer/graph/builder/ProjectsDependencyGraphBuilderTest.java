package com.liferay.upgrades.analyzer.graph.builder;

import com.liferay.upgrades.analyzer.project.dependency.exporter.DOTProjectDependencyExporter;
import com.liferay.upgrades.analyzer.project.dependency.exporter.JSONProjectDependencyExporter;
import com.liferay.upgrades.analyzer.project.dependency.exporter.ProjectDependencyExporter;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraph;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.model.ProjectDetails;
import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
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

        print(projectsDependencyGraph);

        Assertions.assertTrue(true);

    }

    private void print(ProjectsDependencyGraph projectsDependencyGraph) {
        for (ProjectDependencyExporter<?> projectDependencyExporter : _projectDependencyExporters) {
            projectDependencyExporter.export(projectsDependencyGraph);
        }
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

        print(projectsDependencyGraph);

        Assertions.assertTrue(true);

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
                        Set.of(new ProjectDetails("c"))).build();

        print(projectsDependencyGraph);

        Assertions.assertTrue(true);
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
                        Set.of()).build();

//        List<Project> leaves = new ArrayList<>(projectsDependencyBuilder.getLeaves());
//
//        Assertions.assertEquals(2, leaves.size());
//
//        Project leaf = leaves.get(0);
//
//        Project projectA = leaf.getConsumers();
//
//        Assertions.assertEquals(2, projectA.getSubProjects().size());
//
//        for (Project subProject : projectA.getSubProjects()) {
//            Assertions.assertEquals(0, subProject.getSubProjects().size());
//        }

        print(projectsDependencyGraph);
    }

    @Test
    public void testADependsOnBandCD() {
        ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder = new ProjectsDependencyGraphBuilder();

        ProjectsDependencyGraph projectsDependencyGraph = projectsDependencyGraphBuilder
                .addProject(
                        new ProjectDetails("search-portlet"),
                        Set.of(new ProjectDetails("tudelft-employee-api"), new ProjectDetails("tudelft-employee-web")))
                .addProject(
                        new ProjectDetails("tudelft-employee-api"),
                        Set.of())
                .addProject(
                        new ProjectDetails("tudelft-employee-web"),
                        Set.of(new ProjectDetails("webservice-core")))
                .addProject(
                        new ProjectDetails("leave-portlet"),
                        Set.of(new ProjectDetails("webservice-core")))
                .addProject(
                        new ProjectDetails("favorite-assets"),
                        Set.of(new ProjectDetails("tudelft-employee-api")))
                .addProject(
                        new ProjectDetails("employee-portal-language"),
                        Set.of())
                .addProject(
                        new ProjectDetails("to-do-portlet"),
                        Set.of(new ProjectDetails("webservice-core"))).build();

//        List<Project> leaves = new ArrayList<>(projectsDependencyBuilder.getLeaves());
//
//        Assertions.assertEquals(2, leaves.size());
//
//        Project leaf = leaves.get(0);
//
//        Project projectA = leaf.getParent();
//
//        Assertions.assertEquals(2, projectA.getSubProjects().size());
//
//        for (Project subProject : projectA.getSubProjects()) {
//            Assertions.assertEquals(0, subProject.getSubProjects().size());
//        }

        print(projectsDependencyGraph);
    }

    private List<ProjectDependencyExporter<?>> _projectDependencyExporters = List.of(new JSONProjectDependencyExporter(), new DOTProjectDependencyExporter());
}
