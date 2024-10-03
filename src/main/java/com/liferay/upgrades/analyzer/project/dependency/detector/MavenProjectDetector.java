package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MavenProjectDetector implements ProjectDetector {
    @Override
    public boolean matches(String fileName, Path file) {

        if (fileName.equals("pom.xml")
                && Files.exists(Paths.get(file.getParent().toString(), "src"))
                && !Files.exists(Paths.get(file.getParent().toString(), "liferay-theme.json"))) {
           return true;
        }

        return false;
    }

    @Override
    public void detect(Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {
        collect(file);
    }

    @Override
    public void finalize(ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {
        for (Map.Entry<String, Set<String>> module : modules.entrySet()) {
            projectsDependencyGraphBuilder.addProject(
                new ProjectKey(module.getKey(), modulesPath.get(module.getKey())),
                    collectProjectDependencies(module.getValue()));
        }
    }

    private Set<ProjectKey> collectProjectDependencies(Set<String> allDependencies) {
        Set<ProjectKey> dependencies = new HashSet<>();

        for (String dependency : allDependencies) {
            if (modules.containsKey(dependency)) {
                dependencies.add(new ProjectKey(dependency));
            }
        }

        return dependencies;
    }

    private void collect(Path file) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            try (FileInputStream fileInputStream = new FileInputStream(file.toFile())) {
                Document xmlDocument = builder.parse(fileInputStream);
                XPath xPath = XPathFactory.newInstance().newXPath();

                NodeList modulesNodeList = (NodeList) xPath.compile(
                        "/project/modules"
                ).evaluate(xmlDocument, XPathConstants.NODESET);

                if (modulesNodeList.getLength() > 0) {
                    return;
                }

                Set<String> dependencies = new HashSet<>();

                NodeList dependenciesNodeList = (NodeList) xPath.compile(
                        "/project/dependencies/dependency"
                ).evaluate(xmlDocument, XPathConstants.NODESET);

                for (int i = 0; i < dependenciesNodeList.getLength(); i++) {
                    NodeList dependencyChildNodes = dependenciesNodeList.item(i).getChildNodes();

                    for (int j = 0; j < dependencyChildNodes.getLength(); j++) {
                        Node dependencyChildNode = dependencyChildNodes.item(j);

                        if (dependencyChildNode.getNodeName() == "artifactId") {
                            dependencies.add(dependencyChildNode.getTextContent().trim());
                        }
                    }
                }

                Node artifactNode = (Node) xPath.compile(
                        "/project/artifactId"
                ).evaluate(xmlDocument, XPathConstants.NODE);

                modules.put(artifactNode.getTextContent().trim(), dependencies);
                modulesPath.put(artifactNode.getTextContent().trim(), file.getParent().toFile().getAbsolutePath());
            }
        }
        catch (Exception exception) {
            throw new RuntimeException("Error processing " + file.toAbsolutePath(), exception);
        }
    }


    private static final Map<String, String> modulesPath = new HashMap<>();
    private static final Map<String, Set<String>> modules = new HashMap<>();
}
