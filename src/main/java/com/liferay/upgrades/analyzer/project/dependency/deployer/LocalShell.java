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

    public void executeCommand(final String command) throws IOException {

        final ArrayList<String> commands = new ArrayList<String>();
        commands.add("/bin/bash");
        commands.add("-c");
        commands.add(command);

        BufferedReader br = null;

        final ProcessBuilder p = new ProcessBuilder(commands);
        final Process process = p.start();
        final InputStream is = process.getInputStream();
        final InputStreamReader isr = new InputStreamReader(is);
        br = new BufferedReader(isr);

        String line;
        System.out.println("Executing modules deploy");
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    private void secureClose(final Closeable resource) throws IOException {
        if (resource != null) {
            resource.close();
        }
    }

}