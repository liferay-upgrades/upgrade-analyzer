package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;

public class FragmentHostModuleProjectDetector extends BaseStartupProjectDetector {

    @Override
    protected String getClassName() {
        return FragmentHostModuleProjectDetector.class.getSimpleName();
    }

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.equals("bnd.bnd") && _validateFragmentHostModule(file);
    }

    private boolean _validateFragmentHostModule(Path file) {
        String bndContent = ProjectDetectorUtil.readFile(file);

        return !bndContent.isBlank() && bndContent.contains("Fragment-Host:");
    }

}
