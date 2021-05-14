import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class dbkeyloader {

    private String datastorePath = "";
    private int pageSize;
    private long datastoreSize = -1;
    private RandomAccessFile raf;

    dbkeyloader(int pageSize, String datastorePath) {
        this.pageSize = pageSize;
        this.datastorePath = datastorePath;
    }

    public void connect()
        throws FileNotFoundException, IOException
    {
        File file = new File(this.datastorePath);
        this.raf = new RandomAccessFile(file, "rw");
        this.datastoreSize = this.getDataStoreSize();
    }

    public void close()
    {
        if(this.raf != null){
            try{
                this.raf.close();
            }catch(IOException e){
                System.out.println("Error Closing Connection ["+e.toString()+"]");
            }
        }
    }

    public long getDataStoreSize() 
        throws IOException
    {
        if(this.datastoreSize !=  -1 ) { return this.datastoreSize; }
        this.datastoreSize = raf.length();
        return this.datastoreSize;
    }

    protected <T extends dbstoreable> void validateKey(dbkey key , T entity)
        throws Exception
    {
        boolean valid = true;
        if(this.raf == null ) { 
            System.out.println("DB connection is not initialized");
            valid = false; 
        }
        if(this.datastoreSize < key.pageId * this.pageSize ) { 
            System.out.println("PageId is larger that the datastore size");
            valid = false; 
        }
        if(this.pageSize < (key.rId + 1) * entity.getSize()){
            System.out.println("Rid is larger than the datastore page size");
            valid = false;
        } 
        if(!valid){
            throw new Exception("Query is not valid");
        }

    }

    protected long pageIndex(dbkey key){
        return this.pageSize * key.pageId;
    }
    protected <T extends dbstoreable> long entityIndex(dbkey key, T entity){
        return this.pageIndex(key) + key.rId * entity.getSize();
    }
    
    
    
    public <T extends dbstoreable> T saveEntity(dbkey key, T entity)
        throws FileNotFoundException, IOException, Exception
    {
        entity.SetDbKey(key);
        this.validateKey(key, entity);
        byte[] data = entity.serialize();
        this.raf.seek(this.entityIndex(key, entity));
        this.raf.write(data);
        return entity;
    }
    public <T extends dbstoreable> T findEntity(dbkey key, T entity)
        throws FileNotFoundException, IOException, Exception
    {
        this.validateKey(key, entity);
        byte[] RECORD = new byte[entity.getSize()];
        this.raf.seek(this.entityIndex(key, entity));
        this.raf.read(RECORD, 0, RECORD.length);
        T dto = entity.deserialize(RECORD);
        dto.SetDbKey(key);
        return dto;
    } 
    public <T extends dbstoreable> List<T> findPage(dbkey key, T entity)
        throws IOException
    {
        byte[] PAGE = new byte[this.pageSize];
        long pageIndex = this.pageIndex(key);
        System.out.println("Getting page at index "+ pageIndex);
        this.raf.seek(pageIndex);
        this.raf.read(PAGE, 0 , PAGE.length);
        return this.deserializePage(PAGE, entity);
    }

    public <T extends dbstoreable> List<T> savePage(dbkey key,List<T> entities)
        throws UnsupportedEncodingException, IOException, Exception
    {
        if(entities.size() == 0) { throw new Exception("No entities in page"); }
        if(entities.size() * entities.get(0).getSize() > this.pageSize ) { throw new Exception("To many items in page"); }
        
        long pageIndex = this.pageIndex(key);
        byte[] PAGE = new byte[this.pageSize];
        this.serializePage(key, PAGE, entities);
        this.raf.seek(pageIndex);
        this.raf.write(PAGE);
        return entities;
    }

    protected <T extends dbstoreable> void serializePage(dbkey key, byte[] PAGE, List<T> entities)
        throws UnsupportedEncodingException
    {
        int recordLen = 0;
        int recordId = 0;
        for(T entity : entities){
            entity.SetDbKey(new dbkey(recordId, key.pageId));
            byte[] bRecord = entity.serialize();
            System.arraycopy(bRecord, 0, PAGE, recordLen, recordLen+entity.getSize());
            recordLen += entity.getSize();
            recordId += 1;
        }
    }
    protected <T extends dbstoreable> List<T> deserializePage(byte[] PAGE, T entity)
        throws UnsupportedEncodingException
    {
        ArrayList<T> items = new ArrayList<T>();
        int recCount = 0;
        int recordLen = 0;
        int rid = 0;
        boolean isNextRecord = true;
        while (isNextRecord) {
            byte[] bRecord = new byte[entity.getSize()];
            byte[] bRid = new byte[dbstoreable.RID_SIZE];
            System.arraycopy(PAGE, recordLen, bRecord, 0, entity.getSize());
            System.arraycopy(bRecord, 0, bRid, 0, dbstoreable.RID_SIZE);
            rid = ByteBuffer.wrap(bRid).getInt();            
            if (rid != recCount) {
                isNextRecord = false;
            } else {
                T dto = entity.deserialize(bRecord);
                items.add(dto);
            }
            recordLen += entity.getSize();
            recCount++;
            if( recordLen + entity.getSize() > PAGE.length){
                isNextRecord = false;
            }
        }
        return items;
    }
    
    
    
    public boolean readPage(byte[] PAGE, long index)
        throws FileNotFoundException, IOException
    {
        long pointer = index * PAGE.length;

        File file = new File(this.datastorePath);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileLength = raf.length();

        if(pointer > fileLength ) { raf.close(); return false; }

        raf.seek(pointer);
        int total_bytes = raf.read(PAGE, 0, PAGE.length);
        raf.close();
        System.out.println("Loaded Page => { index: "+index+", offset: "+pointer+", pageSize: "+ total_bytes+ ", storeSize: "+fileLength+" } ");
        return true;
    }

    public boolean readRecord(byte[] PAGE, byte[] RECORD, long index, int rid)
        throws FileNotFoundException, IOException
    {
        if(!this.readPage(PAGE, index)) { return false; }
        return this.readRecord(PAGE, RECORD, rid);
    }
    
    public boolean readRecord(byte[] PAGE, byte[] RECORD, int rid){
        if(RECORD.length + 1 * rid > PAGE.length){ return false; } // Guard Against Index Out Of Range
        System.arraycopy(PAGE, RECORD.length * rid, RECORD, 0, RECORD.length);
        System.out.println("Loaded Record => { rid: "+rid+", offset: "+RECORD.length * rid+", recordSize: "+ RECORD.length+" } ");
        return true;
    }
    
    
}
