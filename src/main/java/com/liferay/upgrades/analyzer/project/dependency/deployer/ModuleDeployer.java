package com.liferay.upgrades.analyzer.project.dependency.deployer;

import com.liferay.upgrades.analyzer.project.dependency.model.ProjectKey;
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

            return recursiveScriptFactory(gradleFile, rootDirectory);
        }

        return null;
    }

    public String recursiveScriptFactory(Path gradleFile, String rootDirectory){

        Matcher matcher = GRADLE_PROJECT_PATTERN.matcher(
                ProjectDetectorUtil.readFile(gradleFile));
        List<ProjectKey> projectKeys = new ArrayList<ProjectKey>();
        while(matcher.find()){
            projectKeys.add(ProjectDetectorUtil.getProjectKey(matcher.group(1), projectInfos));
        }
        String directoryPath = gradleFile.toString().substring(0,gradleFile.toString().length()-12);
        if(projectKeys.isEmpty()) return "cd "+ directoryPath + "\nblade gw clean deploy\n";
        else {
            StringBuilder retorno = new StringBuilder();
            for (ProjectKey projectKey : projectKeys) {
                retorno.append(
                        recursiveScriptFactory(
                                Paths.get(rootDirectory + projectKey.getKey().replace(":", "/") + "/build.gradle"),
                                rootDirectory
                        )
                );
            }
            retorno.append("cd ").append(directoryPath).append("\nblade gw clean deploy\n");
            return retorno.toString();
        }

    }

    private final Map<String, ProjectKey> projectInfos = new HashMap<>();

    private static final Pattern ROOT_PROJECT_PATTERN = Pattern.compile(
        ".*(?=/modules/)");

    private static final Pattern GRADLE_PROJECT_PATTERN = Pattern.compile(
        "project.*\\(*[\"'](.*)[\"']\\)");

}
