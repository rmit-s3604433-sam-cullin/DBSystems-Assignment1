package dbstore;

import java.io.IOException;

public class keydb<TKey extends Idbkey, TValue extends Idbentity<TValue>> extends rafdb {
    TValue type;
    public keydb(String datastorePath, TValue type) {
        super(datastorePath);
        this.type = type;
    }

    public TValue load(TKey key)
        throws IOException
    {
        byte[] RECORD = this.read(key.getIndex(), this.type.getSize());
        return this.type.deserialize(RECORD);
    }

    public TKey save(TKey key, TValue entity)
        throws IOException
    {
        byte[] RECORD = entity.serialize();
        this.write(key.getIndex(), RECORD);
        return key;
    }

}
