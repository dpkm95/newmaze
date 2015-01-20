package com.dpkm95.maze.view;

import com.dpkm95.maze.activity.FlexibleMazeActivity;
import com.dpkm95.maze.utils.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.dpkm95.maze.utils.GameControl;
import com.dpkm95.maze.utils.MazeConstants;
import com.dpkm95.maze.utils.Node;
import com.dpkm95.maze.utils.Stack;

@SuppressLint("ViewConstructor")
public class DuelMode extends View {	
	private int mClickCtr = 0;
	private Paint paint, paint0, paint1, paint2, paint2i, paint3, paint3i;
	private float W, H;
	private float ballX, ballY,ballXf,ballYf;
	private int mCols, mRows;
	private int mGameState = MazeConstants.STATE_PLAY;
	private float mazeX, mazeY, mazeXf, mazeYf,control_width;
	private float unit;	
	private Pawn player,opponent;
	private FlexibleMazeActivity root;
	private int[][] mMaze;
	private LongestPathFinder lpf;
	private Stack retPath, keys;
	private int destX, destY;
	private int key_count = 0;
	private boolean teleport = false;
	private int restoreX = 0, restoreY = 0,teleX, teleY;
	private Handler mHandler;
	private GameControl up, down, left, right;
	private Vibrator vibrator;
	private MediaPlayer mp_teleport, mp_key, mp_transition, mp_end,mp_win;
	private long[] pattern_crash = { 50, 50, 50 };
	private long[] pattern_win = { 50, 500, 50 };

	public DuelMode(Context context,
			int maze[][], Handler h) {
		super(context);
		mHandler = h;
		root = (FlexibleMazeActivity) context;
		W = getResources().getDisplayMetrics().widthPixels;
		H = getResources().getDisplayMetrics().heightPixels;
		this.mCols = maze.length;
		this.mRows = maze[0].length;

		this.unit = getUnitSize(H, mRows);
		mazeX = (W - (unit * 5 * mCols + unit)) / 2;
		mazeY = unit;
		mazeXf = mazeX + unit * 5 * mCols + unit;
		mazeYf = mazeY + unit * 5 * mRows + unit;
		ballX = mazeX + 3 * unit;
		ballY = mazeY + 3 * unit;
		control_width = (H-mazeYf);

		this.mMaze = maze;
		lpf = new LongestPathFinder(maze, mCols, mRows);
		retPath = lpf.getLongestPath();
		keys = lpf.getEndPoints();
		key_count = keys.getSize();
		destX = retPath.topX();
		destY = retPath.topY();
		
		player = new Pawn(0,0);
		opponent = new Pawn(0,0);	
		
		paint = new Paint();
		paint0 = new Paint();
		paint1 = new Paint();
		paint2 = new Paint();
		paint2i = new Paint();
		paint3 = new Paint();
		paint3i = new Paint();
		paint0.setColor(Color.WHITE);
		paint1.setTypeface(Typeface.createFromAsset(root.getAssets(),
				"fonts/gisha.ttf"));
		paint1.setStrokeWidth(unit);
		paint3.setStrokeWidth(unit);
		paint2.setTypeface(Typeface.createFromAsset(root.getAssets(),
				"fonts/gisha.ttf"));
		switch (MazeConstants.COLOR) {
		case 0:// blue
			paint1.setColor(Color.rgb(108, 185, 225));
			paint2.setColor(Color.rgb(46, 153, 202));
			paint2i.setColorFilter(new LightingColorFilter(0x2E99CA, 0));
			paint3i.setColorFilter(new LightingColorFilter(0xACD6EA, 0));
			paint3.setColor(Color.rgb(172, 214, 234));
			break;
		case 1:// pink
			paint1.setColor(Color.rgb(247, 172, 213));
			paint2.setColor(Color.rgb(218, 131, 173));
			paint2i.setColorFilter(new LightingColorFilter(0xDA83AD, 0));
			paint3i.setColorFilter(new LightingColorFilter(0xFCE9FC, 0));
			paint3.setColor(Color.rgb(252, 233, 252));
			break;
		case 2:// purple
			paint1.setColor(Color.rgb(165, 134, 191));
			paint2.setColor(Color.rgb(139, 87, 162));
			paint2i.setColorFilter(new LightingColorFilter(0x8B57A2, 0));
			paint3i.setColorFilter(new LightingColorFilter(0xE3CDE4, 0));
			paint3.setColor(Color.rgb(227, 205, 228));
			break;
		case 3:// brown
			paint1.setColor(Color.rgb(232, 208, 182));
			paint2.setColor(Color.rgb(168, 131, 105));
			paint2i.setColorFilter(new LightingColorFilter(0xA88369, 0));
			paint3i.setColorFilter(new LightingColorFilter(0xF8EFE6, 0));
			paint3.setColor(Color.rgb(248, 239, 230));
			break;
		case 4:// grey
			paint1.setColor(Color.rgb(166, 165, 163));
			paint2.setColor(Color.rgb(125, 125, 125));
			paint2i.setColorFilter(new LightingColorFilter(0x7D7D7D, 0));
			paint3i.setColorFilter(new LightingColorFilter(0xC9CACC, 0));
			paint3.setColor(Color.rgb(201, 202, 204));
			break;
		}
		
		up = new GameControl(this, unit, 0, control_width);
		down = new GameControl(this, unit, 1,control_width);
		left = new GameControl(this, unit, 2,control_width);
		right = new GameControl(this, unit, 3,control_width);
		
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		mp_teleport = MediaPlayer.create(context,
				com.dpkm95.maze.R.raw.teleport);
		mp_key = MediaPlayer.create(context, com.dpkm95.maze.R.raw.key);
		mp_end = MediaPlayer.create(context, com.dpkm95.maze.R.raw.end);
		mp_transition = MediaPlayer.create(context,
				com.dpkm95.maze.R.raw.transit);
		mp_win = MediaPlayer.create(context, com.dpkm95.maze.R.raw.win);
	}

	private float getUnitSize(float height, int rows) {
		float unit = (float) ((height * 0.8) / (rows * 5));
		return unit;
	}

	// super class method called when invalidate(), it renders the graphics
	public void onDraw(Canvas canvas) {
		switch (mGameState) {
		case MazeConstants.STATE_PLAY:
			paintMaze(canvas);
			paintBackground(canvas);
			paintGameControls(canvas);
			postOwnPosition();
			paintDestination(canvas);

			paintOpponent(canvas);
			paintKeys(canvas);
			paintPlayer(canvas);
			break;
		case MazeConstants.STATE_CRASH:
			if (MazeConstants.VIBRATION)
				vibrator.vibrate(pattern_crash, -1);			
			//paintCrash(canvas);
			//postCrashMessage(mHandler);
			restoreBall();
			break;
		case MazeConstants.STATE_WIN:
			if (MazeConstants.VIBRATION)
				vibrator.vibrate(pattern_win, -1);
			if (MazeConstants.TONE)
				mp_win.start();
			paintWinner(canvas);
			postWinMessage(mHandler);
			break;
		case MazeConstants.STATE_LOSS:
			paintLoss(canvas);
			if (MazeConstants.VIBRATION)
				vibrator.vibrate(pattern_crash, -1);
			if (MazeConstants.TONE)
				mp_end.start();
			// postLossMessage(mHandler); no need as your loss is detected by
			// outlying activity from opponent win only. Hence activity already
			// knows about it
			break;
		}
	}

	private void paintMaze(Canvas canvas) {
		float px = mazeX, py = mazeY;
		for (int i = 0; i < mRows; i++) {
			// print horizontal lines
			for (int j = 0; j < mCols; j++) {
				if ((mMaze[j][i] & 1) == 0) {
					checkCollision(px, py, false);
					canvas.drawRect(px, py, px + 5 * unit, py + unit, paint1);
					px += 5 * unit;
				} else {
					canvas.drawRect(px, py, px + unit, py + unit, paint1);
					px += 5 * unit;
				}
			}
			canvas.drawRect(px, py, px + unit, py + unit, paint1);
			px = mazeX;
			// print vertical lines
			for (int j = 0; j < mCols; j++) {
				if ((mMaze[j][i] & 8) == 0) {
					checkCollision(px, py, true);
					canvas.drawRect(px, py, px + unit, py + 5 * unit, paint1);
					px += 5 * unit;
				} else {
					px += 5 * unit;
				}
			}
			checkCollision(px, py, true);
			canvas.drawRect(px, py, px + unit, py + 5 * unit, paint1);
			py += 5 * unit;
			px = mazeX;
		}
		// print bottom line
		for (int i = 0; i < mCols; ++i) {
			checkCollision(px + 5 * i * unit, py, false);
			canvas.drawRect(px + 5 * i * unit, py, px + 5 * (i + 1) * unit, py
					+ unit, paint1);
		}
		canvas.drawRect(px + 5 * mCols * unit, py, px + 5 * mCols * unit + unit,
				py + unit, paint1);
	}

	private void checkCollision(float px, float py, boolean dir) {
		ballX = mazeX + 5 * unit * player.x + 3 * unit;
		ballY = mazeY + 5 * unit * player.y + 3 * unit;
		ballXf = mazeX + 5 * unit * player.fx + 3 * unit;
		ballYf = mazeY + 5 * unit * player.fy + 3 * unit;

		if (dir) {
			// move right
			if (right.pressed && ballX < px && px < ballXf && py < ballY
					&& ballY < py + 4 * unit) {
				mGameState = MazeConstants.STATE_CRASH;
			}
			// move left
			if (left.pressed && ballXf < px + unit && px + unit < ballX
					&& py < ballY && ballY < py + 4 * unit) {
				mGameState = MazeConstants.STATE_CRASH;
			}
		} else {
			// move down
			if (down.pressed && ballY < py && py < ballYf && px < ballX
					&& ballX < px + 4 * unit) {
				mGameState = MazeConstants.STATE_CRASH;
			}
			// move up
			if (up.pressed && ballYf < py + unit && py + unit < ballY
					&& px < ballX && ballX < px + 4 * unit) {
				mGameState = MazeConstants.STATE_CRASH;
			}
		}
	}

	// paints the non-maze part of screen
	private void paintBackground(Canvas canvas) {		
		canvas.drawRect(0, 0, mazeX, H, paint1);
		canvas.drawRect(0, 0, W, mazeY, paint1);
		canvas.drawRect(mazeXf, mazeY, W, H, paint1);
		canvas.drawRect(mazeX, mazeYf, W, H, paint1);
		
		paint0.setTextSize(3 * unit);
		canvas.drawText("Scores:", mazeXf + (W - mazeXf) / 2
				- (float) (5.5 * unit), mazeY+4*unit, paint0);
		paint0.setTextSize((int)(2.8 * unit));
		canvas.drawText("Own:"+Integer.toString(player.score), mazeXf + (W - mazeXf) / 2
				- (float) (5.5 * unit), mazeY+8*unit, paint0);
		canvas.drawText("Opp:"+Integer.toString(opponent.score), mazeXf + (W - mazeXf) / 2
				- (float) (5.5 * unit),mazeY+12*unit, paint0);
		// Teleport location
		if (teleport) {
			paint.setColor(Color.GRAY);
			canvas.drawCircle(mazeX + 5 * unit * teleX + 3 * unit, mazeY + 5
					* unit * teleY + 3 * unit, unit, paint);
		}
	}

	// paints line on which pointer is placed
	private void paintGameControls(Canvas canvas) {
		if (up.pressed)
			canvas.drawBitmap(up.image, 0, H - 3*control_width, paint3i);
		else
			canvas.drawBitmap(up.image, 0, H - 3*control_width, paint2i);
		if (down.pressed)
			canvas.drawBitmap(down.image, 0, H - control_width , paint3i);
		else
			canvas.drawBitmap(down.image, 0, H - control_width , paint2i);
		if (left.pressed)
			canvas.drawBitmap(left.image, W - 3*control_width , H -control_width , paint3i);
		else
			canvas.drawBitmap(left.image, W - 3*control_width, H - control_width, paint2i);
		if (right.pressed)
			canvas.drawBitmap(right.image, W - control_width, H - control_width,
					paint3i);
		else
			canvas.drawBitmap(right.image, W - control_width, H - control_width,
					paint2i);
	}
	
	public void setDrawState(int state) {
		mGameState = state;
		invalidate();
	}

	private void postOwnPosition(){
		Message msg = mHandler.obtainMessage();
		msg.what = MazeConstants.EVENT_POSITION_UPDATE;
		Bundle b = new Bundle();
		b.putInt(MazeConstants.PositionUpdates.KEY_X, player.x);		
		b.putInt(MazeConstants.PositionUpdates.KEY_Y, player.y);	
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	private void paintDestination(Canvas canvas) {
		paint.setColor(Color.rgb(200, 200, 200));
		canvas.drawCircle(mazeX + 5 * unit * destX + 3 * unit, mazeY + 5 * unit
				* destY + 3 * unit, unit, paint);
	}

	private void paintOpponent(Canvas canvas) {
		paint.setColor(Color.rgb(255, 168, 111));
		canvas.drawCircle(mazeX + 5 * unit * opponent.x + 3 * unit, mazeY + 5
				* unit * opponent.y + 3 * unit, unit, paint);
	}

	// method to paint the remaining keys at end-points
	private void paintKeys(Canvas canvas) {
		keys = checkKeyStatus(keys);
		Node key = keys.top();
		paint.setColor(Color.rgb(255, 208, 47));
		while (key != null) {
			canvas.drawCircle(mazeX + 5 * unit * key.getX() + 3 * unit, mazeY
					+ 5 * unit * key.getY() + 3 * unit, unit, paint);
			key = key.getNext();
		}
	}

	private void updateKeys(int x,int y){
		Node key = keys.top();
		while (key != null) {
			if (x == key.getX() && y == key.getY()) {
				--key_count;	
				++opponent.score;
				if (key.getNext() == null) {
					keys.removeLastNode();
				} else {
					key.removeCurrentNode();
					keys.dec_size();
				}
			}
			key = key.getNext();
		}
	}
	
	// checks if the ball collides with any of the remaining-keys
	private Stack checkKeyStatus(Stack keys) {
		Node key = keys.top();
		while (key != null) {
			if (player.x == key.getX() && player.y == key.getY()) {
				--key_count;
				root.updateOpponentKeys(key.getX(),key.getY());
				if (MazeConstants.TONE)
					mp_key.start();
				else
					++player.score;
				restoreX = key.getX();
				restoreY = key.getY();
				if (key.getNext() == null) {
					keys.removeLastNode();
				} else {
					key.removeCurrentNode();
					keys.dec_size();
				}
			}
			key = key.getNext();
		}
		return keys;
	}

	private void paintPlayer(Canvas canvas) {
		// reached end point
		player.x = player.fx;
		player.y = player.fy;
		if (player.x == destX && player.y == destY && key_count == 0) {
			restoreX = destX;
			restoreY = destY;
			mGameState = MazeConstants.STATE_WIN;
		}
		paint.setColor(Color.GRAY);
		canvas.drawCircle(mazeX + 5 * unit * player.x + 3 * unit, mazeY + 5
				* unit * player.y + 3 * unit, unit, paint);
	}

	private void restoreBall() {
		mGameState = MazeConstants.STATE_PLAY;
		player.fx = restoreX;
		player.fy = restoreY;
		invalidate();
	}

	private void paintWinner(Canvas canvas) {
		if (MazeConstants.VIBRATION)
			vibrator.vibrate(pattern_win, -1);
		paint.setColor(Color.rgb(189, 233, 59));
		canvas.drawRect(0, 0, W, H, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(5 * unit);
		paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
				"fonts/gisha.ttf"));
		canvas.drawText("You Won!", (W - 8 * 3 * unit) / 2, H / 2, paint);
	}

	private void postWinMessage(Handler h) {
		Message msg = h.obtainMessage();
		msg.what = MazeConstants.EVENT_WIN;
		h.sendMessage(msg);
	}

	private void paintLoss(Canvas canvas) {
		paint.setColor(Color.rgb(255, 145, 70));
		canvas.drawRect(0, 0, W, H, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(5 * unit);
		paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
				"fonts/gisha.ttf"));
		canvas.drawText("You Lost!", (W - 9 * 3 * unit) / 2, H / 2, paint);
		}

	private void postQuitMessage() {
		Message msg = mHandler.obtainMessage();
		msg.what = MazeConstants.QUIT_MAZE;
		mHandler.sendMessage(msg);
	}

	public void updateOpponentPosition(int x,int y){
		opponent.x = x;
		opponent.y = y;
		invalidate();
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		int maskedAction = event.getActionMasked();
		switch (maskedAction) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			ballX = mazeX + 5 * unit * player.x + 3 * unit;
			ballY = mazeY + 5 * unit * player.y + 3 * unit;
			if (event.getX() > (ballX - 3 * unit)
					&& event.getX() < (ballX + 3 * unit)
					&& event.getY() > (ballY - 3 * unit)
					&& event.getY() < (ballY + 3 * unit)) {
				teleport = !teleport;
				if (teleport) {
					if (MazeConstants.TONE)
						mp_teleport.start();
					teleX = player.x;
					teleY = player.y;
				} else {
					if (MazeConstants.TONE)
						mp_transition.start();
					player.fx = teleX;
					player.fy = teleY;
				}
				break;
			}

			if (event.getX() < control_width) {
				if (H - 3*control_width < event.getY()
						&& event.getY() < H - 2*control_width) {
					up.pressed = true;
					player.fy -= 1;
				} else if (H - control_width < event.getY() && event.getY() < H) {
					down.pressed = true;
					player.fy += 1;
				}
			} else if (event.getY() > H-control_width) {
				if (W - 3*control_width < event.getX()
						&& event.getX() < W - 2*control_width) {
					left.pressed = true;
					player.fx -= 1;
				} else if (W - control_width < event.getX() && event.getX() < W) {
					right.pressed = true;
					player.fx += 1;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:
			up.pressed = down.pressed = left.pressed = right.pressed = false;
			break;
		}
		invalidate();
		return true;
	}
}