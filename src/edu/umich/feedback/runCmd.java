package edu.umich.feedback;

import java.io.DataOutputStream;

import android.util.Log;

//define a logcat thread that write to a file
public class runCmd extends Thread {
  String cmd;
  boolean su;
  public runCmd(String Command, boolean usingroot){
    cmd=Command;
    su=usingroot;
  }
   
  public void run(){
    try {
      if (su){
        // with root permission
        Process sh = Runtime.getRuntime().exec("su");      
        DataOutputStream os = new DataOutputStream(sh.getOutputStream());
        Log.d(Constant.logTagMSG, cmd);
        os.writeBytes(cmd);
        os.close();
      } else {
        // without root permission
        Log.d(Constant.logTagMSG,cmd);
        Runtime.getRuntime().exec(cmd); 
      }
    } catch (Exception e) {
      e.printStackTrace();
    }    
  }
}
