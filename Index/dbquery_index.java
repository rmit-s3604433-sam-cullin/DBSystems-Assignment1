
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

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
                readFile(args[2], Integer.parseInt(args[1]));
            }
        } else {
            System.out.println("Error: only pass in three arguments");
        }
    }

    public void readFile(String filepath, int index) {
        dbkeyloader client = new dbkeyloader(1024, filepath);
        dbentity dto = new dbentity();
        try {
            client.connect();
            System.out.println("Connected");
            System.out.println("Reading Page");
            List<dbentity> entities = client.findPage(new dbkey(0, index), dto);
            System.out.println("Loaded entities " +entities.size() );
            for(dbentity entity : entities){
                System.out.println(entity.toString());
            }

            System.out.println("Reading Single Item");
            dbentity entity = client.findEntity(new dbkey(3, index), dto);
            System.out.println(entity.toString());

            System.out.println("Update And Save => " + entity.GetKey().toString());
            entity.id += 1;
            entity.sensorName = "Updated "+ entity.id;
            client.saveEntity(entity.GetKey(), entity);

            System.out.println("Reading Updated Single Item");
            dbentity entity2 = client.findEntity(new dbkey(3, index), dto);
            System.out.println(entity2.toString());

        }catch( Exception e ){
            System.out.println(e.toString());
        } finally {
            client.close();
        }
        // this.datastorePath = filepath;
        // int page_size = 1024;
        // byte[] PAGE = new byte[page_size];
        // byte[] RECORD = new byte[dbentity.RECORD_SIZE];
        // dbentity entity = new dbentity();
        // try {
        //     if(!this.readPage(PAGE, index)){ throw new Exception("Page Index Out of Bounds"); };
        //     this.readRecord(PAGE, RECORD, 0);
        //     dbentity dto = entity.deserialize(RECORD);
        //     System.out.println(dto.toString());

        // } catch (Exception e) {
        //     System.out.println(e.toString());
        // }

        // dbentity entity = new dbentity();

        // String dto = entity.deserialize(RECORD);
        // System.out.println(dto);

    }

    
    public void PrintPage(byte[] bPage) {
        dbentity entity = new dbentity();
        int intSize = 4;
        int pageCount = 0;
        int recCount = 0;
        int recordLen = 0;
        int rid = 0;
        boolean isNextPage = true;
        boolean isNextRecord = true;
        while (isNextRecord) {
            byte[] bRecord = new byte[dbentity.RECORD_SIZE];
            byte[] bRid = new byte[intSize];
            try {
                System.arraycopy(bPage, recordLen, bRecord, 0, dbentity.RECORD_SIZE);
                System.arraycopy(bRecord, 0, bRid, 0, intSize);
                rid = ByteBuffer.wrap(bRid).getInt();
                if (rid != recCount) {
                    isNextRecord = false;
                } else {
                    dbentity dto = entity.deserialize(bRecord);
                    System.out.println(dto.toString());
                    recordLen += dbentity.RECORD_SIZE;
                }
                recCount++;

                if(recordLen + dbentity.RECORD_SIZE > bPage.length){
                    isNextRecord = false;
                    recordLen = 0;
                    recCount = 0;
                    rid = 0;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                isNextRecord = false;
                recordLen = 0;
                recCount = 0;
                rid = 0;
                System.out.println("Got Index Out of range ArrayIndexOutOfBoundsException");
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
        }
    }

}