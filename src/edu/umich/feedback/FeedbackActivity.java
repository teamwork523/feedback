/*
 * @Author: Haokun Luo
 * @Date: 12/04/2013
 * @Detail: A real-time user feedback that logs a file name 
 */

package edu.umich.feedback;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class FeedbackActivity extends Activity {
  private HashMap<String, PInfo> appMap = null;
  private boolean mIsBound = false;
  private FeedbackService mService;
  private Button startButton;
  private Button stopButton;
  private Button resumeButton;
  private EditText targetAppName;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findAllViewsById();
    startButton.setOnClickListener(OnClickStartListener);
    stopButton.setOnClickListener(OnClickStopListener);
    resumeButton.setOnClickListener(OnClickResumeListener);
    startService(new Intent(this, FeedbackService.class));
    
    // fetch all the application information
    if (appMap == null) {
      appMap = getInstalledApps(true);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    long newTime = System.currentTimeMillis();
    String msg = "FeedbackActivity: onResume!!!";
    if (Constant.toastEnabled) {
      Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    Log.i(Constant.logTagMSG, msg);
    
    if (Util.feedbackEnabled == true) {
      // collect feedback
      Toast.makeText(getApplicationContext(), "Feedback received!!!", Toast.LENGTH_SHORT).show();
      String content = Util.convertTSinMStoTSinS(newTime);
      Util.writeResultToFile(Util.getFilename(), content);
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  //  bind all the widgets
  private void findAllViewsById() {
    startButton = (Button) findViewById(R.id.startButton);
    stopButton = (Button) findViewById(R.id.stopButton);
    resumeButton = (Button) findViewById(R.id.resumeButton);
    targetAppName = (EditText) findViewById(R.id.target_app_name);
  }

  // define start button listener
  private OnClickListener OnClickStartListener = new OnClickListener() {
  
    public void onClick(View v) {
      doBindService();
      Util.updateAppName(targetAppName.getText().toString());
      
      Util.updateFilename();
      Util.feedbackEnabled = true;
      String msg = "FeedbackActivity: Start button clicked!!!";
      if (Constant.toastEnabled) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
      }
      Log.i(Constant.logTagMSG, msg);
      
      // launch the application!!!
      if (appMap != null && appMap.containsKey(Util.curAppname)) {
        Intent i;
        PackageManager manager = getPackageManager();
        try {
          i = manager.getLaunchIntentForPackage(appMap.get(Util.curAppname).pname);
          if (i == null)
              throw new PackageManager.NameNotFoundException();
          i.addCategory(Intent.CATEGORY_LAUNCHER);
          startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
          Toast.makeText(getApplicationContext(), 
                "ERROR: fail to find the application name from package manager. Please try again", 
                Toast.LENGTH_SHORT).show();
          Util.feedbackEnabled = false;
        }
      } else {
        Toast.makeText(getApplicationContext(), 
              "ERROR: fail to find the application name from installed packages. Please try again", 
              Toast.LENGTH_SHORT).show();
        Util.feedbackEnabled = false;
      }
    }
  };
  
  //define stop button listener
  private OnClickListener OnClickStopListener = new OnClickListener() {
   
    public void onClick(View v) {
      Util.feedbackEnabled = false;
      doUnbindService(); 
      String msg = "FeedbackActivity: Stop button clicked!!!";
      if (Constant.toastEnabled) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
      }
      Log.i(Constant.logTagMSG, msg);
    }
  };
  
  //define resume button listener
  private OnClickListener OnClickResumeListener = new OnClickListener() {
 
   public void onClick(View v) {
     if (Util.feedbackEnabled != true) {
       return;
     }
     
     // Check whether entered app name exist
     if (appMap != null && appMap.containsKey(targetAppName.getText().toString())) {
       Toast.makeText(getApplicationContext(), 
           "ERROR: fail to find the application name from installed packages. Please try again", 
           Toast.LENGTH_SHORT).show();
       return;
     }
     
     doBindService();
     
     if (!Util.convertToAppName(targetAppName.getText().toString()).equals(Util.curAppname)) {
       Toast.makeText(getApplicationContext(), "App Name changes!!!", Toast.LENGTH_SHORT).show();
       Util.updateAppName(targetAppName.getText().toString());
       Util.updateFilename();
     }
     
     String msg = "FeedbackActivity: Resume button clicked!!!";
     if (Constant.toastEnabled) {
       Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
     }
     Log.i(Constant.logTagMSG, msg);
     
     // launch the application!!!
     Intent i;
     PackageManager manager = getPackageManager();
     try {
       i = manager.getLaunchIntentForPackage(appMap.get(Util.curAppname).pname);
       if (i == null)
           throw new PackageManager.NameNotFoundException();
       i.addCategory(Intent.CATEGORY_LAUNCHER);
       startActivity(i);
     } catch (PackageManager.NameNotFoundException e) {
       Toast.makeText(getApplicationContext(), 
             "ERROR: fail to find the application name from package manager. Please try again", 
             Toast.LENGTH_SHORT).show();
     }
   }
 };
 
  // connect with local service
  private ServiceConnection mConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      // bind to service
      mService = ((FeedbackService.FeedbackBinder) service).getService();
      if (Constant.toastEnabled) {
        Toast.makeText(getApplicationContext(), "FeedbackActivity: Success bind service!!!",
                       Toast.LENGTH_SHORT).show();
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      // disconnect service
      mService = null;
      if (Constant.toastEnabled) {
        Toast.makeText(getApplicationContext(), "FeedbackActivity: Disconnect service!!!",
                       Toast.LENGTH_SHORT).show();
      }
    }
    
  };
  
  private void doBindService() {
    // Establish a connection with the service.  We use an explicit
    // class name because we want a specific service implementation that
    // we know will be running in our own process (and thus won't be
    // supporting component replacement by other applications).
    if (!mIsBound) {
      bindService(new Intent(this, 
                  FeedbackService.class), mConnection, Context.BIND_AUTO_CREATE);
      mIsBound = true;
    }
  }
  
  private void doUnbindService() {
    if (mIsBound) {
      // Detach our existing connection.
      unbindService(mConnection);
      mIsBound = false;
    }
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    //doUnbindService();
  }
  
  // Helper class to get all the application's 
  private class PInfo {
    private String appname = "";
    private String pname = "";
    private String versionName = "";
    private int versionCode = 0;
    private void prettyPrint() {
      String detail = appname + "\n" + pname + "\n" + versionName + "\n" + versionCode;
      if (Constant.toastEnabled) {
        Toast.makeText(getApplicationContext(), "FeedbackActivity: package detail:\n" + detail,
                       Toast.LENGTH_SHORT).show();
      }
    }
  }
  
  // Return a map of Application name to its packet information
  private HashMap<String, PInfo> getInstalledApps(boolean getSysPackages) {
    HashMap<String, PInfo> res = new HashMap<String, PInfo>();        
    List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
    for(int i=0;i<packs.size();i++) {
        PackageInfo p = packs.get(i);
        if ((!getSysPackages) && (p.versionName == null)) {
            continue ;
        }
        PInfo newInfo = new PInfo();
        newInfo.appname = Util.convertToAppName(p.applicationInfo.loadLabel(getPackageManager()).toString());
        newInfo.pname = p.packageName;
        newInfo.versionName = p.versionName;
        newInfo.versionCode = p.versionCode;
        res.put(newInfo.appname, newInfo);
        Log.i(Constant.logTagMSG, "Appname: " + newInfo.appname + "; Package name: " + newInfo.pname);
    }
    return res; 
  }
}
