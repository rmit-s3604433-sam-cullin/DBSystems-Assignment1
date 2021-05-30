package index;

import java.io.UnsupportedEncodingException;

import dbstore.Idbentity;
import entity.dbEntityKey;
import utils.Deserialize;
import utils.Serialize;

public class dbIndexValue implements Idbentity<dbIndexValue> {
    public dbEntityKey lookupKey;


    public dbIndexValue( int pageId , int rid){
        this.lookupKey = new dbEntityKey(pageId, rid);
    }
    public dbIndexValue(dbEntityKey key){
        this.lookupKey = key;
    }




    public int getLookupIndex(int pageSize, int recordSize){
        return this.lookupKey.getIndex(pageSize, recordSize);
    }

    public dbEntityKey getKey(){
        return this.lookupKey;
    }

    @Override
    public int getSize() {
        return dbEntityKey.CONTENT_SIZE;
    }



    @Override
    public byte[] serialize() throws UnsupportedEncodingException {
        byte[] DATA = new byte[this.getSize()];
        Serialize.bytes(this.lookupKey.serialize(), this.lookupKey.getSize(), 0, DATA);
        return DATA;
    }

    @Override
    public dbIndexValue deserialize(byte[] DATA) throws UnsupportedEncodingException {
        return new dbIndexValue(new dbEntityKey().deserialize(
            Deserialize.bytes(DATA, dbEntityKey.CONTENT_SIZE, 0)
        ));
    }

    @Override
    public String toString() {
        return String.format("JSON(lookupKey)=>{ \"rId\": %d , \"pageId\": %d }", this.lookupKey.getRId(), this.lookupKey.getPageId());
    }


}

