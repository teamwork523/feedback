package edu.umich.feedback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class Util {
  private static String curFilename = "";
  
  public static String curAppname = "tunein_radio";
  public static boolean feedbackEnabled = false;
  public static int MAX_VOLUME = 15;
  public static int privVolume = 0;
  public static long privVolChangeTime = System.currentTimeMillis();
  
  // convert to the current 
  public static String convertTSinMStoTSinS(long ts) {
    String tsInStr = String.valueOf(ts);
    return tsInStr.substring(0, tsInStr.length() - 3) + "." + 
           tsInStr.substring(tsInStr.length() - 3);
  }
  
  // convert to desired application name
  public static String convertToAppName(String app_name) {
    return app_name.toLowerCase().replace(' ', '_');
  }
  
  // Fetch the current filename
  public static String getFilename() {
    return curFilename;
  }
  
  public static void updateAppName(String app_name) {
    curAppname = convertToAppName(app_name);
  }
  
  //update the current Filename and return the lastest filename
  public static void updateFilename() {
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy_MM_dd-HH:mm:ss");
    String timeNow = sdfDate.format(new Date(System.currentTimeMillis()));
    curFilename = curAppname + "_" + timeNow;
  }
 
  // wrapper to write to sdcard with dafault folder
  public static void writeResultToFile(String filename, String line) {
    writeResultToFile(filename, Constant.outputPath + "/" + curAppname, line);
  }
  
  // Wrote a line to the designated file
  public static void writeResultToFile(String filename, String realFolderPath, String line) {
    // append ".txt" to the filename
    String dstFilePath = realFolderPath + "/" + filename + ".txt";
    // automatic append a newline to the line 
    String content = line + "\n";
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
