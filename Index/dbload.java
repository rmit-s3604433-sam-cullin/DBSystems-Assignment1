
import java.io.BufferedReader;
import java.io.FileReader;

import dbstore.dbBytePage;
import entity.dbEntityKey;
import entity.dbEntityLoader;
import utils.Args.ArgOptions;
import utils.Args.IncorrectArgs;
import utils.Args.IntArg;
import utils.Args.StringArg;
import utils.Cli;

public class dbload {
    public static final String HEAP_FNAME = "./data/heap.";

    
    

    public static class dbloadCli extends Cli {
        private ArgOptions<String> fileArg = new StringArg().required().index(2).defaultVal("../data.min.csv").message("csv file path to load the data from");
        private ArgOptions<Integer> pageSizeArg = new IntArg().required().flag("-p").defaultVal(1024).message("The Page size to use for the heap file");

        public String file;
        public int pageSize;
        @Override
        protected String getHelp() {
            return " dbload [file] <options> ";
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
        dbloadCli cli = new dbloadCli();
        cli.Load(args);
        System.out.println(cli.toString());

        dbload indexMain = new dbload();
        try{
            long startTime = System.currentTimeMillis();
            indexMain.run(cli);
            long endTime = System.currentTimeMillis();

            System.out.println("Load time: " + (endTime - startTime) + "ms");
           

        }catch ( Exception e ){
            System.out.println("Error In Main");
            e.printStackTrace();
        }
        

    }

     



    public void run(dbloadCli options){
        dbEntityRow entityType = new dbEntityRow();
        dbEntityKey entityKey = new dbEntityKey();
        dbBytePage<dbEntityRow> pageType = new dbBytePage<dbEntityRow>(options.pageSize, entityType);
        dbEntityLoader<dbEntityRow, dbBytePage<dbEntityRow>> client = new dbEntityLoader<>("/Users/mullin/Documents/uni/DBSystems/Assignment1/Index/data/1024.heap", pageType, entityType);
        BufferedReader br = null;
        String line;
        String  filename = options.file;
        String stringDelimeter = ",";

        try{
            client.connect();

            br = new BufferedReader(new FileReader(filename));
            line = br.readLine(); 
            while ((line = br.readLine()) != null)
            {
                String[] entry = line.split(stringDelimeter, -1);
                dbEntityRow tmp = entityType.initialize(entry, entityKey);
                client.insertEntity(tmp);
            }
            client.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        }

        
    } 
 
    
   
}
