package com.dpkm95.maze.utils;

public class MazeConstants {
	public static int MAZE_ROWS = 16;
	public static int MAZE_COLS = 10;
	public static boolean SIZE = false;
	public static int DIFFICULTY=1;
	public static boolean TONE=true;
	public static boolean VIBRATION = true;
	public static boolean RESUMABLE = false;
	public static final int QUIT_MAZE = 0;
	public static final int EVENT_CRASH = 2;
	public static final int EVENT_WIN = 3;
	public static final int EVENT_LOSS = 1;
	public static final  int EVENT_POSITION_UPDATE = 4;
	public final static int STATE_PLAY = 1;
	public final static int STATE_CRASH = 2;
	public final static int STATE_WIN = 3;
	public final static int STATE_LOSS = 4;
	public static int COLOR = 4;


	public static class PositionUpdates {
		public final static String KEY_X = "xfraction";
		public final static String KEY_Y = "yfraction";
	}
}
