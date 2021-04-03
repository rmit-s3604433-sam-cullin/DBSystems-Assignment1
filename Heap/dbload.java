import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;


/**
 *  Database Systems - HEAP IMPLEMENTATION
 */

public class dbload implements dbimpl
{
    
   public static void main(String args[])
   {
      dbload load = new dbload();

      
      long startTime = System.currentTimeMillis();
      load.readArguments(args);
      long endTime = System.currentTimeMillis();

      System.out.println("Load time: " + (endTime - startTime) + "ms");
   }

   
   public void readArguments(String args[])
   {
       System.out.println(Arrays.toString(args));
      if (args.length == 3)
      {
         if (args[0].equals("-p") && isInteger(args[1]))
         {
            readFile(args[2], Integer.parseInt(args[1]));
         }
      }
      else
      {
         System.out.println("Error: only pass in three arguments");
      }
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

   
   public void readFile(String filename, int pagesize)
   {
      dbload load = new dbload();
      dbentity entity = new dbentity();
      File heapfile = new File(HEAP_FNAME + pagesize);
      BufferedReader br = null;
      FileOutputStream fos = null;
      String line = "";
      String nextLine = "";
      String stringDelimeter = ",";
      byte[] RECORD = new byte[dbentity.RECORD_SIZE];
      int outCount, pageCount, recCount;
      outCount = pageCount = recCount = 0;

      try
      {
         
         fos = new FileOutputStream(heapfile);
         br = new BufferedReader(new FileReader(filename));
         
         line = br.readLine(); 
         while ((line = br.readLine()) != null)
         {
            String[] entry = line.split(stringDelimeter, -1);
            
            RECORD = entity.serialize(RECORD, entry, outCount);
            
            
            outCount++;
            fos.write(RECORD);
            if ((outCount+1)*dbentity.RECORD_SIZE > pagesize)
            {
               eofByteAddOn(fos, pagesize, outCount, pageCount);
               
               outCount = 0;
               pageCount++;
            }
            recCount++;
         }
      }
      catch (FileNotFoundException e)
      {
         System.out.println("File: " + filename + " not found.");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         if (br != null)
         {
            try
            {
               
               if ((nextLine = br.readLine()) == null)
               {
                  eofByteAddOn(fos, pagesize, outCount, pageCount);
                  pageCount++;
               }
               fos.close();
               br.close();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }
      System.out.println("Page total: " + pageCount);
      System.out.println("Record total: " + recCount);
   }


   
   
   public void eofByteAddOn(FileOutputStream fos, int pSize, int out, int pCount) 
          throws IOException
   {
      byte[] fPadding = new byte[pSize-(dbentity.RECORD_SIZE*out)-4];
      byte[] bPageNum = intToByteArray(pCount);
      fos.write(fPadding);
      fos.write(bPageNum);
   }

   
   public byte[] intToByteArray(int i)
   {
      ByteBuffer bBuffer = ByteBuffer.allocate(4);
      bBuffer.putInt(i);
      return bBuffer.array();
   }
}