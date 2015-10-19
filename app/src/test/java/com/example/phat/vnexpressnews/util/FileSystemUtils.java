package com.example.phat.vnexpressnews.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * This class provides some helper method for testing class in order to
 * load file system as json file for testing purpose.
 */
public class FileSystemUtils {

    private static final String BASE_PATH = resolveBasePath();

    private static String resolveBasePath() {
        final String path = "./app/src/test/java/com/example/phat/vnexpressnews/resources/";
        final String module = "app";
        final String currentDirectory = "./";
        if (Arrays.asList(new File(currentDirectory).list()).contains(module)) {
            return path;
        }

        return "../" + path; // version for call unit tests from terminal './gradlew test'
    }

    /**
     * Reads json file content and returns as string format.
     */
    public static String loadJsonFile(final String path) {
        final StringBuilder sb = new StringBuilder();
        String strLine;
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(BASE_PATH + path), "UTF-8"));
            while ((strLine = reader.readLine()) != null) {
                sb.append(strLine);
            }
        } catch (final IOException e) {
            // Because this class is just used for testing purpose.
            // We do nothing, tries to keep it as simple as possible
            System.out.println("Message: " +e.getMessage() + "\n Cause: " +e.getCause());
        }
        return sb.toString();
    }

}
