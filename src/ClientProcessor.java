import utils.TransactionType;
import utils.TransactionTypeHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientProcessor extends Thread{
    private Socket socket;

    private BufferedInputStream bis;

    private BufferedOutputStream bos;

    private HashMap<Byte, TransactionTypeHandler> handlers = new HashMap<>();

    private ArrayList<Runnable> threadFinishListeners = new ArrayList<>();

    ClientProcessor(Socket s) throws IOException {
        this.socket = s;

        this.bis = new BufferedInputStream(s.getInputStream());

        this.bos = new BufferedOutputStream(s.getOutputStream());

        handlers.put(TransactionType.UPLOAD, new FileTransactionHandler(bis, bos));

        handlers.put(TransactionType.PING, new PingHandler(bis, bos));
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        System.out.println("Début transaction client");

        try {
            byte transactionType = (byte) bis.read();
            TransactionTypeHandler tt = handlers.get(transactionType);
            //Handle transaction
            if(tt != null){
                tt.Handle();
            }
            else{
                //invalid packet type
                System.err.println("No handler for packet type : " + transactionType);
                bos.write((byte)-1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        };

        for(Runnable r : threadFinishListeners){
            r.run();
        }
    }

    //listeners
    void addThreadFinishListener(Runnable r){
        this.threadFinishListeners.add(r);
    }
}
