
import java.io.IOException;
import java.util.Arrays;

import entity.dbEntityKey;
import entity.dbEntityLoader;
import entity.dbPage;

public class dbquery_index {
    public static void main(String args[]) {
        dbquery_index query = new dbquery_index();

        query.readArguments(args);

    }

    public boolean isInteger(String s) {
        boolean isValidInt = false;
        try {
            Integer.parseInt(s);
            isValidInt = true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return isValidInt;
    }

    public void readArguments(String args[]) {
        System.out.println(Arrays.toString(args));
        if (args.length == 3) {
            if (args[0].equals("-p") && isInteger(args[1])) {
                readDB(args[2], Integer.parseInt(args[1]));
            }
        } else {
            System.out.println("Error: only pass in three arguments");
        }
    }

    public void readDB(String filepath , int index ){
        dbEntityRow rowType = new dbEntityRow();
        dbPage<dbEntityRow> pageType = new dbPage<dbEntityRow>(1024, rowType);
        dbEntityLoader<dbEntityRow, dbPage<dbEntityRow> > client = new dbEntityLoader<>(filepath, pageType, rowType);

        try{
            client.connect();
            dbEntityRow row = client.findEntity(new dbEntityKey(index, 1));
            System.out.println(row.toString());

            dbPage<dbEntityRow> page = client.findPage(new dbEntityKey(index, 0));
            System.out.println(page);
            for(dbEntityRow entity : page.entities){
                System.out.println(entity.toString());
            }

        }catch(IOException e ){
            System.out.println(e.toString());
            e.printStackTrace();
        }catch(Exception e ) {
            System.out.println(e.toString());
            e.printStackTrace();
        }finally{
            client.close();
        }

        
    }

  

}