package edu.umich.feedback;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class NotificationReceiverActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Constant.toastEnabled) {
      Toast.makeText(getApplicationContext(), "NotificationReceiverActivity: created!!!", Toast.LENGTH_SHORT).show();
    }
  }
  
  @Override
  protected void onResume() {
    if (Constant.toastEnabled) {
      Toast.makeText(getApplicationContext(), "NotificationReceiverActivity: onResume!!!", Toast.LENGTH_SHORT).show();
    }
  }
}
