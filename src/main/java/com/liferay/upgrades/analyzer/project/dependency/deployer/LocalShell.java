package com.liferay.upgrades.analyzer.project.dependency.deployer;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

public class LocalShell {

    private static final Logger log = Logger.getLogger(LocalShell.class.getName());

    public static void executeCommand(String command) throws IOException {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("/bin/bash");
        commands.add("-c");
        commands.add(command);

        BufferedReader br = null;

        ProcessBuilder p = new ProcessBuilder(commands);
        Process process = p.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        br = new BufferedReader(isr);

        String line;
        System.out.println("Executing modules deploy");

        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    private static void secureClose(Closeable resource) throws IOException {
        if (resource != null) {
            resource.close();
        }
    }

}