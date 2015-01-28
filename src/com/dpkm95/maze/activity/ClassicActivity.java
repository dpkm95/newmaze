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
	
	protected void onPause(){
		super.onPause();
		if(MazeConstants.PLAY){
			drawView.saveStateVariables();
			//MazeConstants.PLAY=;	
		}
		
		Log.d("CA","pause");
	}
}
