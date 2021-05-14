public class dbkey {
    public int rId;
    public int pageId;
    dbkey(){}
    dbkey(int rId, int pageId){
        this.rId = rId;
        this.pageId = pageId;
    }

    @Override
    public String toString() {
        return String.format("JSON(dbkey)=>{ \"rId\": %d , \"pageId\": %d }", this.rId, this.pageId);
    }
}