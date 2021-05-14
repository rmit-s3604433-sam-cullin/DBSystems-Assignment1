import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;



public abstract class dbstoreable extends dbkey {
    public static final String DEFAULT_ENCODING = "utf-8";
    public static final int RID_SIZE = 4;
    public static final int EOF_PAGENUM_SIZE = 4;

    public int rid;
    public int pageId;

    public void SetDbKey(dbkey key){
        this.rid = key.rId;
        this.pageId = key.pageId;
    }

    public dbkey GetKey(){
        return new dbkey(this.rid, this.pageId);
    }

    public String GetEncoding(){
        return DEFAULT_ENCODING;
    }
    //#region Encoders
    public void copyString(String entry, int size, int OFFSET, byte[] rec)
            throws UnsupportedEncodingException 
    {
        //System.out.println("copyString("+entry+","+size+","+OFFSET+")");
        byte[] DATA = new byte[size];
        byte[] SRC = entry.trim().getBytes(this.GetEncoding());
        if (entry != "")
        {
            System.arraycopy(SRC, 0,
                    DATA, 0, SRC.length);
        }
        System.arraycopy(DATA, 0, rec, OFFSET, DATA.length);
    }
    public void copyInt(String entryS, int size, int OFFSET, byte[] rec)
            throws UnsupportedEncodingException 
    {
        int entry = Integer.parseInt(entryS); 
        copyInt(entry, size, OFFSET, rec);
    }
    public void copyInt(int entry, int size, int OFFSET, byte[] rec)
            throws UnsupportedEncodingException 
    {
        ByteBuffer bBuffer =ByteBuffer.allocate(size);
        bBuffer.putInt(entry);
        byte[] DATA = bBuffer.array();
        System.arraycopy(DATA, 0, rec, OFFSET, DATA.length);
    }
    //#endregion

    //#region Decoders
    int readInt(byte[] record, int size, int offset )
    throws UnsupportedEncodingException
    {
        byte[] OUT = new byte[size];
        System.arraycopy(record, offset , OUT, 0, size);
        int num = ByteBuffer.wrap(OUT).getInt();
        return num;
    }

    String readString(byte[] record, int size, int offset)
    throws UnsupportedEncodingException
    {
        byte[] OUT = new byte[size];
        System.arraycopy(record, offset , OUT, 0 , size);
        return new String(OUT);
        
    }
    //#endregion

    public abstract int getSize();
    public abstract byte[] serialize() throws UnsupportedEncodingException;
    public abstract <T extends dbstoreable> T deserialize(byte[] DATA) throws UnsupportedEncodingException;
}
