package transaction;

import server.Server;
import store.Store;
import utils.TransactionType;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class RequestHandler extends Transaction{

    private Server myServer;

    private int idLength;

    private String id;
    public RequestHandler(BufferedInputStream bis, BufferedOutputStream bos) {
        super(bis, bos);
        this.myServer = Server.instance();
        this.idLength = myServer.getID_length();
    }

    @Override
    public void Handle() {
        System.out.println("Request ID transaction started");
        try {

            long length = dis.readLong();
            String s = "";
            int index;
            while(s.length() < length){
                index = bis.read();
                if(index == -1)
                    throw new EOFException("Input stream end reached unexpectedly before a string could be read");

                s += (char)index;
            }
            id = s;

            System.out.println(id);

            Store store = myServer.requestStore(id);

            if(store == null){
                bos.write(TransactionType.INFO_ERR);
                bos.flush();
            }
            else{
                bos.write(TransactionType.INFO_OK);

                dos.writeInt(store.getFileNumber());
                dos.writeLong(store.getExpectedBytes());
                dos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
