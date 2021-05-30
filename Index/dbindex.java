import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bTree.bTreeRoot;
import dbstore.dbBytePage;
import entity.dbEntityLoader;
import index.dbIndexValue;
import index.dbIntIndexKey;
import index.dbStringIndexKey;
import utils.Args.IncorrectArgs;
import utils.Cli;

public class dbindex {

    
    

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
        dbStringIndexKey.STRING_INDEX_KEY_SIZE = 38;
        //dbStringIndexKey keyType = new dbStringIndexKey("");
        dbIntIndexKey keyType = new dbIntIndexKey(0);
        dbIndexValue valueType = new dbIndexValue(0,0);
        bTreeRoot<dbIntIndexKey,dbIndexValue> rootNode = new bTreeRoot<dbIntIndexKey,dbIndexValue>(keyType, valueType);

       // bTreeDB<dbIntIndexKey,dbIndexValue> saver = new bTreeDB<dbIntIndexKey,dbIndexValue>("/Users/mullin/Documents/uni/DBSystems/Assignment1/Index/data/index", 6 , rootNode);

        dbEntityRow entityType = new dbEntityRow(); 
        dbBytePage<dbEntityRow> pageType = new dbBytePage<>(1024,entityType);
        dbEntityLoader<dbEntityRow,dbBytePage<dbEntityRow>> loader = new dbEntityLoader<>("/Users/mullin/Documents/uni/DBSystems/Assignment1/Index/data/1024.heap", pageType, entityType);
        Map<Integer,Integer> cache = new HashMap<>(); 
        try{
            loader.connect();
            System.out.println("Loading Index");
            long indexBuildStart = System.currentTimeMillis();
            Iterator<dbEntityRow> rows = loader.iterator();
            while(rows.hasNext()){
                dbEntityRow tmpRow = rows.next();
                if(!cache.containsKey(tmpRow.id)){
                    dbIntIndexKey tmpKey = new dbIntIndexKey(tmpRow.id);
                    dbIndexValue tmpLocation = new dbIndexValue(tmpRow.getKey());
                    rootNode.insert(tmpKey, tmpLocation);
                    cache.put(tmpRow.id, 1);
                }else{
                    System.out.println("Non Unique Not adding to heap"+tmpRow.id);
                }
                
            }
            long indexBuildEnd = System.currentTimeMillis();
            cache = null;
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
