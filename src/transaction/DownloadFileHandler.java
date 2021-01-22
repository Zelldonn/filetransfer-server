package transaction;

import server.Server;
import store.Store;
import utils.TransactionType;
import utils.UI;


import java.io.*;
import java.nio.file.Path;

public class DownloadFileHandler extends Transfer {

    private Server myServer;

    private FileInputStream fis;

    public DownloadFileHandler(BufferedInputStream bis, BufferedOutputStream bos) {
        super(bis, bos);

        this.myServer = Server.instance();
    }

    private void sendFile(File[] fileList) throws IOException {
        int fileNumber = fileList.length;
        //System.out.println("Will send "+ fileList.length +"file");
        dos.writeInt(fileNumber);
        dos.flush();

        for(File file : fileList){
            data = file.getName().getBytes();
            dos.writeLong(data.length);
            dos.flush();
            bos.write(data);
            bos.flush();

            if(file.isFile()){
                //System.out.print(" and it is a file of size " + file.length());
                dos.write(TransactionType.FILE);
                dos.writeLong(file.length());
                dos.flush();

                FileInputStream fis = new FileInputStream(file);
                //System.out.print(" named: " + file.toString());

                int count;
                while((count = fis.read(buffer)) > 0){
                    bos.write(buffer, 0, count);
                    transferredBytes += count;
                }
                bos.flush();
            }else{
                //System.out.print(" and it is a directory");
                dos.writeByte(TransactionType.DIRECTORY);

                //Not sure this is working
                File[] filesInFolder = file.listFiles();

                if(file.isDirectory()){
                    data = file.toString().getBytes();
                    dos.writeLong(data.length);
                    dos.flush();
                    bos.write(data);
                    bos.flush();

                    sendFile(filesInFolder);
                }
            }
            byte next = dis.readByte();
            if(next != TransactionType.NEXT_FILE){
                System.out.println("Server has not sended NEXTFILE ACK");
            }else {
                //System.out.println("Server received my file");
            }
        }
    }

    @Override
    public void Handle() {
        try {
            System.out.println("Download transaction started");
            //have to read the ID
            long length = dis.readLong();
            String s = "";
            int index;
            while(s.length() < length){
                index = bis.read();
                if(index == -1)
                    throw new EOFException("Input stream end reached unexpectedly before a string could be read");

                s += (char)index;
            }

            Store store = myServer.requestStore(s);
            File[] fileList = store.path.toFile().listFiles();

            //TODO :refactor this garbage : make a common project
            if(fileList == null){
                System.err.println("Error fileList is null");
            }
            this.expectedFiles = UI.calculateTotalFiles(fileList);
            this.expectedBytes = UI.calculateTotalSize(fileList);

            dos.writeInt(expectedFiles);
            dos.writeLong(expectedBytes);
            dos.flush();

            System.out.println("transaction started : " + expectedFiles + " file(s) expected ("+ UI.byte2Readable(expectedBytes)+")");

            sendFile(fileList);

        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        }finally{
            try {
                if(fis != null)
                    fis.close();
                //notify server the store is invalid

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(transferredBytes == expectedBytes){
            success = true;
            System.out.println("Transfer done.");
        } else{
            System.err.println("Not all files have been transferred");
        }
    }
}
