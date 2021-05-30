import bTree.bTreeDB;
import bTree.bTreeRoot;
import bTree.dbIndexNode;
import bTree.dbInnerNode;
import entity.dbEntityKey;
import index.dbIndexValue;
import index.dbIntIndexKey;
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
        dbIntIndexKey keyType = new dbIntIndexKey(0);
        dbIndexValue valueType = new dbIndexValue(0,0);
        bTreeRoot<dbIntIndexKey,dbIndexValue> rootNode = new bTreeRoot<dbIntIndexKey,dbIndexValue>(keyType, valueType);
       

        bTreeDB<dbIntIndexKey,dbIndexValue> db = new bTreeDB<dbIntIndexKey,dbIndexValue>("/Users/mullin/Documents/uni/DBSystems/Assignment1/Index/data/index", 6 , rootNode);


    

      



        try {
            System.out.println("Loading");
            db.connect();
            dbIndexNode<dbIntIndexKey,dbIndexValue> root = new dbInnerNode<dbIntIndexKey,dbIndexValue>(keyType, valueType);
            root.initializeLoad(new dbEntityKey(0, 0), db);
            root.load();
            System.out.println(root.toJsonString());
            
            rootNode.setRoot(root);




            dbIndexValue result = rootNode.search(
                new dbIntIndexKey(2888153)
            );

            System.out.println(result);


        } catch (Exception e ){
            e.printStackTrace();
        }finally{
            db.close();
        }
        
    } 
 
    
   
}
