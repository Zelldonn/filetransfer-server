package transaction;

import server.*;
import store.*;
import utils.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static utils.Paths.createDirectory;

public class UploadFileHandler extends Transfer {

    private Server myServer;

    static FileOutputStream fos;

    long totalCapacity ;

    long freePartitionSpace;
    long usablePartitionSpace;

    public UploadFileHandler(BufferedInputStream bis, BufferedOutputStream bos) {
        super(bis, bos);

        File diskPartition = new File("C:");
        totalCapacity = diskPartition.getTotalSpace();
        freePartitionSpace = diskPartition.getFreeSpace();
        usablePartitionSpace = diskPartition.getUsableSpace();

        this.myServer = Server.instance();
    }

    private void receiveFile(Path downloadDirectory) throws IOException {
        int n = dis.readInt();
        //System.out.println("Transferring "+ n +" file(s)");
        for(int i = 0; i < n; i++) {

            long fileNameLength = dis.readLong();

            String s = "";
            int index;
            while(s.length() < fileNameLength){
                index = bis.read();
                if(index == -1)
                    throw new EOFException("Input stream end reached unexpectedly before a string could be read");

                s += (char)index;
            }
            String pathName = s;
            byte pathType = dis.readByte();

            if(pathType == TransactionType.FILE){
                Path p = Paths.get(downloadDirectory.toString() + "/" + pathName);
                //System.out.print("Will create this file : " + p);

                long fileSize ;
                fileSize = dis.readLong();

                //System.out.println( " of size : "+ fileSize);

                try {
                    fos = new FileOutputStream(p.toFile());
                } catch (FileNotFoundException ex) {
                    System.out.println("File not found. ");
                }

                int count = 0;
                long read = 0;
                while (read < fileSize) {
                    count = bis.read(buffer);
                    read += count;
                    transferredBytes += count;
                    fos.write(buffer, 0, count);
                }
            }else if(pathType == TransactionType.DIRECTORY){
                //System.out.println("Directory received");
                long DirectoryNameLength = dis.readLong();

                String dir = "";
                int z;
                while(dir.length() < DirectoryNameLength){
                    z = bis.read();
                    if(z == -1)
                        throw new EOFException("Input stream end reached unexpectedly before a string could be read");

                    dir += (char)z;
                }
                String DirectoryPathName = s;
                //System.out.println("path N° " +(i+1) + " :" + DirectoryPathName );
                Path newFolder = Paths.get(downloadDirectory.toString() +"/"+ DirectoryPathName);

                createDirectory(newFolder);
                receiveFile(newFolder);
            }
            writeAndFlush(TransactionType.NEXT_FILE);
        }
    }

    @Override
    public void Handle() {
        Store newStore = this.myServer.allocateStore();

        try {
            expectedFiles = dis.readInt();
            expectedBytes = dis.readLong();
            if(expectedBytes > freePartitionSpace)
                System.out.println("No space available on server");
            System.out.println("transaction.Transaction started : " + expectedFiles + " file(s) expected ("+ UI.byte2Readable(expectedBytes)+")");

            receiveFile(newStore.path);

        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        }finally{
            try {
                if(fos != null)
                    fos.close();
                //notify server the store is invalid
                if(!success)
                    myServer.invalidateStore(newStore);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(transferredBytes == expectedBytes){
            try {
                success = true;
                newStore.start();
                newStore.setFileNumber(expectedFiles);
                newStore.setExpectedBytes(expectedBytes);
                byte[] answer = newStore.ID.getBytes();
                dos.writeLong(answer.length);
                dos.flush();
                bos.write(answer);
                bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Transfer done.");
        } else{
            System.err.println("Not all files have been transferred");
            myServer.invalidateStore(newStore);
            //if file error : send an error to the client
        }
    }
}
