package dbstore;

import java.io.IOException;

public class objectdb<TKey extends Idbkey, TObject extends IdbStorable<TObject>> extends rafdb {
    TObject type;
    public objectdb(String datastorePath, TObject type) {
        super(datastorePath);
        this.type = type;
    }

    public TObject load(TKey obj)
        throws IOException
    {
        byte[] RECORD = this.read(obj.getIndex(), this.type.getSize());
        return this.type.deserialize(RECORD);
    }

    public TObject save(TObject obj)
        throws IOException
    {
        byte[] RECORD = obj.serialize();
        this.write(obj.getIndex(), RECORD);
        return obj;
    }

}
