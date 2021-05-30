import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import bTree.bTreeDB;
import bTree.bTreeRoot;
import dbstore.dbBytePage;
import entity.dbEntityLoader;
import index.dbIndexValue;
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
        dbStringIndexKey keyType = new dbStringIndexKey("");
        dbIndexValue valueType = new dbIndexValue(0,0);
        bTreeRoot<dbStringIndexKey,dbIndexValue> rootNode = new bTreeRoot<dbStringIndexKey,dbIndexValue>(keyType, valueType);

        bTreeDB<dbStringIndexKey,dbIndexValue> saver = new bTreeDB<dbStringIndexKey,dbIndexValue>("/Users/mullin/Documents/uni/DBSystems/Assignment1/Index/data/index", 6 , rootNode);

        dbEntityRow entityType = new dbEntityRow(); 
        dbBytePage<dbEntityRow> pageType = new dbBytePage<>(1024,entityType);
        dbEntityLoader<dbEntityRow,dbBytePage<dbEntityRow>> loader = new dbEntityLoader<>("/Users/mullin/Documents/uni/DBSystems/Assignment1/Index/data/heap.1024", pageType, entityType);

        try{
            loader.connect();

            Iterator<dbEntityRow> rows = loader.iterator();
            int index = 0;
            while(rows.hasNext()){
                dbEntityRow tmpRow = rows.next();
                System.out.println(index + tmpRow.toString());
                index += 1;
                dbStringIndexKey tmpKey = new dbStringIndexKey(tmpRow.sensorName);
                dbIndexValue tmpLocation = new dbIndexValue(tmpRow.getKey());
                rootNode.insert(tmpKey, tmpLocation);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            loader.close();
        }
       




        try {
            System.out.println("Saving");
            saver.connect();
            saver.save();
            saver.close();
            saver.log();
            //System.out.println("RootNodeKey:"+rootNode.getNode().toJsonString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    } 
 
    
   
}
