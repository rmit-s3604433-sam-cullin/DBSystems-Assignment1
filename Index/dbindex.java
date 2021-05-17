import java.io.UnsupportedEncodingException;

import bTree.bTreeRoot;
import entity.dbEntity;
import entity.dbEntityKey;
import utils.Args.IncorrectArgs;
import utils.Cli;
import utils.Deserialize;
import utils.Serialize;

public class dbindex {

    public static class dbStringIndex extends dbEntity<dbStringIndex> implements Comparable<dbStringIndex> {

        public static final int MAX_KEY_SIZE = 10;


        public static final int KEY_OFFSET = 0;
        public static final int INDEX_KEY_OFFSET = KEY_OFFSET + MAX_KEY_SIZE;

        public static int pageSize = 1024;
        public static int recordSize = dbEntityRow.RECORD_SIZE;
        
        public String indexValue;
        public dbEntityKey lookupKey;

        dbStringIndex() {}
        dbStringIndex(String indexValue , int pageId , int rid ){
            this.indexValue = indexValue;
            this.lookupKey = new dbEntityKey(pageId, rid);
        }

        @Override
        public int getSize() {
            return MAX_KEY_SIZE + dbEntityKey.CONTENT_SIZE;
        }

        @Override
        public byte[] serialize() throws UnsupportedEncodingException {
            byte[] DATA = new byte[this.getSize()];
            Serialize.bytes(this.lookupKey.serialize(), this.lookupKey.getSize(), INDEX_KEY_OFFSET, DATA);
            Serialize.string(this.indexValue, MAX_KEY_SIZE, KEY_OFFSET , DATA);
            return DATA;
        }

        @Override
        public dbindex.dbStringIndex deserialize(byte[] DATA) throws UnsupportedEncodingException {
            dbStringIndex dto = new dbStringIndex();
            dto.lookupKey = new dbEntityKey().deserialize(
                Deserialize.bytes(DATA, dbEntityKey.CONTENT_SIZE, INDEX_KEY_OFFSET)
            );
            dto.indexValue = Deserialize.string(DATA, MAX_KEY_SIZE, KEY_OFFSET);
            return dto;
        }

        @Override
        public long getIndex() {
            return this.lookupKey.getIndex(pageSize, recordSize);
        }

        @Override
        public int compareTo(dbindex.dbStringIndex o) {
            return o.indexValue.compareTo(this.indexValue);
        }

        @Override
        public String toString() {
            return this.lookupKey.toString();
        }
        
    }

    public static class dbindexCli extends Cli {
        @Override
        protected String getHelp() {
            return " dbindex [file] <options> ";
        }

        @Override
        protected void loadArgs(String[] args) throws IncorrectArgs {

        }
    }
    public static void main(String args[])
    {
        dbindexCli cli = new dbindexCli();
        cli.Load(args);

        dbindex indexMain = new dbindex();
        try{
            indexMain.run(cli);

        }catch ( Exception e ){
            System.out.println("Error In Main");
            e.printStackTrace();
        }


       
 
    }


    public void run(dbindexCli options){
        bTreeRoot<dbStringIndex,dbStringIndex> rootNode = new bTreeRoot<dbStringIndex,dbStringIndex>();
        dbStringIndex test1 = new dbStringIndex("AAAAA",0, 0);
        
        rootNode.insert(test1, test1);
        System.out.println("saved Item");
        dbStringIndex test2 = rootNode.search(test1);
        System.out.println(test2.toString());
        
    } 
 
    
   
}
