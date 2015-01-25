package com.dpkm95.maze.activity;

import java.util.ArrayList;
import java.util.List;

import com.dpkm95.maze.R;
import com.dpkm95.maze.utils.Archiver;
import com.dpkm95.maze.view.LaunchView;
import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	LaunchView lv;	
	private UiLifecycleHelper uiHelper;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MA","create");
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
	    
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		float width = size.x;
		float height = size.y;
		lv = new LaunchView(this, width, height);
		lv.setBackgroundColor(Color.WHITE);
		setContentView(lv);
	}	

	@Override
	protected void onResume() {
	  super.onResume();

	  // Logs 'install' and 'app activate' App Events.
	  AppEventsLogger.activateApp(this);
	  uiHelper.onResume();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
	  super.onPause();

	  // Logs 'app deactivate' App Event.
	  AppEventsLogger.deactivateApp(this);
	  uiHelper.onPause();	  
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	public void shareFacebookDialog(){
		String linkString = "https://developers.facebook.com/android";
		FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this).setLink(linkString)
	    		.setApplicationName("maze")
	            .setLink("https://www.google.co.in")
	            .setName("Maze Challenge!")
	            .setDescription("My top score: "+Archiver.get_top_score(this)+"\nBeat that!").build();
	    uiHelper.trackPendingDialogCall(shareDialog.present());
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.e("Activity", String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.i("Activity", "Success!");
	        }
	    });
	}

	public void shareFb() {
		Intent shareIntent = new Intent("android.intent.action.SEND");
		   shareIntent.setType("text/plain");
		   shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
		   shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, R.drawable.ic_launcher);

		   PackageManager pm = this.getPackageManager();
		   List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
		     for (final ResolveInfo app : activityList) 
		     {
		         if ((app.activityInfo.name).contains("facebook")) 
		         {
		           final ActivityInfo activity = app.activityInfo;
		           final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
		          shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		          shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		          shareIntent.setComponent(name);
		          startActivity(shareIntent);
		          break;
		        }
		      }
		
	}
}
