import java.util.Iterator;

import bTree.bTreeRoot;
import dbstore.dbBytePage;
import entity.dbEntityLoader;
import index.dbIndexValue;
import index.dbIntIndexKey;
import utils.Args.ArgOptions;
import utils.Args.IncorrectArgs;
import utils.Args.IntArg;
import utils.Args.StringArg;
import utils.Cli;

public class dbindex {

    
    

    public static class dbindexCli extends Cli {
        private ArgOptions<String> fileArg = new StringArg().required().index(2).defaultVal("./data/1024.heap").message("heap file to load");
        private ArgOptions<Integer> pageSizeArg = new IntArg().required().flag("-p").defaultVal(1024).message("The Page size used in the heap file");

        public String file;
        public int pageSize;

        @Override
        protected String getHelp() {
            return " dbindex <options> [file] \n"+
            this.fileArg.toString() + "\n"+
            this.pageSizeArg.toString() + "\n";
        }

        @Override
        protected void loadArgs(String[] args) throws IncorrectArgs {
            this.file = this.fileArg.Load(args);
            this.pageSize = this.pageSizeArg.Load(args);
        }
        @Override
        public String toString() {
            return String.format("file: %s pageSize: %d", this.file, this.pageSize);
        }
    }
    public static void main(String args[])
    {
        dbindexCli cli = new dbindexCli();
        cli.Load(args);
        System.out.println(cli.toString());

        dbindex indexMain = new dbindex();
        try{
            indexMain.run(cli);

        }catch ( Exception e ){
            System.out.println("Error In Main");
            e.printStackTrace();
        }


       
 
    }

    public void run(dbindexCli options){
        dbIntIndexKey keyType = new dbIntIndexKey(0);
        dbIndexValue valueType = new dbIndexValue(0,0);
        bTreeRoot<dbIntIndexKey,dbIndexValue> rootNode = new bTreeRoot<dbIntIndexKey,dbIndexValue>(keyType, valueType);

        dbEntityRow entityType = new dbEntityRow(); 
        dbBytePage<dbEntityRow> pageType = new dbBytePage<>(options.pageSize,entityType);
        dbEntityLoader<dbEntityRow,dbBytePage<dbEntityRow>> loader = new dbEntityLoader<>(options.file, pageType, entityType);
        try{
            loader.connect();
            System.out.println("Loading Index");
            long indexBuildStart = System.currentTimeMillis();
            Iterator<dbEntityRow> rows = loader.iterator();
            while(rows.hasNext()){
                dbEntityRow tmpRow = rows.next();
                dbIntIndexKey tmpKey = new dbIntIndexKey(tmpRow.id);
                dbIndexValue tmpLocation = new dbIndexValue(tmpRow.getKey());
                rootNode.insert(tmpKey, tmpLocation); 
            }
            long indexBuildEnd = System.currentTimeMillis();
            System.out.println("Index Build time: " + (indexBuildEnd - indexBuildStart) + "ms");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            
        }
       




        try {
            int searchId;
            long indexStart, indexEnd, indexTotal,
                  heapStart, heapEnd, heapTotal = -1;
            while(true){
                searchId = options.readInteger("Enter id:");
                System.out.println("Retrieving ID "+ searchId);
            
                indexStart = System.currentTimeMillis();
                dbIndexValue result = rootNode.search(
                    new dbIntIndexKey(searchId)
                );
                indexEnd = System.currentTimeMillis();
                indexTotal = (indexEnd - indexStart);
                System.out.println(String.format("Index Query Time: %d", (indexTotal)));
                if(result == null){
                    System.out.println("No record found");
                    continue;
                }

                heapStart = System.currentTimeMillis();
                dbEntityRow row = loader.findEntity(result.getKey());
                heapEnd = System.currentTimeMillis();
                heapTotal = (heapEnd - heapStart);
                System.out.println(String.format("Heap Query Time: %d", (heapTotal)));

                System.out.println(String.format("Total Query Time:%d", (heapTotal + indexTotal)));
                
    
                System.out.println(result);
                System.out.println(row.toString());
    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    } 
 
    
   
}
