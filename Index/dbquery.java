import bTree.bTreeDB;
import bTree.bTreeRoot;
import bTree.dbIndexNode;
import bTree.dbInnerNode;
import entity.dbEntityKey;
import index.dbIndexValue;
import index.dbStringIndexKey;
import utils.Args.IncorrectArgs;
import utils.Cli;

public class dbquery {

    
    

    public static class dbqueryCli extends Cli {
        @Override
        protected String getHelp() {
            return " dbquery [file] <options> ";
        }

        @Override
        protected void loadArgs(String[] args) throws IncorrectArgs {

        }
    }
    public static void main(String args[])
    {
        dbqueryCli cli = new dbqueryCli();
        cli.Load(args);

        dbquery indexMain = new dbquery();
        try{
            indexMain.run(cli);

        }catch ( Exception e ){
            System.out.println("Error In Main");
            e.printStackTrace();
        }


       
 
    }


    public void run(dbqueryCli options){
        dbStringIndexKey.STRING_INDEX_KEY_SIZE = 38;
        dbStringIndexKey keyType = new dbStringIndexKey("");
        dbIndexValue valueType = new dbIndexValue(0,0);
        bTreeRoot<dbStringIndexKey,dbIndexValue> rootNode = new bTreeRoot<dbStringIndexKey,dbIndexValue>(keyType, valueType);
       

        bTreeDB<dbStringIndexKey,dbIndexValue> db = new bTreeDB<dbStringIndexKey,dbIndexValue>("/Users/mullin/Documents/uni/DBSystems/Assignment1/Index/data/index", 6 , rootNode);


    

      



        try {
            System.out.println("Loading");
            db.connect();
            dbIndexNode<dbStringIndexKey,dbIndexValue> root = new dbInnerNode<dbStringIndexKey,dbIndexValue>(keyType, valueType);
            root.initializeLoad(new dbEntityKey(0, 0), db);
            root.load();
            System.out.println(root.toJsonString());
            
            rootNode.setRoot(root);

            db.log();


        } catch (Exception e ){
            e.printStackTrace();
        }finally{
            db.close();
        }
        
    } 
 
    
   
}
