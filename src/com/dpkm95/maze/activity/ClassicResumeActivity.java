package com.dpkm95.maze.activity;

import com.dpkm95.maze.utils.MazeConstants;
import com.dpkm95.maze.view.ClassicResumeMode;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

public class ClassicResumeActivity extends Activity{	
	private ClassicResumeMode drawView;
		 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);				
		drawView = new ClassicResumeMode(this);
		drawView.setBackgroundColor(Color.WHITE);
		setContentView(drawView);
	}
	
	protected void onPause(){
		super.onPause();
		drawView.saveStateVariables();		
		Log.d("CRA","pause");
	}
}
