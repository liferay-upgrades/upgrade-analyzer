package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIModuleProjectDetector extends BaseStartupProjectDetector {

    @Override
    protected String getClassName() {
        return APIModuleProjectDetector.class.getSimpleName();
    }

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.contains("bnd.bnd") && _validateAPIModule(file);
    }

    private boolean _validateAPIModule(Path file) {
        String content = ProjectDetectorUtil.readFile(file);

        Pattern pattern = Pattern.compile("-includeresource:\\s*META-INF/service.xml");

        Matcher matcher = pattern.matcher(content);

       return matcher.find();
    }

}
