import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ServerConfiguration {
    // [SERVICE]

    /**
     * A string representing the path the root directory (i.e where everything will be stored/read from
     * <p>
     * NOTE : The string should end with a forward slash '/'. If the given string does not end properly, a '/' will be appended to the end upon creation
     */
    private Path rootDirectory;
    private Path storeDirectory;
    private int defaultLeaseTime; //in seconds
    private int IDlength;

    private String charSet;

    //[NETWORK]
    private int serverPort;

    public ServerConfiguration(Path rootDirectory, Path storeDir, int defaultLeaseTime, int IDlength, int port, String charSet) {
        this.setRootDirectory(rootDirectory);
        this.setStoreDirectory(storeDir);
        this.setDefaultLeaseTime(defaultLeaseTime);
        this.setIDlength(IDlength);
        this.setServerPort(port);
        this.setCharSet(charSet);
    }

    public static ServerConfiguration loadFromFile(Path config_file) throws FileNotFoundException {
        Properties prop = new Properties();

        InputStream is = new FileInputStream(config_file.toFile());

        try {
            prop.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path current = Paths.get(System.getProperty("user.dir"));

        Path root = null;
        String rootStr = (String)prop.get("ROOT_DIR");
        if(rootStr.isEmpty())
            root = current;
        else
            root = current.resolve(rootStr);

        Path store = null;
        String storeStr = (String)prop.get("STORE_DIR");
        if(storeStr == null  || storeStr.isEmpty())
            store = current;
        else
            store = root.resolve(storeStr);

        int lease = Integer.parseInt((String)prop.get("DEFAULT_LEASE_TIME"));
        int id = Integer.parseInt((String)prop.get("ID_LENGTH"));
        int port = Integer.parseInt((String)prop.get("SERVER_PORT"));
        String charset = (String) prop.get("CHARSET");

        ServerConfiguration config = new ServerConfiguration(root, store, lease, id, port, charset);

        return config;
    }

    public Path rootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(Path p){
        this.rootDirectory = p;
    }

    public void setStoreDirectory(Path p){
        this.storeDirectory = p;
    }

    public Path storeDirectory() {
        return storeDirectory;
    }

    public int defaultLeaseTime() {
        return defaultLeaseTime;
    }

    public void setDefaultLeaseTime(int defaultLeaseTime) {
        if(defaultLeaseTime < 0)
            throw new IllegalArgumentException("lease time must be positive");

        this.defaultLeaseTime = defaultLeaseTime;
    }

    public int IDLength() {
        return IDlength;
    }

    public void setIDlength(int iDlength) {
        IDlength = iDlength;
    }

    public String charSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public int serverPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        if(serverPort < 1 || serverPort > 65535)
            throw new IllegalArgumentException("server port must be between 1 and 65535");
        this.serverPort = serverPort;
    }
}
