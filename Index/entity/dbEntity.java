package entity;

public abstract class dbEntity<T> extends dbStorable<T> {


    public long getIndex() {
        return this.getSize() * this.key.getRId();
    }
}