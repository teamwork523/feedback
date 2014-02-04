package edu.umich.feedback;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

public class Util {
  private static String curFilename = "";
  
  public static String curAppname = "tunein_radio";
  public static boolean feedbackEnabled = false;
  public static boolean logcatEnabled = false;
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
  // You could also append customized tag at the end
  public static String getFilename(String tag) {
    if (tag.equals("")) {
      return curFilename;
    } else {
      return curFilename + "_" + tag;
    }
  }
  
  // Fetch the whole filepath and name
  public static String getFilepath(String tag) {
    return Constant.userFeedbackPath + "/" + curAppname + "/" + getFilename(tag) + ".txt";
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
    writeResultToFile(filename, Constant.userFeedbackPath + "/" + curAppname, line);
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
  
  // Kill a process based on the input keyword
  public static void killProcess(String tar){
    try {
      Process sh = Runtime.getRuntime().exec("su");
      DataOutputStream os = new DataOutputStream(sh.getOutputStream());
      String Command;

      try {
        ArrayList<String> rows=getPlinesfromPS(tar);
        for (int i=0;i<rows.size();i++){
          String[] cols = rows.get(i).split("\\s");
          for (int j=1;j<cols.length;j++) {
            if (cols[j].length()!=0){ 
              Command="kill "+ cols[j]+"\n";
              os.writeBytes(Command);
              break;
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }                     

      Command="exit\n";
      os.writeBytes(Command);
      os.flush();
      os.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected static ArrayList<String> getPlinesfromPS(String processName){
    String resps=executePS();
    String[] lines = resps.split("\\n");
    ArrayList<String> reslines=new ArrayList<String>();
    for (int i=0;i<lines.length;i++){
      if (lines[i].contains(processName)){
        reslines.add(lines[i]);
      }
    }
    return reslines;
  }

  protected static String executePS() {
    String line = null;
    try {
      Process process = Runtime.getRuntime().exec("ps");
      InputStreamReader inputStream = new InputStreamReader(process.getInputStream());
      BufferedReader reader = new BufferedReader(inputStream);
      int read;
      char[] buffer = new char[4096];
      StringBuffer output = new StringBuffer();
      while ((read = reader.read(buffer)) > 0) {
        output.append(buffer, 0, read);
      }
      process.waitFor();

      line = output.toString();
      reader.close();
      inputStream.close();
      reader.close(); 
    } catch (Exception e) {
      e.printStackTrace();
    }
    return line;
  }
}
