import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;



public class dbquery implements dbimpl
{
   public static void main(String args[])
   {
      dbquery load = new dbquery();

      long startTime = System.currentTimeMillis();
      load.readArguments(args);
      long endTime = System.currentTimeMillis();

      System.out.println("Query time: " + (endTime - startTime) + "ms");
   }


   public void readArguments(String args[])
   {
	   
	   String name = null;
	   int size = 0;
	   if(args.length != 2){
		   size = 4096;
	   }
      if (args.length == 2)
      {
         if (isInteger(args[1]))
         {
            name = args[0];
            size = Integer.valueOf(args[1]);
         }
      }
      
      readHeap(name, size);
   }

   public boolean isInteger(String s)
   {
      boolean isValidInt = false;
      try
      {
         Integer.parseInt(s);
         isValidInt = true;
      }
      catch (NumberFormatException e)
      {
         e.printStackTrace();
      }
      return isValidInt;
   }

   public void readAllRecordsInHeap(String name, int pagesize)
   {
      File heapfile = new File(HEAP_FNAME + pagesize);
      dbentity entity = new dbentity();
      int intSize = 4;
      int pageCount = 0;
      int recCount = 0;
      int recordLen = 0;
      int rid = 0;
      boolean isNextPage = true;
      boolean isNextRecord = true;
      try
      {
         FileInputStream fis = new FileInputStream(heapfile);
         while (isNextPage)
         {
            byte[] bPage = new byte[pagesize];
            byte[] bPageNum = new byte[intSize];
            fis.read(bPage, 0, pagesize);
            System.arraycopy(bPage, bPage.length-intSize, bPageNum, 0, intSize);

            isNextRecord = true;
            while (isNextRecord)
            {
               byte[] bRecord = new byte[dbentity.RECORD_SIZE];
               byte[] bRid = new byte[intSize];
               try
               {
                  System.arraycopy(bPage, recordLen, bRecord, 0, dbentity.RECORD_SIZE);
                  System.arraycopy(bRecord, 0, bRid, 0, intSize);
                  rid = ByteBuffer.wrap(bRid).getInt();
                  if (rid != recCount)
                  {
                     isNextRecord = false;
                  }
                  else
                  {
                     String dto = entity.deserialize(bRecord);
                      if(dto.toLowerCase().contains(name.toLowerCase())){
                          System.out.println(dto);
                      }
                     recordLen += dbentity.RECORD_SIZE;
                  }
                  recCount++;
               }
               catch (ArrayIndexOutOfBoundsException e)
               {
                  isNextRecord = false;
                  recordLen = 0;
                  recCount = 0;
                  rid = 0;
               }
            }
            if (ByteBuffer.wrap(bPageNum).getInt() != pageCount)
            {
               isNextPage = false;
            }
            pageCount++;
         }
      }
      catch (FileNotFoundException e)
      {
         System.out.println("File: " + HEAP_FNAME + pagesize + " not found.");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
   
   
   public void readHeap(String name, int pagesize)
   {
      File heapfile = new File(HEAP_FNAME + pagesize);
      dbentity entity = new dbentity();
      int intSize = 4;
      int pageCount = 0;
      int recCount = 0;
      int recordLen = 0;
      int rid = 0;
      boolean isNextPage = true;
      boolean isNextRecord = true;
      try
      {
         FileInputStream fis = new FileInputStream(heapfile);
         while (isNextPage)
         {
            byte[] bPage = new byte[pagesize];
            byte[] bPageNum = new byte[intSize];
            fis.read(bPage, 0, pagesize);
            System.arraycopy(bPage, bPage.length-intSize, bPageNum, 0, intSize);

            isNextRecord = true;
            while (isNextRecord)
            {
               byte[] bRecord = new byte[dbentity.RECORD_SIZE];
               byte[] bRid = new byte[intSize];
               try
               {
                  System.arraycopy(bPage, recordLen, bRecord, 0, dbentity.RECORD_SIZE);
                  System.arraycopy(bRecord, 0, bRid, 0, intSize);
                  rid = ByteBuffer.wrap(bRid).getInt();
                  if (rid != recCount)
                  {
                     isNextRecord = false;
                  }
                  else
                  {
                      String dto = entity.deserialize(bRecord);
                      if(dto.toLowerCase().contains(name.toLowerCase())){
                          System.out.println(dto);
                      }
                     recordLen += dbentity.RECORD_SIZE;
                  }
                  recCount++;
               }
               catch (ArrayIndexOutOfBoundsException e)
               {
                  isNextRecord = false;
                  recordLen = 0;
                  recCount = 0;
                  rid = 0;
               }
            }
            if (ByteBuffer.wrap(bPageNum).getInt() != pageCount)
            {
               isNextPage = false;
            }
            pageCount++;
         }
      }
      catch (FileNotFoundException e)
      {
         System.out.println("File: " + HEAP_FNAME + pagesize + " not found.");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}