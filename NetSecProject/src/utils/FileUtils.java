package utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class FileUtils {

	/** 
	 * read file into byte array
     * @param filename 
     * @return byteArray of file
     * @throws IOException 
     */  
    public static byte[] toByteArray(String filename) throws IOException{  
          
        File f = new File(filename);  
        if(!f.exists()){  
            throw new FileNotFoundException(filename);  
        }  
  
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int)f.length());  
        BufferedInputStream in = null;  
        try{  
            in = new BufferedInputStream(new FileInputStream(f));  
            int buf_size = 1024;  
            byte[] buffer = new byte[buf_size];  
            int length = 0;  
            while(-1 != (length = in.read(buffer,0,buf_size))){  
                bos.write(buffer,0,length);  
            }  
            return bos.toByteArray();  
        }catch (IOException e) {  
            e.printStackTrace();
            throw e;
        }finally{  
            try{  
                in.close();  
            }catch (IOException e) {  
                e.printStackTrace();  
            }  
            bos.close();  
        }  
    }  
    
    public static void main(String args[]) throws IOException{
    	String file = "public.der";
//    	String file = "Message.java";
    	File f = new File(file);
    	System.out.println(f.getPath());
    	System.out.println(f.getCanonicalPath());
    	System.out.println(f.exists());
//    	FileUtils.toByteArray(file);
    }
}
