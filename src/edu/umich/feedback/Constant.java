package edu.umich.feedback;

import android.os.Environment;

public class Constant {
  public static final String logTagMSG = "UserFeedback";
  
  // output file path
  public static final String outputPath = Environment.getExternalStorageDirectory().getPath() + "/feedback";
}
