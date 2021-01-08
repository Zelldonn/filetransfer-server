package utils;

import java.io.*;
import java.net.Socket;

public class NetworkIO implements TransactionTypeHandler {
    private BufferedInputStream bufferedInputStream;

    private BufferedOutputStream bufferedOutputStream;

    private DataInputStream dataInputStream;

    private DataOutputStream dataOutputStream;

    NetworkIO(Socket soc) throws IOException {
        bufferedInputStream = new BufferedInputStream(soc.getInputStream());
        bufferedOutputStream = new BufferedOutputStream(soc.getOutputStream());
        dataInputStream = new DataInputStream(bufferedInputStream);
        dataOutputStream = new DataOutputStream(bufferedOutputStream);
    }

    public void writeAndFlush(byte b) throws IOException {
        bufferedOutputStream.write(b);
        bufferedOutputStream.flush();
    }

    public void readInt() throws IOException {
        dataInputStream.readInt();
    }
    public void readLong() throws IOException {
        dataInputStream.readLong();
    }

    @Override
    public void Handle() {

    }
}
