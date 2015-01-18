package com.dpkm95.maze.utils;

public class Pawn {
	public int x,fx;
	public int y,fy;
	public int score;
	public float life;
	public int path_covered;	
	public Pawn(int i,int j){
		x=i;
		y=j;
		score = 0;
		life = 100;
		path_covered=0;
	}
	
}
