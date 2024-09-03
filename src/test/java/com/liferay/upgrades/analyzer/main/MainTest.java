package com.liferay.upgrades.analyzer.main;

import java.io.IOException;

public class MainTest {

    public static void main(String[] args) throws IOException {
        Main.main(new String[] {"-f", "path/to/workspace", "-d"});
    }
}
