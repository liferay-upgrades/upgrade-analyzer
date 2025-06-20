package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;

public class ServiceModuleProjectDetector extends BaseStartupProjectDetector {

    @Override
    protected String getClassName() {
        return ServiceModuleProjectDetector.class.getSimpleName();
    }

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.equals("bnd.bnd") && _validateServiceXML(file);
    }

    private boolean _validateServiceXML(Path file) {
        String serviceXMLContent = ProjectDetectorUtil.readFile(file);

        return !serviceXMLContent.isBlank() &&
                serviceXMLContent.contains("Liferay-Service: true");
    }

}
