package dbstore;

import java.io.UnsupportedEncodingException;

public interface Idbentity<T> {
    public int getSize();
    public byte[] serialize() throws UnsupportedEncodingException;
    public T deserialize(byte[] DATA) throws UnsupportedEncodingException;
}
