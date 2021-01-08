package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Paths {
    static public void createDirectory(Path path){
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.err.println("Failed to create directory!" + e.getMessage());
        }
    }
}
