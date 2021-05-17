package utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class Deserialize {
    public static int integer(byte[] record, int size, int offset )
    throws UnsupportedEncodingException
    {
        byte[] OUT = new byte[size];
        System.arraycopy(record, offset , OUT, 0, size);
        int num = ByteBuffer.wrap(OUT).getInt();
        return num;
    }
    public static String string(byte[] record, int size, int offset)
    throws UnsupportedEncodingException
    {
        byte[] OUT = new byte[size];
        System.arraycopy(record, offset , OUT, 0 , size);
        return new String(OUT);
        
    }
    public static byte[] bytes(byte[] record, int size, int offset){
        byte[] OUT = new byte[size];
        System.arraycopy(record, offset, OUT, 0, size);
        return OUT;
    }
}
