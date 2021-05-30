package entity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import dbstore.dbBytePage;
import dbstore.rafdb;

public class dbEntityLoader<TEntity extends dbStorable<TEntity>, TPage extends dbBytePage<TEntity>> extends rafdb implements Iterable<TEntity> {
    private dbStorable<TPage> pageType;
    private dbStorable<TEntity> entityType;

    public static class EntityIterator<TEntity extends dbStorable<TEntity>, TPage extends dbBytePage<TEntity>>
            implements Iterator<TEntity> {
        private dbEntityLoader<TEntity, TPage> loader;

        EntityIterator(dbEntityLoader<TEntity, TPage> loader) {
            this.loader = loader;
        }

        @Override
        public boolean hasNext() {
            dbEntityKey nextKey = this.loader.getNextKey();
            try {
                this.loader.validateKey(nextKey);
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        @Override
        public TEntity next() {
            try {
                dbEntityKey key = this.loader.getNextKey();
                TEntity entity = this.loader.findEntity(key);
                this.loader.setLastKey(key);
                return entity;
            } catch (Exception e) {
                System.out.println("Error Loading Next Record From Entity Store");
                System.out.println(e.toString());
                System.exit(1);
            }

            return null;
        }

    }


    public dbEntityLoader(String datastorePath, dbStorable<TPage> pageType, dbStorable<TEntity> entityType) {
        super(datastorePath);
        this.pageType = pageType;
        this.entityType = entityType;
    }

    protected void validateKey(dbEntityKey key) throws Exception {
        boolean valid = true;
        if (!this.isInitialized()) {
            System.out.println("DB connection is not initialized");
            valid = false;
        }
        if (this.getDataStoreSize() < key.getPageId() * this.pageType.getSize()) {
            System.out.println("PageId is larger that the datastore size "+key.toString());
            valid = false;
        }
        if (this.pageType.getSize() < (key.getRId() + 1) * this.entityType.getSize()) {
            System.out.println("Page Size"+this.pageType.getSize());
            System.out.println("Offset "+(key.getRId() + 1) * this.entityType.getSize());
            System.out.println("Rid is larger than the datastore page size "+ key.toString());
            valid = false;
        }
        if (!valid) {
            throw new Exception("Query is not valid "+key.toString());
        }

    }

    public long getIndex(dbEntityKey key) {
        return key.getIndex(this.pageType.getSize(), this.entityType.getSize());
    }

    public long getPageIndex(dbEntityKey key) {
        return key.getIndex(this.pageType.getSize(), 0);
    }

    public TEntity findEntity(dbEntityKey key) throws FileNotFoundException, IOException, Exception {
        this.validateKey(key);
        byte[] RECORD = this.read(this.getIndex(key), this.entityType.getSize());
        TEntity dto = this.entityType.deserialize(RECORD);
        dto.setKey(key);
        return dto;
    }

    public TEntity saveEntity(TEntity entity) throws FileNotFoundException, IOException, Exception {
        this.validateKey(entity.getKey());
        byte[] data = entity.serialize();
        this.write(this.getIndex(entity.getKey()), data);
        return entity;
    }

    public TPage findPage(dbEntityKey key) throws IOException {
        long pageIndex = this.getPageIndex(key);
        System.out.println("Getting page at index " + pageIndex);
        byte[] PAGE = this.read(pageIndex, this.pageType.getSize());
        return this.pageType.deserialize(PAGE);
    }

    public TPage savePage(TPage page) throws UnsupportedEncodingException, IOException {
        long pageIndex = this.getPageIndex(page.getKey());
        byte[] PAGE = page.serialize();
        this.write(pageIndex, PAGE);
        return page;
    }

    private TPage cachedPage;
    private dbEntityKey cachedKey;
    protected dbEntityKey getNextKey(){
        if(this.cachedKey == null){ return new dbEntityKey(0, 0); }
        if (this.pageType.getSize() < (this.cachedKey.getRId() + 2) * this.entityType.getSize()) {
            return new dbEntityKey(this.cachedKey.getPageId() + 1, 0);
        }
        return new dbEntityKey(this.cachedKey.getPageId(), this.cachedKey.getRId() + 1);
    }
    protected void setLastKey(dbEntityKey key) {
        this.cachedKey = key;
    }
    @SuppressWarnings("unchecked")
    public void insertEntity(TEntity entity)
        throws UnsupportedEncodingException, IOException
    {
        dbEntityKey key = this.getNextKey();
        // Is Next Page
        if(this.cachedKey == null || key.getPageId() > this.cachedKey.getPageId() ) {
            if(this.cachedPage != null){
                this.savePage(this.cachedPage);
                this.cachedPage = null;
            }
            this.cachedPage = (TPage) this.pageType.clone();
            this.cachedPage.setKey(key);
        }
        entity.setKey(key);
        this.cachedPage.entities.add(entity);
        this.setLastKey(key);
    }

    @Override
    public void close() {
        if(this.cachedPage != null){
            try{
                this.savePage(this.cachedPage);

            }catch(Exception e){
                System.out.println("Was unable to save page cache from last insert!");
                System.out.println(this.cachedPage.entities.size());
                System.out.println(e.toString());
                e.printStackTrace();
            }
            this.cachedPage = null;
        }
        super.close();
    }
    @Override
    public Iterator<TEntity> iterator() {
        return new EntityIterator<TEntity, TPage>(this);
    }

}
