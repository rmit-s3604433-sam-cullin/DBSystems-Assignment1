package entity;

import java.io.UnsupportedEncodingException;

import dbstore.Idbentity;
import utils.Deserialize;
import utils.Serialize;

public class dbEntityKey implements Idbentity<dbEntityKey> {
    public static final int RID_SIZE = 4;
    public static final int PID_SIZE = 4;
    public static final int RID_OFFSET = 0;
    public static final int PID_OFFSET = 4;
    public static final int CONTENT_SIZE = RID_SIZE + PID_SIZE;
    private int rId;
    private int pageId;


    public dbEntityKey(){}
    public dbEntityKey(int pageId, int rId){
        this.rId = rId;
        this.pageId = pageId;
    }

    public int getRId(){
        return this.rId;
    }

    public int getPageId() {
        return this.pageId;
    }

    public int getIndex(int pageSize, int recordSize){
        return this.rId * recordSize + this.pageId * pageSize;
    }

    @Override
    public String toString() {
        return String.format("JSON(dbkey)=>{ \"rId\": %d , \"pageId\": %d }", this.rId, this.pageId);
    }
    @Override
    public int getSize() {
        return RID_SIZE + PID_SIZE;
    }

    @Override
    public byte[] serialize() throws UnsupportedEncodingException {
        byte[] record = new byte[this.getSize()];
        Serialize.integer( this.rId , RID_SIZE, RID_OFFSET , record);
        Serialize.integer(this.pageId, PID_SIZE, PID_OFFSET, record);
        return record;
    }

    @Override
    public dbEntityKey deserialize(byte[] DATA) throws UnsupportedEncodingException {
        dbEntityKey dto = new dbEntityKey();
        dto.rId = Deserialize.integer(DATA, RID_SIZE, RID_OFFSET);
        //System.out.println("deserialize.rId "+dto.rId);
        dto.pageId = Deserialize.integer(DATA, PID_SIZE, PID_OFFSET);
        //System.out.println("deserialize.pageId "+dto.pageId);
        return dto;
    }


}