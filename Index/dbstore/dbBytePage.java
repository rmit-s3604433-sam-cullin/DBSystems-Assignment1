package dbstore;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import entity.dbEntityKey;
import entity.dbStorable;
import utils.Serialize;

public class dbBytePage<TEntity extends dbStorable<TEntity>> extends dbStorable<dbBytePage<TEntity>> {
    public List<TEntity> entities = new ArrayList<TEntity>();
    protected TEntity type;
    protected int size;

    public dbBytePage(int size, TEntity type){
        super();
        this.type = type;
        this.size = size;
        
    }
    @Override
    public Object clone() {
        return new dbBytePage<TEntity>(this.size, this.type);
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public byte[] serialize() throws UnsupportedEncodingException {
        byte[] PAGE = new byte[this.getSize()];
        int recordOffset = 0;
        int recordId = 0;
        for(TEntity entity : this.entities){
            entity.setKey(new dbEntityKey(this.key.getPageId(),recordId));
            byte[] bRecord = entity.serialize();
            if(bRecord.length != entity.getSize()){
                throw new UnsupportedEncodingException("Error Serialize Data is not the same size as returned by getSize()");
            }
            Serialize.bytes(bRecord, bRecord.length, recordOffset, PAGE);
            recordOffset += bRecord.length;
            recordId += 1;
        }
        return PAGE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public dbBytePage<TEntity> deserialize(byte[] DATA) throws UnsupportedEncodingException {
        dbBytePage<TEntity> page = (dbBytePage<TEntity>) this.clone();
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
        return this.getSize() * this.key.getPageId();
    }

    @Override
    public String toString() {
        return String.format("JSON(dbPage)=>{ \"pageId\": %d, \"pageLength\": %d, \"pageSize\": %d }", this.key.getPageId(),this.entities.size() , this.getSize() );
    }
}
