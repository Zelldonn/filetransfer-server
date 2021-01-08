import utils.TransactionType;
import utils.UI;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static utils.Paths.createDirectory;

public class FileTransactionHandler extends Transaction{

    private Server myServer;

    private DataInputStream dis;

    private DataOutputStream dos;

    static FileOutputStream fos;

    private long downloadedBytes = 0L, expectedBytes = 0L;

    private int expectedFiles = 0;

    byte[] buffer = new byte[16*1024];

    FileTransactionHandler(BufferedInputStream bis, BufferedOutputStream bos) throws IOException {
        super(bis, bos);

        this.dis = new DataInputStream(bis);

        this.dos = new DataOutputStream(bos);

        this.myServer = Server.instance();
    }

    private void receiveFile(Path downloadDirectory) throws IOException {
        int n = dis.readInt();
        System.out.println("Transferring "+ n +" file(s)");
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
                System.out.print("Will create this file : " + p);

                long fileSize ;
                fileSize = dis.readLong();

                System.out.println( " of size : "+ fileSize);

                try {
                    fos = new FileOutputStream(p.toFile());
                } catch (FileNotFoundException ex) {
                    System.out.println("File not found. ");
                }

                int count = 0;
                long read = 0;
                while (read < fileSize) {
                    count = bis.read(buffer);
                    System.out.println("im reading");
                    read += count;
                    downloadedBytes += count;
                    fos.write(buffer, 0, count);
                }
            }else if(pathType == TransactionType.DIRECTORY){
                System.out.println("Directory received");
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
                System.out.println("path N° " +(i+1) + " :" + DirectoryPathName );
                Path newFolder = Paths.get(downloadDirectory.toString() +"/"+ DirectoryPathName);

                createDirectory(newFolder);
                receiveFile(newFolder);
            }
            writeAndFlush(TransactionType.NEXTFILE);
        }
    }

    @Override
    public void Handle() {
        try {
            Store newStore = this.myServer.allocateStore();

            expectedFiles = dis.readInt();
            expectedBytes = dis.readLong();
            System.out.println("Transaction started : " + expectedFiles + " file(s) expected ("+ UI.byte2Readable(expectedBytes)+")");

            receiveFile(newStore.path);

            if(downloadedBytes == expectedBytes){
                newStore.start();
                byte[] answer = newStore.ID.getBytes();
                dos.writeLong(answer.length);
                dos.flush();
                bos.write(answer);
                bos.flush();
            } else{
                System.err.println("Not all files have been transferred");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
