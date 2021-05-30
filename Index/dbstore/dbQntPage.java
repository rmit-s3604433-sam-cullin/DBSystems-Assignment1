package dbstore;

public class dbQntPage<TEntity extends dbStorable<TEntity>> extends dbBytePage<TEntity> {

    public dbQntPage(int size, TEntity type) {
        super(size, type);
    }

    @Override
    public int getSize() {
        return this.size * this.type.getSize();
    }

    @Override
    public Object clone() {
        return new dbQntPage<TEntity>(this.size, this.type);
    }
    
}
