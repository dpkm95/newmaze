package com.dpkm95.maze.activity;

import com.dpkm95.maze.utils.MazeConstants;
import com.dpkm95.maze.view.ClassicMode;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class ClassicActivity extends Activity{	
	private ClassicMode drawView;
	private int x,y;
	private float unit;
		 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		float width = size.x;
		float height = size.y;
		if(MazeConstants.SIZE){
			x=16;y=10;
			unit = (float) ((height * 0.8) / (y * 5));
		}else{
			x=12;y=8;
			unit = (float) ((height * 0.8) / (y * 5.5));
		}
		
		drawView = new ClassicMode(this,width, height, unit, x, y);
		drawView.setBackgroundColor(Color.WHITE);
		setContentView(drawView);	
		//drawView.clockedUpdate();
	}
	
	public void force_invalidate(){
		drawView.invalidate();
	}
	
	public void startClock(){
		drawView.clockedUpdate();
	}
}
