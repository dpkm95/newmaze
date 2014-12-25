package com.dpkm95.maze.activity;

import com.dpkm95.maze.utils.MazeConstants;
import com.dpkm95.maze.view.ChallengeMode;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class ChallengeActivity extends Activity{
	private ChallengeMode drawView;
	private int x,y;
	private float unit;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		if(MazeConstants.SIZE){
			x=16;y=10;
			unit = (float) ((height * 0.8) / (y * 5));
		}else{
			x=12;y=8;
			unit = (float) ((height * 0.8) / (y * 5.5));
		}		
		drawView = new ChallengeMode(this, width, height, unit, x, y);
		drawView.setBackgroundColor(Color.WHITE);
		setContentView(drawView);
	}
}

