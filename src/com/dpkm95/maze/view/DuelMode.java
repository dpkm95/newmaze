package com.dpkm95.maze.view;

import com.dpkm95.maze.R;
import com.dpkm95.maze.activity.FlexibleMazeActivity;
import com.dpkm95.maze.utils.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

import com.dpkm95.maze.utils.BitmapTransformer;
import com.dpkm95.maze.utils.MazeConstants;
import com.dpkm95.maze.utils.Node;
import com.dpkm95.maze.utils.Stack;

@SuppressLint("ViewConstructor")
public class DuelMode extends View {	
	private int mClickCtr = 0;
	private SparseArray<PointF> mActivePointers;
	private Paint paint = new Paint();
	private float W, H;
	private float ballX, ballY,ballXf,ballYf;
	private int mCols, mRows;
	private int mGameState = MazeConstants.STATE_PLAY;
	private float mazeX, mazeY, mazeXf, mazeYf;
	private float unit;	
	private Pawn player,opponent;
	private FlexibleMazeActivity root;
	private int lcl,bcl;
	private float offset;
	private int[][] mMaze;
	private LongestPathFinder lpf;
	private Stack retPath, keys;
	private int destX, destY;
	private int key_count = 0;
	private boolean teleport = false;
	private int restoreX = 0, restoreY = 0,teleX, teleY;
	private Handler mHandler;
	private Bitmap arrow;
	private Vibrator vibrator;
	private MediaPlayer mp_teleport, mp_key, mp_transition, mp_end,mp_win;
	private long[] pattern_crash = { 50, 50, 50 };
	private long[] pattern_win = { 50, 500, 50 };

	public DuelMode(Context context, float width, float height,
			int maze[][], Handler h) {
		super(context);
		mHandler = h;
		root = (FlexibleMazeActivity) context;
		this.mCols = maze.length;
		this.mRows = maze[0].length;

		this.unit = getUnitSize(height, mRows);
		mActivePointers = new SparseArray<PointF>();
		this.W = width;
		this.H = height;
		mazeX = (width - (unit * 5 * mCols + unit)) / 2;
		mazeY = 2 * unit;
		mazeXf = mazeX + unit * 5 * mCols + unit;
		mazeYf = mazeY + unit * 5 * mRows + unit;
		ballX = mazeX + 3 * unit;
		ballY = mazeY + 3 * unit;

		this.mMaze = maze;
		lpf = new LongestPathFinder(maze, mCols, mRows);
		retPath = lpf.getLongestPath();
		keys = lpf.getEndPoints();
		key_count = keys.getSize();
		destX = retPath.topX();
		destY = retPath.topY();
		
		player = new Pawn(0,0);
		opponent = new Pawn(0,0);	
		
		arrow = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.arrow),
				(int) (4 * unit), (int) (4 * unit));
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

			paintControlLine(canvas);

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
		paint.setColor(Color.rgb(0, 162, 232));
		paint.setStrokeWidth(unit);
		float px = mazeX, py = mazeY;
		for (int i = 0; i < mCols; i++) {
			// print horizontal lines
			for (int j = 0; j < mRows; j++) {
				if ((mMaze[j][i] & 1) == 0) {
					checkCollision(px, py, false);
					canvas.drawRect(px, py, px + 5 * unit, py + unit, paint);
					px += 5 * unit;
				} else {
					canvas.drawRect(px, py, px + unit, py + unit, paint);
					px += 5 * unit;
				}
			}
			canvas.drawRect(px, py, px + unit, py + unit, paint);
			px = mazeX;
			// print vertical lines
			for (int j = 0; j < mRows; j++) {
				if ((mMaze[j][i] & 8) == 0) {
					checkCollision(px, py, true);
					canvas.drawRect(px, py, px + unit, py + 5 * unit, paint);
					px += 5 * unit;
				} else {
					px += 5 * unit;
				}
			}
			checkCollision(px, py, true);
			canvas.drawRect(px, py, px + unit, py + 5 * unit, paint);
			py += 5 * unit;
			px = mazeX;
		}
		// print bottom line
		for (int i = 0; i < mRows; ++i) {
			checkCollision(px + 5 * i * unit, py, false);
			canvas.drawRect(px + 5 * i * unit, py, px + 5 * (i + 1) * unit, py
					+ unit, paint);
		}
		canvas.drawRect(px + 5 * mRows * unit, py, px + 5 * mRows * unit + unit + unit,
				py + unit, paint);
	}

	private void checkCollision(float px, float py, boolean dir) {
		ballX = mazeX + 5 * unit * player.x + 3 * unit;
		ballY = mazeY + 5 * unit * player.y + 3 * unit;
		ballXf = mazeX + 5 * unit * player.fx + 3 * unit;
		ballYf = mazeY + 5 * unit * player.fy + 3 * unit;

		if (dir) {
			// move right
			if (bcl == 3 && ballX < px && px < ballXf && py < ballY
					&& ballY < py + 4 * unit) {
				mGameState = MazeConstants.STATE_CRASH;
			}
			// move left
			if (bcl == 1 && ballXf < px + unit && px + unit < ballX
					&& py < ballY && ballY < py + 4 * unit) {
				mGameState = MazeConstants.STATE_CRASH;
			}
		} else {
			// move down
			if (lcl == 3 && ballY < py && py < ballYf && px < ballX
					&& ballX < px + 4 * unit) {
				mGameState = MazeConstants.STATE_CRASH;
			}
			// move up
			if (lcl == 1 && ballYf < py + unit && py + unit < ballY
					&& px < ballX && ballX < px + 4 * unit) {
				mGameState = MazeConstants.STATE_CRASH;
			}
		}
		// invalidate();
	}

	// paints the non-maze part of screen
	private void paintBackground(Canvas canvas) {
		paint.setColor(Color.rgb(0, 162, 232));
		canvas.drawRect(0, 0, mazeX, H, paint);
		canvas.drawRect(0, 0, W, mazeY, paint);
		canvas.drawRect(mazeXf, mazeY, W, H, paint);
		canvas.drawRect(mazeX, mazeYf, W, H, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(3 * unit);
		paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
				"fonts/gisha.ttf"));
		canvas.drawText("My Score:", mazeXf + (W - mazeXf) / 2
				- (float) (4.5 * unit), H / 4 - 4 * unit, paint);
		canvas.drawText(Integer.toString(player.score), mazeXf + (W - mazeXf) / 2
				- (float) (1.5 * unit), H / 4, paint);
		canvas.drawText("Opp Score:", mazeXf + (W - mazeXf) / 2
				- (float) (4.5 * unit), H / 2, paint);
		canvas.drawText(Integer.toString(opponent.score), mazeXf + (W - mazeXf) / 2
				- (float) (1.5 * unit), H / 2 + 4 * unit, paint);
		// Teleport location
		if (teleport) {
			paint.setColor(Color.GRAY);
			canvas.drawCircle(mazeX + 5 * unit * teleX + 3 * unit, mazeY + 5
					* unit * teleY + 3 * unit, unit, paint);
		}
	}

	// paints line on which pointer is placed
	private void paintControlLine(Canvas canvas) {
		paint.setColor(Color.rgb(153, 217, 234));

		canvas.drawBitmap(arrow, mazeX - 5 * unit, mazeY, null);
		Bitmap down_arrow = BitmapTransformer.RotateBitmap(arrow, 180);
		canvas.drawBitmap(down_arrow, mazeX - 5 * unit, mazeYf - 4 * unit, null);
		Bitmap left_arrow = BitmapTransformer.RotateBitmap(down_arrow, 90);
		canvas.drawBitmap(left_arrow, mazeXf - 3 * offset - unit,
				mazeYf + unit, null);
		Bitmap right_arrow = BitmapTransformer.RotateBitmap(left_arrow, 180);
		canvas.drawBitmap(right_arrow, mazeXf - 4 * unit, mazeYf + unit, null);

		paint.setStrokeWidth(unit);
		canvas.drawLine(mazeX - 3 * unit, mazeY + (int) (1.5 * unit), mazeX - 3
				* unit, mazeYf - (int) (1.5 * unit), paint);
		canvas.drawLine(mazeXf - 3 * offset + (int) (0.5 * unit), mazeYf + 3
				* unit, mazeXf - (int) (1.5 * unit), mazeYf + 3 * unit, paint);
		paint.setColor(Color.rgb(0, 162, 232));
		switch (lcl) {
		case 1:

			canvas.drawRect(mazeX - 5 * unit, mazeY, mazeX - unit, mazeY
					+ offset, paint);
			break;
		case 2:
			canvas.drawRect(mazeX - 5 * unit, mazeY + offset, mazeX - unit,
					mazeY + 2 * offset, paint);
			break;
		case 3:
			canvas.drawRect(mazeX - 5 * unit, mazeY + 2 * offset, mazeX - unit,
					mazeYf, paint);
			break;
		}
		switch (bcl) {
		case 1:
			canvas.drawRect(mazeXf - 3 * offset - unit, mazeYf + unit, mazeXf
					- 2 * offset, mazeYf + 5 * unit, paint);
			break;
		case 2:
			canvas.drawRect(mazeXf - 2 * offset, mazeYf + unit,
					mazeXf - offset, mazeYf + 5 * unit, paint);
			break;
		case 3:
			canvas.drawRect(mazeXf - offset, mazeYf + unit, mazeXf, mazeYf + 5
					* unit, paint);
			break;
		}
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
		int pointerIndex = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIndex);
		int maskedAction = event.getActionMasked();
		switch(mGameState){
		case MazeConstants.STATE_CRASH:
		case MazeConstants.STATE_LOSS:
		case MazeConstants.STATE_WIN:
			if(mClickCtr++==2)
				postQuitMessage();
			return false;//don't handle touch event
		}
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

			if (event.getX() < mazeX && event.getY() < mazeYf) {
				if (mazeY < event.getY() && event.getY() < mazeY + offset) {
					lcl = 1;
					player.fy -= 1;
				} else if (mazeY + offset < event.getY()
						&& event.getY() < mazeY + 2 * offset) {
					lcl = 2;
				} else if (mazeY + 2 * offset < event.getY()
						&& event.getY() < mazeYf) {
					lcl = 3;
					player.fy += 1;
				}
			} else if (event.getY() > mazeYf && event.getX() > mazeX) {
				if (mazeXf - 3 * offset < event.getX()
						&& event.getX() < mazeXf - 2 * offset) {
					bcl = 1;
					player.fx -= 1;
				} else if (mazeXf - 2 * offset < event.getX()
						&& event.getX() < mazeXf - offset) {
					bcl = 2;
				} else if (mazeXf - offset < event.getX()
						&& event.getX() < mazeXf) {
					bcl = 3;
					player.fx += 1;
				}
			}
			PointF f = new PointF();
			f.x = event.getX(pointerIndex);
			f.y = event.getY(pointerIndex);
			mActivePointers.put(pointerId, f);
			break;
		case MotionEvent.ACTION_MOVE:
			for (int size = event.getPointerCount(), i = 0; i < size; i++) {
				//PointF point = mActivePointers.get(event.getPointerId(i));
				if (event.getX(i) < mazeX && event.getY(i) < mazeYf) {
					if (mazeY < event.getY(i) && event.getY(i) < mazeY + offset) {
						lcl = 1;
					} else if (mazeY + offset < event.getY(i)
							&& event.getY(i) < mazeY + 2 * offset) {
						lcl = 2;
					} else if (mazeY + 2 * offset < event.getY(i)
							&& event.getY(i) < mazeYf) {
						lcl = 3;
					}
				} else if (event.getY(i) > mazeYf && event.getX(i) > mazeX) {
					if (mazeXf - 3 * offset < event.getX(i)
							&& event.getX(i) < mazeXf - 2 * offset) {
						bcl = 1;
					} else if (mazeXf - 2 * offset < event.getX(i)
							&& event.getX(i) < mazeXf - offset) {
						bcl = 2;
					} else if (mazeXf - offset < event.getX(i)
							&& event.getX(i) < mazeXf) {
						bcl = 3;
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:
			lcl = bcl = 0;
			mActivePointers.remove(pointerId);
			break;
		}

		invalidate();
		return true;
	}
}