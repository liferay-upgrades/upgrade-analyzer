package com.liferay.upgrades.analyzer.project.dependency.analyzer.factory;

import com.liferay.upgrades.analyzer.project.dependency.analyzer.ProjectDependencyAnalyzer;
import com.liferay.upgrades.analyzer.project.dependency.detector.*;

import java.util.List;

public class ProjectDependencyAnalyzerFactory {

    public static ProjectDependencyAnalyzer getProjectDependencyAnalyzer(
        String exportType) {

        if (exportType.equals(_GAME_PLAN_EXPORT_TYPE) || exportType.equals(_DOT_GRAPH_EXPORT_TYPE)) {
            return new ProjectDependencyAnalyzer(
                List.of(
                    new GradleProjectDetector(), new MavenProjectDetector(),
                    new JSPortletProjectDetector(), new ThemeProjectDetector()
                )
            );
        }
        else if (exportType.equals(_STARTUP_GAME_PLAN_EXPORT_TYPE)) {
            return new ProjectDependencyAnalyzer(
                List.of(
                    new APIModuleProjectDetector(), new CommonPlusUtilModuleDetector(),
                    new FragmentHostModuleProjectDetector(), new HookModuleProjectDetector(),
                    new ServiceModuleProjectDetector(), new OtherModuleProjectDetector()
                )
            );
        }
        else throw new RuntimeException(
            "Unsupported export type: " + exportType);
    }

    private static final String _DOT_GRAPH_EXPORT_TYPE = "dot-graph";
    private static final String _GAME_PLAN_EXPORT_TYPE = "game-plan";
    private static final String _STARTUP_GAME_PLAN_EXPORT_TYPE = "startup-game-plan";

}
