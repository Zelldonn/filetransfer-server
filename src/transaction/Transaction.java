package transaction;

import utils.TransactionTypeHandler;

import java.io.*;

public class Transaction implements TransactionTypeHandler {

    protected BufferedInputStream bis;

    protected BufferedOutputStream bos;

    protected DataInputStream dis;

    protected DataOutputStream dos;

    Transaction(BufferedInputStream bis, BufferedOutputStream bos){
        this.bis = bis;

        this.bos = bos;

        this.dis = new DataInputStream(bis);

        this.dos = new DataOutputStream(bos);
    }

    public void writeAndFlush(byte b) throws IOException {
        bos.write(b);
        bos.flush();
    }

    @Override
    public void Handle() {

    }
}
