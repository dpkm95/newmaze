package com.dpkm95.maze.activity;

import com.dpkm95.maze.utils.MazeConstants;
import com.dpkm95.maze.view.ClassicMode;
import com.facebook.AppEventsLogger;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

public class ClassicActivity extends Activity{	
	private ClassicMode drawView;
		 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);						
		drawView = new ClassicMode(this);
		drawView.setBackgroundColor(Color.WHITE);
		setContentView(drawView);	
		Log.d("CA","create");
	}
	
	protected void onStart(){
		super.onStart();
		MazeConstants.PLAY=true;
		Log.d("CA","start");
	}
	
	protected void onDestroy() {
		super.onDestroy();
		MazeConstants.PLAY=false;	
		Log.d("CA","destroy");
	}
}
