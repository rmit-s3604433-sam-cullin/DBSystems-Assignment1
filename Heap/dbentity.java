import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class dbentity {
    /* Fixed Variable Lengths */

        /* Other */
    public static final String ENCODING = "utf-8";
    public static final int RID_SIZE = 4;
    public static final int EOF_PAGENUM_SIZE = 4;

        /* Sizes */
    public static final int ID_SIZE = 4;
    public static final int DATETIME_SIZE = 22;
    public static final int YEAR_SIZE = 4;
    public static final int MONTH_SIZE = 9;
    public static final int DAY_SIZE = 9;
    public static final int MDATE_SIZE = 4;
    public static final int TIME_SIZE = 4;
    public static final int SENSORID_SIZE = 4;
    public static final int SENSORNAME_SIZE = 38;
    public static final int HOURLYCOUNT_SIZE = 4;

    public static final int RECORD_SIZE =  RID_SIZE 
                                        + EOF_PAGENUM_SIZE 
                                        + ID_SIZE
                                        + DATETIME_SIZE
                                        + YEAR_SIZE
                                        + MONTH_SIZE
                                        + MDATE_SIZE
                                        + DAY_SIZE
                                        + TIME_SIZE
                                        + SENSORID_SIZE
                                        + SENSORNAME_SIZE
                                        + HOURLYCOUNT_SIZE;

        /* Columns */
    public static final int ID_COL = 0;
    public static final int DATE_COL = 1;
    public static final int YEAR_COL = 2;
    public static final int MONTH_COL = 3;
    public static final int MDATE_COL = 4;
    public static final int DAY_COL = 5;
    public static final int TIME_COL = 6;
    public static final int SENSORID_COL = 7;
    public static final int SENSORNAME_COL = 8;
    public static final int HOURLYCOUNT_COL = 9;


        /* Offsets */ 
    public static final int RID_OFFSET = 0;
    public static final int ID_OFFSET = RID_OFFSET + RID_SIZE;
    public static final int DATETIME_OFFSET = ID_OFFSET + ID_SIZE;
    public static final int YEAR_OFFSET = DATETIME_OFFSET + DATETIME_SIZE;
    public static final int MONTH_OFFSET = YEAR_OFFSET + YEAR_SIZE;
    public static final int MDATE_OFFSET = MONTH_OFFSET + MONTH_SIZE;
    public static final int DAY_OFFSET = MDATE_OFFSET + MDATE_SIZE;
    public static final int TIME_OFFSET = DAY_OFFSET + DAY_SIZE;
    public static final int SENSORID_OFFSET = TIME_OFFSET + TIME_SIZE;
    public static final int SENSORNAME_OFFSET = SENSORID_OFFSET + SENSORID_SIZE;
    public static final int HOURLYCOUNT_OFFSET = SENSORNAME_OFFSET + SENSORNAME_SIZE;





    public void copyString(String entry, int size, int OFFSET, byte[] rec)
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
        //System.out.println("copyInt("+entry+","+size+","+OFFSET+")");
        bBuffer.putInt(entry);
        byte[] DATA = bBuffer.array();
        System.out.println("Writing Int "+ DATA.length);
        System.arraycopy(DATA, 0, rec, OFFSET, size);
    }
    



    byte[] serialize(byte[] record, String[] in, int outCount)
            throws UnsupportedEncodingException
    {
        copyInt(    outCount,             RID_SIZE         , RID_OFFSET         , record);
        copyInt(    in[ID_COL],           ID_SIZE          , ID_OFFSET          , record);
        copyString( in[DATE_COL],         DATETIME_SIZE    , DATETIME_OFFSET    , record);
        copyInt(    in[YEAR_COL],         YEAR_SIZE        , YEAR_OFFSET        , record);
        copyString( in[MONTH_COL],        MONTH_SIZE       , MONTH_OFFSET       , record);
        copyInt(    in[MDATE_COL],        MDATE_SIZE       , MDATE_OFFSET       , record);
        copyString( in[DAY_COL],          DAY_SIZE         , DAY_OFFSET         , record);
        copyInt(    in[TIME_COL],         TIME_SIZE        , TIME_OFFSET        , record);
        copyInt(    in[SENSORID_COL],     SENSORID_SIZE    , SENSORID_OFFSET    , record);
        copyString( in[SENSORNAME_COL],   SENSORNAME_SIZE  , SENSORNAME_OFFSET  , record);
        copyInt(    in[HOURLYCOUNT_COL],  HOURLYCOUNT_SIZE , HOURLYCOUNT_OFFSET , record);
        return record;
    }

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

    String deserialize(byte[] record)
        throws UnsupportedEncodingException
    {
        System.out.println("Deserializing "+ record.length + " " + dbentity.RECORD_SIZE);
        int    RID        = readInt(    record, RID_SIZE         ,  RID_OFFSET        );
        System.out.println("Loaded RID "+ RID);
        int    ID         = readInt(    record, ID_SIZE          ,  ID_OFFSET         );
        System.out.println("Loaded ID "+ ID);
        String DATETIME   = readString( record, DATETIME_SIZE    ,  DATETIME_OFFSET   );
        System.out.println("Loaded DATETIME "+ DATETIME);
        int    YEAR       = readInt(    record, YEAR_SIZE        ,  YEAR_OFFSET       );
        System.out.println("Loaded YEAR "+ YEAR);
        String MONTH      = readString( record, MONTH_SIZE       ,  MONTH_OFFSET      );
        System.out.println("Loaded MONTH "+ MONTH);
        int    MDATE      = readInt(    record, MDATE_SIZE       ,  MDATE_OFFSET      );
        System.out.println("Loaded MDATE "+ MDATE);
        String DAY        = readString( record, DAY_SIZE         ,  DAY_OFFSET        );
        System.out.println("Loaded DAY "+ DAY);
        int    TIME       = readInt(    record, TIME_SIZE        ,  TIME_OFFSET       );
        System.out.println("Loaded TIME "+ TIME);
        int    SENSORID   = readInt(    record, SENSORID_SIZE    ,  SENSORID_OFFSET   );
        System.out.println("Loaded SENSORID "+ SENSORID);
        String SENSORNAME = readString( record, SENSORNAME_SIZE  ,  SENSORNAME_OFFSET );
        System.out.println("Loaded SENSORNAME "+ SENSORNAME);
        int    HOURLYCOUNT= readInt(    record, HOURLYCOUNT_SIZE ,  HOURLYCOUNT_OFFSET);
        System.out.println("Loaded HOURLYCOUNT "+ HOURLYCOUNT);

        return String.format("%d, %d, %s, %d, %s, %d, %s, %d, %d, %s, %d",
                 RID
                ,ID
                ,DATETIME
                ,YEAR
                ,MONTH
                ,MDATE
                ,DAY
                ,TIME
                ,SENSORID
                ,SENSORNAME
                ,HOURLYCOUNT
        );
    }

} 