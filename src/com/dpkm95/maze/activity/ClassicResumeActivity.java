package com.dpkm95.maze.activity;

import com.dpkm95.maze.view.ClassicResumeMode;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class ClassicResumeActivity extends Activity{	
	private ClassicResumeMode drawView;
		 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);				
		drawView = new ClassicResumeMode(this);
		drawView.setBackgroundColor(Color.WHITE);
		setContentView(drawView);
	}
}
