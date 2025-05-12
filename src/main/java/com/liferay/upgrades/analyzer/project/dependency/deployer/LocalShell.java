package com.liferay.upgrades.analyzer.project.dependency.deployer;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public class LocalShell {

    private static final Logger log = Logger.getLogger(LocalShell.class.getName());

    public static void executeCommand(String command) throws IOException {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("/bin/bash");
        commands.add("-c");
        commands.add(command);

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();

        if (Objects.nonNull(inputStream)) {
            System.out.println("Executing modules deploy");

            String content = new String(inputStream.readAllBytes());

            System.out.printf(content);
        }
    }

    private static void secureClose(Closeable resource) throws IOException {
        if (resource != null) {
            resource.close();
        }
    }

}