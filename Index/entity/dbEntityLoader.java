package entity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import dbstore.rafdb;


public class dbEntityLoader<TEntity extends dbStorable<TEntity>,TPage extends dbStorable<TPage> > extends rafdb {
    private dbStorable<TPage> pageType;
    private dbStorable<TEntity> entityType;



    public dbEntityLoader(String datastorePath, dbStorable<TPage> pageType, dbStorable<TEntity> entityType) {
        super(datastorePath);
        this.pageType = pageType;
        this.entityType = entityType;
    }



   

    protected void validateKey(dbEntityKey key)
        throws Exception
    {
        boolean valid = true;
        if(!this.isInitialized()) { 
            System.out.println("DB connection is not initialized");
            valid = false; 
        }
        if(this.getDataStoreSize() < key.getPageId() * this.pageType.getSize() ) { 
            System.out.println("PageId is larger that the datastore size");
            valid = false; 
        }
        if(this.pageType.getSize() < (key.getRId() + 1) * this.entityType.getSize()){
            System.out.println("Rid is larger than the datastore page size");
            valid = false;
        } 
        if(!valid){
            throw new Exception("Query is not valid");
        }

    }

    public long getIndex(dbEntityKey key){
        return key.getIndex(this.pageType.getSize() , this.entityType.getSize());
    }

    public long getPageIndex(dbEntityKey key){
        return key.getIndex(this.pageType.getSize(), 0);
    }
    
    
    public TEntity findEntity(dbEntityKey key)
        throws FileNotFoundException, IOException, Exception
    {
        this.validateKey(key);
        byte[] RECORD = this.read(this.getIndex(key), this.entityType.getSize());
        TEntity dto = this.entityType.deserialize(RECORD);
        dto.setKey(key);
        return dto;
    }
    public TEntity saveEntity(TEntity entity)
        throws FileNotFoundException, IOException, Exception
    {
        this.validateKey(entity.getKey());
        byte[] data = entity.serialize();
        this.write(this.getIndex(entity.getKey()), data);
        return entity;
    }
     
    public TPage findPage(dbEntityKey key)
        throws IOException
    {
        long pageIndex = this.getPageIndex(key);
        System.out.println("Getting page at index "+ pageIndex);
        byte[] PAGE = this.read(pageIndex, this.pageType.getSize());
        return this.pageType.deserialize(PAGE);
    }
    public TPage savePage(TPage page)
       throws UnsupportedEncodingException, IOException
    {
        long pageIndex = this.getPageIndex(page.getKey());
        byte[] PAGE = page.serialize();
        this.write(pageIndex, PAGE);
        return page;
    }

    
    
    
}
