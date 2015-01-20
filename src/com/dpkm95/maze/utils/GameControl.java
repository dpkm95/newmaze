package com.dpkm95.maze.utils;

import com.dpkm95.maze.R;
import com.dpkm95.maze.view.ChallengeMode;
import com.dpkm95.maze.view.ClassicMode;
import com.dpkm95.maze.view.ClassicResumeMode;
import com.dpkm95.maze.view.DuelMode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GameControl {
	public Bitmap image;
	public boolean pressed;
	public GameControl(ClassicMode view,float unit,int i,float control_width){
		switch(i){
		//0-up,1-down,2-left,3-right
		case 0:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int)control_width, (int)control_width), 270);
			break;
		case 1:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width), 90);
			break;
		case 2:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width), 180);
			break;
		case 3:
			image=BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width);
			break;
		}
	}
	public GameControl(ClassicResumeMode view, float unit, int i,float control_width) {
		switch(i){
		//0-up,1-down,2-left,3-right
		case 0:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int)control_width, (int)control_width), 270);
			break;
		case 1:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width), 90);
			break;
		case 2:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width), 180);
			break;
		case 3:
			image=BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width);
			break;
		}
	}		
	public GameControl(DuelMode view, float unit, int i,float control_width) {
		switch(i){
		//0-up,1-down,2-left,3-right
		case 0:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int)control_width, (int)control_width), 270);
			break;
		case 1:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width), 90);
			break;
		case 2:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width), 180);
			break;
		case 3:
			image=BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width);
			break;
		}
	}
	public GameControl(ChallengeMode view, float unit, int i,float control_width) {
		switch(i){
		//0-up,1-down,2-left,3-right
		case 0:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int)control_width, (int)control_width), 270);
			break;
		case 1:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width), 90);
			break;
		case 2:
			image = BitmapTransformer.RotateBitmap(BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width), 180);
			break;
		case 3:
			image=BitmapTransformer.getResizedBitmap(
					BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow),
					(int) control_width, (int) control_width);
			break;
		}
	}
}
