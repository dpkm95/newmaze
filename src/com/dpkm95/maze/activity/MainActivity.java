package com.dpkm95.maze.activity;

import com.dpkm95.maze.view.ClassicMode;
import com.dpkm95.maze.view.LaunchView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	LaunchView lv;	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		float width = size.x;
		float height = size.y;
		lv = new LaunchView(this, width, height);
		lv.setBackgroundColor(Color.WHITE);
		setContentView(lv);
	}	

	public void sendOwnMaze() {
		// TODO Auto-generated method stub
		
	}

	public Object getOpponentMaze(String readMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	public void startGame(int[][] mOwnMaze, int[][] mOppMaze) {
		// TODO Auto-generated method stub
		
	}
}
