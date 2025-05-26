package com.liferay.upgrades.analyzer.project.dependency.detector;

import com.liferay.upgrades.analyzer.project.dependency.graph.builder.ProjectsDependencyGraphBuilder;
import com.liferay.upgrades.analyzer.project.dependency.model.Project;
import com.liferay.upgrades.analyzer.project.dependency.util.ProjectDetectorUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

public class OtherModuleProjectDetector implements ProjectDetector {

    @Override
    public boolean matches(String fileName, Path file) {
        return fileName.equals("bnd.bnd") && _validateIsNotOtherCategory(fileName, file);
    }

    @Override
    public void process(
        Path file, ProjectsDependencyGraphBuilder projectsDependencyGraphBuilder) {

        Project project = ProjectDetectorUtil.getProjectKey(file);

        String detectorKey = String.format(
            "%s=%s", project.getKey(), OtherModuleProjectDetector.class.getSimpleName());

        project.setKey(detectorKey);
        project.setName(project.getKey());

        projectsDependencyGraphBuilder.addProject(project, Collections.emptySet());
    }

    private boolean _validateIsNotOtherCategory(String fileName, Path file) {
        Reflections reflections = new Reflections(
            "com.liferay.upgrades.analyzer.project.dependency.detector");

        Set<Class<? extends ProjectDetector>> classes =
            reflections.getSubTypesOf(ProjectDetector.class).stream().filter(
                    _STARTUP_DETECTOR_CLASSES::contains
            ).collect(Collectors.toSet());

        boolean other = true;

        for (Class<? extends ProjectDetector> clazz : classes) {
            try {
                Method method = clazz.getMethod("matches", String.class, Path.class);

                Object obj = method.invoke(
                    clazz.getDeclaredConstructor().newInstance(), fileName, file);

                if (obj.equals(Boolean.TRUE)) {
                    other = false;
                    break;
                }
            }
            catch (NoSuchMethodException | IllegalAccessException |
                   InvocationTargetException | InstantiationException exception) {
                throw new RuntimeException(exception);
            }
        }

        return other;
    }

    private static final List<Class<?>> _STARTUP_DETECTOR_CLASSES = List.of(
        APIModuleProjectDetector.class,
        CommonPlusUtilModuleDetector.class,
        FragmentHostModuleProjectDetector.class,
        HookModuleProjectDetector.class,
        ServiceModuleProjectDetector.class
    );

}
