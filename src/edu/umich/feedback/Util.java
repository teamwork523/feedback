package edu.umich.feedback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import android.util.Log;

public class Util {
  public static long privVolChangeTime = System.currentTimeMillis();
  
  // write result to the scroll screen
  // create lock to prevent multiple file writes at the same time
  public static void writeResultToFile(String filename, String foldername, String content) {
    String realFolderPath = Constant.outputPath + 
                        (foldername == "" ? "" : "/" + foldername);
    String dstFilePath = realFolderPath + "/" + filename;
    File d = new File(realFolderPath);
    File f = new File(dstFilePath);
    
    // check if directory exist
    if (!d.exists()) {
      if (!d.mkdirs()) {
        Log.e(Constant.logTagMSG, "ERROR: fail to create directory " + realFolderPath);
        return;
      }
    }
    
    // check file existence
    if (!f.exists()) {
      try {
        f.createNewFile();
        // set file to be readable
      } catch (IOException e) {
        e.printStackTrace();
        Log.e(Constant.logTagMSG, "ERROR: fail to create file " + dstFilePath);
      }
    }
    
    // append to file 
    try {
      // prevent multiple threads write to the same file
      @SuppressWarnings("resource")
      FileChannel channel = new RandomAccessFile(f, "rw").getChannel(); // Use the file channel to create a lock on the file.
      FileLock lock = null;
      
      do {
        // try to acquire a lock
        lock = channel.tryLock();
      } while (lock == null);
    
      FileOutputStream out = new FileOutputStream(f, true);
      out.write(content.getBytes(), 0, content.length());
      out.close();
      
      // release the lock
      lock.release();
      channel.close();
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(Constant.logTagMSG, "ERROR: cannot write to file.\n" + e.toString());
    }
  }
}
