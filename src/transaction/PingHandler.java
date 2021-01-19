package transaction;

import utils.TransactionType;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class PingHandler extends Transaction {

    public PingHandler(BufferedInputStream bis, BufferedOutputStream bos) {
        super(bis, bos);
    }

    @Override
    public void Handle() {
        System.out.println("Ping transaction started");
        try {
            bos.write(TransactionType.PONG);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
