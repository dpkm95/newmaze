package com.dpkm95.maze.activity;

import com.dpkm95.maze.view.ChallengeMode;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class ChallengeActivity extends Activity{
	private ChallengeMode drawView;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		drawView = new ChallengeMode(this);
		drawView.setBackgroundColor(Color.WHITE);
		setContentView(drawView);
	}
}

