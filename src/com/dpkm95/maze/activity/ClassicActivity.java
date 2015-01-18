package com.dpkm95.maze.activity;

import com.dpkm95.maze.view.ClassicMode;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class ClassicActivity extends Activity{	
	private ClassicMode drawView;
		 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);						
		drawView = new ClassicMode(this);
		drawView.setBackgroundColor(Color.WHITE);
		setContentView(drawView);	
	}
}
