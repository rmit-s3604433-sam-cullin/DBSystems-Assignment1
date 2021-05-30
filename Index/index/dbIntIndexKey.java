package index;

import java.io.UnsupportedEncodingException;

import dbstore.Idbentity;
import utils.Deserialize;
import utils.Serialize;

public class dbIntIndexKey implements Idbentity<dbIntIndexKey> , Comparable<dbIntIndexKey> {
    private int value;
    public dbIntIndexKey(int value){
        this.value = value;
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public byte[] serialize() throws UnsupportedEncodingException {
        return Serialize.integer(this.value, this.getSize());
    }

    @Override
    public dbIntIndexKey deserialize(byte[] DATA) throws UnsupportedEncodingException {
        return new dbIntIndexKey(Deserialize.integer(DATA));
    }

    @Override
    public int compareTo(dbIntIndexKey o) {
        return this.value - o.value;
    }
}