package transaction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;

public class Transfer extends Transaction{

    protected static final int BUFFER_SIZE = 16*1024;

    protected long transferredBytes = 0L, expectedBytes = 0L;

    protected int expectedFiles = 0;

    protected byte[] data, buffer;

    protected boolean success = true;

    Transfer(BufferedInputStream bis, BufferedOutputStream bos) {
        super(bis, bos);

        this.buffer = new byte[BUFFER_SIZE];
    }


}
