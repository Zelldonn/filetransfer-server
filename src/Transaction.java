import utils.TransactionTypeHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class Transaction implements TransactionTypeHandler {

    protected BufferedInputStream bis;

    protected BufferedOutputStream bos;

    Transaction(BufferedInputStream bis, BufferedOutputStream bos){
        this.bis = bis;

        this.bos = bos;
    }

    public void writeAndFlush(byte b) throws IOException {
        bos.write(b);
        bos.flush();
    }

    @Override
    public void Handle() {

    }
}
