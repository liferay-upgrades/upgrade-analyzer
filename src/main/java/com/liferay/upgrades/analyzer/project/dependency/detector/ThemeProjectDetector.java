package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Collections;

public class ThemeProjectDetector implements ProjectDetector {
    @Override
    public boolean matches(String fileName, Path file) {
        if (fileName.equals("liferay-look-and-feel.xml")) {
           return true;
        }

        return false;
    }

    @Override
    public void process(Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {
        projectsDependencyGraphBuilder.addProject(getThemeProjectKey(file), Collections.emptySet());
    }

    public Project getThemeProjectKey(Path file) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            try (FileInputStream fileInputStream = new FileInputStream(file.toFile())) {
                Document xmlDocument = builder.parse(fileInputStream);
                XPath xPath = XPathFactory.newInstance().newXPath();

                Node themeNode = (Node) xPath.compile(
                        "/look-and-feel/theme"
                ).evaluate(xmlDocument, XPathConstants.NODE);

                NamedNodeMap attributes = themeNode.getAttributes();

                Node idNode = attributes.getNamedItem("id");

                String themeId = idNode.getNodeValue();

                themeId = (themeId.endsWith("-theme") ? themeId : themeId + "-theme");

                String themeRootPath = getThemeRootPath(file, themeId);

                String themeRootFolderName = themeRootPath.substring(themeRootPath.lastIndexOf("/") + 1);

                if (themeRootFolderName.equalsIgnoreCase(themeId)) {
                    return  new Project(themeRootFolderName, themeRootPath);
                }

                return  new Project(themeRootFolderName + "[" + themeId + "]", themeRootPath);
            }
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private String getThemeRootPath(Path file, String themeId) {
        Path parent = file.getParent();


        while (parent != null && parent != parent.getRoot()) {
            if (parent.getFileName().toString().toLowerCase().contains(themeId.toLowerCase())) {
                return parent.toAbsolutePath().toString();
            }

            parent = parent.getParent();
        }

        parent = file.getParent();

        while (parent != null && parent != parent.getRoot()) {
            if (parent.getFileName().toString().toLowerCase().contains("src")) {
                return parent.getParent().toAbsolutePath().toString();
            }

            parent = parent.getParent();
        }

        throw new RuntimeException(
                "Theme root path not found for themeId = " + themeId + ", path = " + file.toAbsolutePath());
    }

}
