package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Albert Gomes Cabral
 */
public class ExporterModuleDetector extends BaseStartupProjectDetector {

    @Override
    protected String getClassName() {
        return ExporterModuleDetector.class.getSimpleName();
    }

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.equals("bnd.bnd") && _validateExporters(file);
    }

    private boolean _validateExporters(Path file) {
        String content = ProjectDetectorUtil.readFile(file);

        if (content.contains("META-INF/service.xml") || content.contains("Liferay-Service")) {
            return false;
        }

        Matcher matcher = _EXPORT_PACKAGE_PATTERN.matcher(content);

        if (matcher.find()) {
            String group = matcher.group(1);

            if (!group.isBlank()) {
                String[] packages = group.split(",");

                return packages.length > 0;
            }
        }

        return false;
    }

    private static final Pattern _EXPORT_PACKAGE_PATTERN = Pattern.compile(
        "Export-Package:\\\\(\\s*\\n(?:\\s+.+\\\\s*\\n)*\\s+.+\\n?)");

}
