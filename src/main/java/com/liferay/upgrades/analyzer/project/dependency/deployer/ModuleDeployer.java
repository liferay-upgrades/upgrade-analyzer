package com.liferay.upgrades.analyzer.project.dependency.deployer;

import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuleDeployer {

    public String scriptFactory(Path gradleFile){
        Matcher matcher = ROOT_PROJECT_PATTERN.matcher(gradleFile.toString());

        if (matcher.find()) {
            String rootDirectory = matcher.group();

            return _factoryScript(gradleFile, rootDirectory);
        }

        return null;
    }

    private String _factoryScript(Path gradleFile, String rootDirectory){
        Matcher matcher = GRADLE_PROJECT_PATTERN.matcher(
            ProjectDetectorUtil.readFile(gradleFile));

        List<Project> projects = new ArrayList<>();

        while (matcher.find()) {
            projects.add(ProjectDetectorUtil.getProjectKey(matcher.group(1), projectInfos));
        }

        int gradleFileLength = 12;

        String gradleFilePath = gradleFile.toString();

        String directoryPath = gradleFilePath.substring(
            0, gradleFilePath.length() - gradleFileLength);

        if (projects.isEmpty()) {
            return "cd "+ directoryPath + "\nblade gw clean deploy\n";
        }
        else {
            StringBuilder sb = new StringBuilder();

            for (Project project : projects) {
                gradleFile = Paths.get(rootDirectory +
                    project.getKey().replace(":", "/") + "/build.gradle");

                sb.append(
                    _factoryScript(gradleFile, rootDirectory)
                );
            }

            sb.append("cd ").append(directoryPath).append("\nblade gw clean deploy\n");

            return sb.toString();
        }
    }

    private final Map<String, Project> projectInfos = new HashMap<>();

    private static final Pattern ROOT_PROJECT_PATTERN = Pattern.compile(
        ".*(?=/modules/)");

    private static final Pattern GRADLE_PROJECT_PATTERN = Pattern.compile(
        "project.*\\(*[\"'](.*)[\"']\\)");

}
