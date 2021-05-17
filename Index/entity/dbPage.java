package entity;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class dbPage<TEntity extends dbStorable<TEntity>> extends dbStorable<dbPage<TEntity>> {
    public List<TEntity> entities = new ArrayList<TEntity>();
    protected TEntity type;
    protected int size;

    public dbPage(int size, TEntity type){
        super();
        this.type = type;
        this.size = size;
        
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public byte[] serialize() throws UnsupportedEncodingException {
        byte[] PAGE = new byte[this.size];
        int recordLen = 0;
        int recordId = 0;
        for(TEntity entity : this.entities){
            entity.setKey(new dbEntityKey(this.key.getPageId(),recordId));
            byte[] bRecord = entity.serialize();
            System.arraycopy(bRecord, 0, PAGE, recordLen, recordLen+entity.getSize());
            recordLen += entity.getSize();
            recordId += 1;
        }
        return PAGE;
    }

    @Override
    public dbPage<TEntity> deserialize(byte[] DATA) throws UnsupportedEncodingException {
        dbPage<TEntity> page = new dbPage<TEntity>(this.size, this.type);
        System.out.println("deserialize Page ");
        int recCount = 0;
        int recordLen = 0;
        int rid = 0;
        boolean isNextRecord = true;
        while (isNextRecord) {
            byte[] bRecord = new byte[type.getSize()];
            byte[] bRid = new byte[dbEntityKey.RID_SIZE];
            System.arraycopy(DATA, recordLen, bRecord, 0, type.getSize());
            System.arraycopy(bRecord, 0, bRid, 0, dbEntityKey.RID_SIZE);
            rid = ByteBuffer.wrap(bRid).getInt();
            System.out.println(String.format("Loading Record start:%d end:%d rid:%d rSize:%d pSize:%d ", recordLen, recordLen + type.getSize() , rid, type.getSize(), page.getSize() ));            
            if (rid != recCount) {
                isNextRecord = false;
                System.out.println("Stopping last record");
            } else {
                TEntity dto = this.type.deserialize(bRecord);
                System.out.println("adding record");
                page.entities.add(dto);
            }
            recordLen += type.getSize();
            recCount++;
            if( recordLen + type.getSize() > DATA.length){
                System.out.println("Stopping load next record will be out side of range");
                isNextRecord = false;
            }
        }
        return page;
    }

    @Override
    public long getIndex() {
        return this.size * this.key.getPageId();
    }

    @Override
    public String toString() {
        return String.format("JSON(dbPage)=>{ \"pageId\": %d, \"pageLength\": %d, \"pageSize\": %d }", this.key.getPageId(),this.entities.size() , this.size);
    }
}
