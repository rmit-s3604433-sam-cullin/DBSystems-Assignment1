package bTree;

import java.io.UnsupportedEncodingException;

import dbstore.IdbStorable;
import entity.dbEntity;
import utils.Deserialize;
import utils.Serialize;

public class dbIndexElement<TKey extends Comparable<TKey> & IdbStorable<TKey> > extends dbEntity<dbIndexElement<TKey>> {

    public static final int LOOKUP_PID_SIZE = 4;
    public static final int LOOKUP_RID_SIZE = 4;
    public static final int LOOKUP_RID_OFFSET = 0;
    public static final int LOOKUP_PID_OFFSET = LOOKUP_RID_OFFSET + LOOKUP_RID_SIZE;
    public static final int INDEX_KEY_OFFSET = LOOKUP_PID_OFFSET + LOOKUP_PID_SIZE;

    public int lookupPId;
    public int lookupRId;
    public TKey indexedKey;
    public TKey type;

    public dbIndexElement(TKey type){
        this.type= type;
    }
    public dbIndexElement(TKey indexed, int pid, int rid){
        this.type = indexed;
        this.indexedKey = indexed;
        this.lookupPId = pid;
        this.lookupRId = rid;
    }


    @Override
    public int getSize() {
        return this.indexedKey.getSize() + LOOKUP_PID_SIZE + LOOKUP_RID_SIZE;
    }

    @Override
    public byte[] serialize() throws UnsupportedEncodingException {
        byte[] DATA = new byte[this.getSize()];
        Serialize.bytes(this.indexedKey.serialize(), this.indexedKey.getSize(), INDEX_KEY_OFFSET, DATA);
        Serialize.integer(this.lookupRId, LOOKUP_RID_SIZE, LOOKUP_RID_OFFSET, DATA);
        Serialize.integer(this.lookupPId, LOOKUP_PID_SIZE, LOOKUP_PID_OFFSET, DATA);
        return DATA;
    }

    @Override
    public dbIndexElement<TKey> deserialize(byte[] DATA) throws UnsupportedEncodingException {
        dbIndexElement<TKey> dto = new dbIndexElement<TKey>(this.type);
        dto.indexedKey = this.type.deserialize(
            Deserialize.bytes(DATA, this.type.getSize(), INDEX_KEY_OFFSET)
        );
        dto.lookupPId = Deserialize.integer(DATA, LOOKUP_PID_SIZE, LOOKUP_PID_OFFSET);
        dto.lookupRId = Deserialize.integer(DATA, LOOKUP_RID_SIZE, LOOKUP_RID_OFFSET);
        return dto;
    }
    
}
