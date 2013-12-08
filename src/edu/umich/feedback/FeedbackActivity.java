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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends Activity {
  private boolean mIsBound = false;
  private FeedbackService mService;
  private Button startButton;
  private Button stopButton;
  private EditText targetAppName;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findAllViewsById();
    startButton.setOnClickListener(OnClickStartListener);
    stopButton.setOnClickListener(OnClickStopListener);
    startService(new Intent(this, FeedbackService.class));
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
    }
  };
  
  //define start button listener
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
}
