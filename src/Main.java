import server.Server;
import server.ServerConfiguration;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.out.println("server.Server start");

        Path userDir = Paths.get(System.getProperty("user.dir"));

        ServerConfiguration config = null;

        try {
            config = ServerConfiguration.loadFromFile(userDir.resolve("server.properties"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not load configuration from file. Creating file...");
            createDefaultConfig();
            try {
                config = ServerConfiguration.loadFromFile(userDir.resolve("server.properties"));
            } catch (FileNotFoundException fileNotFoundException) {
                System.err.println("Cannot create file...EXITING");
                System.exit(1);
            }
        }

        if(!validate_config(config)){
            throw new RuntimeException("Config file is not valid");
        }

        System.out.println("======================================");
        System.out.println("Starting server with config : ");
        System.out.printf("[Network]\n\tPort : %d\n", config.serverPort());
        System.out.printf("[server.Server]\n\troot : %s\n", config.rootDirectory());
        System.out.printf("\tstore : %s\n", config.storeDirectory());
        System.out.printf("\tlease time : %d s\n", config.defaultLeaseTime());
        System.out.printf("\tID length: %d\n", config.IDLength());
        System.out.println("======================================");

        try {
            Server s = new Server(config);
            s.start();
            s.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static void createDefaultConfig() {
        try {
            File myObj = new File("server.properties");
            if (myObj.createNewFile()) {
                System.out.println("Default server.properties file created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter("server.properties");
            myWriter.write(" ROOT_DIR=\n"+
                    "STORE_DIR=store\n"+
                    "DEFAULT_LEASE_TIME=60\n"+
                    "ID_LENGTH=5\n"+
                    "SERVER_PORT=8565\n"+
                    "CHARSET=azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN0123456789");
            myWriter.close();
            System.out.println("Successfully wrote default properties.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to server.properties.");
            e.printStackTrace();
        }
    }
    private static boolean validate_config(ServerConfiguration config) {
        return true; //TODO: vraiment vérifier la config
    }
}
