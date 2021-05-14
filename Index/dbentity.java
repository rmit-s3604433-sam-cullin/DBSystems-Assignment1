
import java.io.UnsupportedEncodingException;


public class dbentity extends dbstoreable {
    //#region Static Sizes and Offsets
    /* Fixed Variable Lengths */

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

    //#endregion
    
    
    public int id;
    public String date;
    public int year;
    public String month;
    public int mDate;
    public String day;
    public int time;
    public int sensorId;
    public String sensorName;
    public int hourlyCount;

    dbentity(){}



    dbentity initialize(String[] in, int outCount)
            throws UnsupportedEncodingException
    {
        byte[] record = new byte[this.getSize()];
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
        return this.deserialize(record);
    }

    @Override
    public byte[] serialize()
        throws UnsupportedEncodingException 
    {
        byte[] record = new byte[this.getSize()];
        copyInt(    this.rid,          RID_SIZE         , RID_OFFSET         , record);
        copyInt(    this.id,           ID_SIZE          , ID_OFFSET          , record);
        copyString( this.date,         DATETIME_SIZE    , DATETIME_OFFSET    , record);
        copyInt(    this.year,         YEAR_SIZE        , YEAR_OFFSET        , record);
        copyString( this.month,        MONTH_SIZE       , MONTH_OFFSET       , record);
        copyInt(    this.mDate,        MDATE_SIZE       , MDATE_OFFSET       , record);
        copyString( this.day,          DAY_SIZE         , DAY_OFFSET         , record);
        copyInt(    this.time,         TIME_SIZE        , TIME_OFFSET        , record);
        copyInt(    this.sensorId,     SENSORID_SIZE    , SENSORID_OFFSET    , record);
        copyString( this.sensorName,   SENSORNAME_SIZE  , SENSORNAME_OFFSET  , record);
        copyInt(    this.hourlyCount,  HOURLYCOUNT_SIZE , HOURLYCOUNT_OFFSET , record);
        return record;
    }

    @SuppressWarnings("unchecked")
    @Override
    public dbentity deserialize(byte[] record) 
    throws UnsupportedEncodingException
    {
        dbentity dto   = new dbentity();
        dto.rid        = readInt(    record, RID_SIZE         ,  RID_OFFSET        );
        dto.id         = readInt(    record, ID_SIZE          ,  ID_OFFSET         );
        dto.date       = readString( record, DATETIME_SIZE    ,  DATETIME_OFFSET   );
        dto.year       = readInt(    record, YEAR_SIZE        ,  YEAR_OFFSET       );
        dto.month      = readString( record, MONTH_SIZE       ,  MONTH_OFFSET      );
        dto.mDate      = readInt(    record, MDATE_SIZE       ,  MDATE_OFFSET      );
        dto.day        = readString( record, DAY_SIZE         ,  DAY_OFFSET        );
        dto.time       = readInt(    record, TIME_SIZE        ,  TIME_OFFSET       );
        dto.sensorId   = readInt(    record, SENSORID_SIZE    ,  SENSORID_OFFSET   );
        dto.sensorName = readString( record, SENSORNAME_SIZE  ,  SENSORNAME_OFFSET );
        dto.hourlyCount= readInt(    record, HOURLYCOUNT_SIZE ,  HOURLYCOUNT_OFFSET);
        return dto;
    }



    @Override
    public int getSize() {
        return RECORD_SIZE;
    }
    
    @Override
    public String toString() {
        return String.format("JSON(dbentity)=>{ \"pageid\": %d, \"rid\": %d, \"id\": %d, \"date\": \"%s\", \"year\": %d, \"month\": \"%s\", \"mDate\": %d, \"day\": \"%s\", \"time\": %d, \"sensorId\": %d, \"sensorName\": \"%s\", \"hourlyCount\": %d }",
                 this.pageId
                ,this.rid
                ,this.id
                ,this.date
                ,this.year
                ,this.month
                ,this.mDate
                ,this.day
                ,this.time
                ,this.sensorId
                ,this.sensorName
                ,this.hourlyCount
        );
    }

} 