package index;

import java.io.UnsupportedEncodingException;

import dbstore.Idbentity;
import utils.Deserialize;
import utils.Serialize;

public class dbStringIndexKey implements Idbentity<dbStringIndexKey> , Comparable<dbStringIndexKey> {
    public static int STRING_INDEX_KEY_SIZE = 38;
    private String value;
    public dbStringIndexKey(String value){
        this.value = value;
    }

    @Override
    public int getSize() {
        return STRING_INDEX_KEY_SIZE;
    }

    @Override
    public byte[] serialize() throws UnsupportedEncodingException {
        return Serialize.string(this.value, this.getSize());
    }

    @Override
    public dbStringIndexKey deserialize(byte[] DATA) throws UnsupportedEncodingException {
        return new dbStringIndexKey(Deserialize.string(DATA));
    }

    @Override
    public int compareTo(dbStringIndexKey o) {
        return this.value.compareTo(o.value);
    }

    @Override
    public String toString() {
        return "\""+this.value+"\"";
    }
}