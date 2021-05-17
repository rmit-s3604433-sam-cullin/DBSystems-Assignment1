package utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class Serialize {
    public static String ENCODING = "utf-8";
    public static void bytes(byte[] entry, int size , int OFFSET, byte[] rec){
        System.arraycopy(entry, 0, rec, OFFSET, entry.length);
    }
    public static void string(String entry, int size, int OFFSET, byte[] rec)
            throws UnsupportedEncodingException 
    {
        //System.out.println("copyString("+entry+","+size+","+OFFSET+")");
        byte[] DATA = new byte[size];
        byte[] SRC = entry.trim().getBytes(ENCODING);
        if (entry != "")
        {
            System.arraycopy(SRC, 0,
                    DATA, 0, SRC.length);
        }
        System.arraycopy(DATA, 0, rec, OFFSET, DATA.length);
    }
    public static void integer(String entryS, int size, int OFFSET, byte[] rec)
            throws UnsupportedEncodingException 
    {
        int entry = Integer.parseInt(entryS); 
        integer(entry, size, OFFSET, rec);
    }
    public static void integer(int entry, int size, int OFFSET, byte[] rec)
            throws UnsupportedEncodingException 
    {
        ByteBuffer bBuffer =ByteBuffer.allocate(size);
        bBuffer.putInt(entry);
        byte[] DATA = bBuffer.array();
        System.arraycopy(DATA, 0, rec, OFFSET, DATA.length);
    }
}
