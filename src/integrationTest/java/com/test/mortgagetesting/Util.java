package com.test.mortgagetesting;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {

    public static Path getResourceFilePath(String fileNameAndPath) {
        try {
            return Paths.get(ClassLoader.getSystemResource(fileNameAndPath).toURI());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
