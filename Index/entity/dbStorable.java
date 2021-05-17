package entity;

import dbstore.IdbStorable;



public abstract class dbStorable<T> implements IdbStorable<T> {
    public static final int EOF_PAGENUM_SIZE = 4;

    public static final int DB_META_PADDING = EOF_PAGENUM_SIZE;

    private int pageSize;

    public void SetPageSize(int size){
        pageSize = size;
    }

    public dbEntityKey key;

    public dbStorable(){
        this.key = new dbEntityKey(0,0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj instanceof dbStorable){
            try {
                dbStorable<Object> casted = (dbStorable<Object>) obj;
                if(casted.key.getPageId() == this.key.getPageId() && casted.key.getRId() == this.key.getRId()){
                    return true;
                }
            }catch(Exception e){}
        }
        return false;
    }



    public dbEntityKey getKey() {
        return this.key ;
    }

    public void setKey(dbEntityKey key) {
        this.key = key;
    }

    @Override
    public long getIndex() {
        return this.key.getIndex(this.getSize(), pageSize);
    }

    


}
