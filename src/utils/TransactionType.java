package utils;

public class TransactionType {
    public static final byte PING = 0x0;

    public static final byte PONG = 0x1;

    public static final byte UPLOAD = 0x2;

    public static final byte FILE = 0x3;

    public static final byte DIRECTORY = 0x4;

    public static final byte NEXT_FILE = 0x12;
}
