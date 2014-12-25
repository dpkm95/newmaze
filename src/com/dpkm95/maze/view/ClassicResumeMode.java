package com.dpkm95.maze.view;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dpkm95.maze.activity.ClassicResumeActivity;
import com.dpkm95.maze.utils.*;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.dpkm95.maze.utils.MazeGenerator;

@SuppressLint({ "DrawAllocation", "ClickableViewAccessibility" })
public class ClassicResumeMode extends View {
	public final static int STATE_PLAY = 1;
	public final static int STATE_CRASH = 2;
	public final static int STATE_WIN = 3;

	private SparseArray<PointF> mActivePointers = new SparseArray<PointF>();
	Paint paint = new Paint();
	float W, H;
	static float ballX, ballY;
	int x, y;
	int draw = STATE_PLAY;
	float mazeX, mazeY, mazeXf, mazeYf;
	float unit, tradius;
	int dirX, dirY;
	MazeGenerator mg;
	static int[][] maze;
	LongestPathFinder lpf;
	static Stack retPath, keys;
	static int destX, destY;
	static float destfX, destfY;
	float iniX, iniY;
	static float rX, rY, retDestX, retDestY;
	static int key_count = 1;
	static int key_score = 0;
	int life_score = 1;
	int life_number = 0;
	static float restoreX = 0, restoreY = 0;
	static float teleX, teleY;
	boolean teleport = false,archive = true;
	int delay = 20;

	ClassicResumeActivity master = new ClassicResumeActivity();
	ClassicResumeActivity root;
	Context m_context;
	Vibrator vibrator;
	MediaPlayer mp_teleport, mp_key, mp_transition, mp_end, mp_bump;
	long[] pattern_crash = { 50, 50, 50 };
	long[] pattern_win = { 50, 500, 50 };

	// AchievementsActivity archiver = new AchievementsActivity();

	public ClassicResumeMode(Context context) {
		super(context);
	}

	public ClassicResumeMode(Context context, float width, float height,
			float unit, int x, int y) {
		super(context);
		mActivePointers = new SparseArray<PointF>();
		this.m_context = context;
		root = (ClassicResumeActivity) context;
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		mp_teleport = MediaPlayer.create(context,
				com.dpkm95.maze.R.raw.teleport);
		mp_key = MediaPlayer.create(context, com.dpkm95.maze.R.raw.key);
		mp_end = MediaPlayer.create(context, com.dpkm95.maze.R.raw.end);
		mp_transition = MediaPlayer.create(context,
				com.dpkm95.maze.R.raw.transit);
		mp_bump = MediaPlayer.create(context, com.dpkm95.maze.R.raw.bump);

		if (MazeConstants.TONE)
			mp_transition.start();
		this.unit = unit;
		this.x = x;
		this.y = y;
		this.W = width;
		this.H = height;
		mazeX = (width - (unit * 5 * x + unit)) / 2;
		mazeY = 2 * unit;
		mazeXf = mazeX + unit * 5 * x + unit;
		mazeYf = mazeY + unit * 5 * y + unit;
		teleX = iniX = ballX = mazeX + 3 * unit;
		teleY = iniY = ballY = mazeY + 3 * unit;
		destfX = mazeX + 5 * unit * destX + 3 * unit;
		destfY = mazeY + 5 * (unit + 2) * destY + 3 * unit;
		tradius = unit;
		mg = new MazeGenerator(x, y);
		keys = new Stack();
		try {
			Object[] o = Archiver.get_game_state(m_context, root);
			maze = (int[][]) o[0];
			int[][] k = (int[][]) o[1];
			for (int i = 0; i < k.length; ++i)
				keys.insert(k[i][0], k[i][1]);
			destX = (int) o[2];
			destY = (int) o[3];
			key_count = (int) o[4];
			key_score = (int) o[5];
			life_score = (int) o[6];
			ballX = (float) o[7];
			ballY = (float) o[8];
			teleX = (float) o[9];
			teleY = (float) o[10];
			teleport = (boolean) o[11];
			life_number = (int) o[12];

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		rX = retDestX = destX;
		rY = retDestY = destY;
	}

	// super class method called when invalidate(), it renders the graphics
	@Override
	public void onDraw(Canvas canvas) {
		switch (draw) {
		case STATE_PLAY:
			paintMaze(canvas);
			paintBackgroundColor(canvas);

			paintControlLine(canvas);
			paintPointers(canvas);

			paintDestination(canvas);
			paintKeys(canvas);

			paintBall(canvas);
			break;
		case STATE_CRASH:
			if (MazeConstants.VIBRATION)
				vibrator.vibrate(pattern_crash, -1);
			paintLoading(canvas);
			if (life_score != 1) {
				if (MazeConstants.TONE)
					mp_end.start();
				paintCrash(canvas);
				MazeConstants.RESUMABLE=false;
				if (archive) {
					Archiver.save_classic_score(root, key_score);
					archive = false;
				}
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						root.finish();
					}
				}, 1500);
			} else {
				if (MazeConstants.TONE)
					mp_bump.start();
				life_score = -1 * life_number++;
				restoreBall();
			}
			break;
		case STATE_WIN:
			if (MazeConstants.TONE)
				mp_transition.start();
			if (MazeConstants.VIBRATION)
				vibrator.vibrate(pattern_win, -1);
			nextMaze(canvas);
			break;
		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!hasFocus)
			Log.d("a there", Integer.toString(keys.getSize()));
		try {
			Stack k = new Stack();
			k.copy(keys);
			Archiver.save_game_state(m_context, root, maze, destX, destY, k,
					key_count, key_score, life_score, ballX, ballY, teleX,
					teleY, teleport, life_number);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void paintMaze(Canvas canvas) {
		paint.setColor(Color.rgb(0, 162, 232));
		paint.setStrokeWidth(unit);
		float px = mazeX, py = mazeY;
		for (int i = 0; i < y; i++) {
			// print horizontal lines
			for (int j = 0; j < x; j++) {
				if ((maze[j][i] & 1) == 0) {
					if (checkCollision(px, py, px + 5 * unit, py + unit))
						draw = STATE_CRASH;
					canvas.drawRect(px, py, px + 5 * unit, py + unit, paint);
					px += 5 * unit;
				} else {
					if (checkCollision(px, py, px + unit, py + unit))
						draw = STATE_CRASH;
					canvas.drawRect(px, py, px + unit, py + unit, paint);
					px += 5 * unit;
				}
			}
			canvas.drawRect(px, py, px + unit, py + unit, paint);
			px = mazeX;
			// print vertical lines
			for (int j = 0; j < x; j++) {
				if ((maze[j][i] & 8) == 0) {
					if (checkCollision(px, py, px + unit, py + 5 * unit))
						draw = STATE_CRASH;
					canvas.drawRect(px, py, px + unit, py + 5 * unit, paint);
					px += 5 * unit;
				} else {
					px += 5 * unit;
				}
			}
			if (checkCollision(px, py, px + unit, py + 5 * unit))
				draw = STATE_CRASH;
			canvas.drawRect(px, py, px + unit, py + 5 * unit, paint);
			py += 5 * unit;
			px = mazeX;
		}
		// print bottom line
		if (checkCollision(px, py, px + 5 * x * unit + unit, py + unit))
			draw = STATE_CRASH;
		canvas.drawRect(px, py, px + 5 * x * unit + unit, py + unit, paint);
	}

	public boolean checkCollision(float px, float py, float pxf, float pyf) {
		if (ballX > px - unit / 2 && ballX < pxf + unit / 2
				&& ballY > py - unit / 2 && ballY < pyf + unit / 2)
			return true;
		return false;
	}

	// paints the non-maze part of screen
	private void paintBackgroundColor(Canvas canvas) {
		paint.setColor(Color.rgb(0, 162, 232));
		canvas.drawRect(0, 0, mazeX, H, paint);
		canvas.drawRect(0, 0, W, mazeY, paint);
		canvas.drawRect(mazeXf, mazeY, W, H, paint);
		canvas.drawRect(mazeX, mazeYf, W, H, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(3 * unit);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		canvas.drawText("Score:", W - 10 * unit, H / 4 - 4 * unit, paint);
		canvas.drawText(Integer.toString(key_score), W - 7 * unit, H / 4, paint);
		canvas.drawText("Lives:", W - 10 * unit, H / 2, paint);
		canvas.drawText(Integer.toString(life_score), W - 7 * unit, H / 2 + 4
				* unit, paint);
		// Teleport location
		if (teleport) {
			paint.setColor(Color.GRAY);
			canvas.drawCircle(teleX, teleY, unit, paint);
		}
	}

	// paints line on which pointer is placed
	private void paintControlLine(Canvas canvas) {
		paint.setColor(Color.rgb(153, 217, 234));
		paint.setStrokeWidth(unit);

		// left control line
		canvas.drawLine(mazeX - 3 * unit, mazeY + unit, mazeX - 3 * unit,
				mazeYf - unit, paint);
		// bottom control line
		canvas.drawLine(mazeX + unit, mazeYf + 3 * unit, mazeXf - unit, mazeYf
				+ 3 * unit, paint);
	}

	// paints the pointers which show position of player
	private void paintPointers(Canvas canvas) {
		paint.setColor(Color.GRAY);
		// left control-line pointer
		canvas.drawCircle(mazeX - 3 * unit, ballY, unit / 2, paint);
		// bottom control-line pointer
		canvas.drawCircle(ballX, mazeYf + 3 * unit, unit / 2, paint);
	}

	private void paintDestination(Canvas canvas) {
		paint.setColor(Color.rgb(200, 200, 200));
		canvas.drawCircle(mazeX + 5 * unit * destX + 3 * unit, mazeY + 5 * unit
				* destY + 3 * unit, unit, paint);
	}

	// method to paint the remaining keys at end-points
	private void paintKeys(Canvas canvas) {
		keys = checkKeyStatus(keys);
		Node key = keys.top();
		// Log.d("blmost there--",);
		paint.setColor(Color.rgb(255, 208, 47));
		while (key != null) {
			canvas.drawCircle(mazeX + 5 * unit * key.getX() + 3 * unit, mazeY
					+ 5 * unit * key.getY() + 3 * unit, unit, paint);
			key = key.getNext();
		}
	}

	// checks if the ball collides with any of the remaining-keys
	private Stack checkKeyStatus(Stack keys) {
		Node key = keys.top();
		while (key != null) {
			if (ballX < mazeX + 5 * unit * key.getX() + 5 * unit
					&& ballX > mazeX + 5 * unit * key.getX() + 1 * unit
					&& ballY < mazeY + 5 * unit * key.getY() + 5 * unit
					&& ballY > mazeY + 5 * unit * key.getY() + 1 * unit) {
				--key_count;
				if (MazeConstants.TONE)
					mp_key.start();
				if (life_score != 1)
					++life_score;
				else
					++key_score;
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

	private void paintBall(Canvas canvas) {
		if (ballX < mazeX + 5 * unit * destX + 4 * unit
				&& ballX > mazeX + 5 * unit * destX + 2 * unit
				&& ballY < mazeY + 5 * unit * destY + 4 * unit
				&& ballY > mazeY + 5 * unit * destY + 2 * unit
				&& key_count == 0) {

			restoreX = destX;
			restoreY = destY;
			draw = STATE_WIN;
		}
		paint.setColor(Color.GRAY);
		canvas.drawCircle(ballX, ballY, unit, paint);
	}

	private void paintLoading(Canvas canvas) {
		paint.setColor(Color.rgb(0, 162, 232));
		canvas.drawRect(0, 0, W, H, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(5 * unit);
		paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
				"fonts/gisha.ttf"));
		canvas.drawText("Loading...", (W - 10 * 3 * unit) / 2, H / 2, paint);
	}

	private void restoreBall() {
		draw = STATE_PLAY;
		ballX = mazeX + 5 * unit * restoreX + 3 * unit;
		ballY = mazeY + 5 * unit * restoreY + 3 * unit;
		invalidate();
	}

	private void paintCrash(Canvas canvas) {
		if (MazeConstants.VIBRATION)
			vibrator.vibrate(pattern_win, -1);
		paint.setColor(Color.rgb(255, 145, 70));
		canvas.drawRect(0, 0, W, H, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(5 * unit);
		paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
				"fonts/gisha.ttf"));
		canvas.drawText("Nasty bump!", (W - 11 * 3 * unit) / 2, H / 2, paint);

	}

	private void nextMaze(Canvas canvas) {
		draw = STATE_PLAY;
		paintLoading(canvas);
		mg = new MazeGenerator(x, y);
		maze = mg.getMaze();
		lpf = new LongestPathFinder(maze, x, y);
		retPath = lpf.getLongestPath();
		keys = lpf.getEndPoints();
		keys.push(0, 0);

		key_count = keys.getSize();
		destX = retPath.topX();
		destY = retPath.topY();
		rX = retDestX = destX;
		rY = retDestY = destY;
		teleX = ballX;
		teleY = ballY;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pointerIndex = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIndex);
		int maskedAction = event.getActionMasked();
		
		switch (maskedAction) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN: {
			if (event.getX() > (ballX - 3 * unit)
					&& event.getX() < (ballX + 3 * unit)
					&& event.getY() > (ballY - 3 * unit)
					&& event.getY() < (ballY + 3 * unit)) {
				teleport = !teleport;
				if (teleport) {
					if (MazeConstants.TONE)
						mp_teleport.start();
					teleX = ballX;
					teleY = ballY;
				} else {
					if (MazeConstants.TONE)
						mp_transition.start();
					ballX = teleX;
					ballY = teleY;
				}
				break;
			}

			if (event.getX() > ballX - 1.2 * unit
					&& event.getX() < ballX + 1.2 * unit
					|| event.getY() > ballY - 1.2 * unit
					&& event.getY() < ballY + 1.2 * unit) {
				if (event.getY() < mazeY
						&& (event.getX() > mazeX && event.getX() < mazeXf))
					ballX = event.getX();
				if (event.getX() < mazeY
						&& (event.getY() > mazeY && event.getY() < mazeYf))
					ballY = event.getY();
				PointF f = new PointF();
				f.x = event.getX(pointerIndex);
				f.y = event.getY(pointerIndex);
				mActivePointers.put(pointerId, f);
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			for (int size = event.getPointerCount(), i = 0; i < size; i++) {
				PointF point = mActivePointers.get(event.getPointerId(i));
				if (point != null) {
					if (event.getX(i) > ballX - 1.2 * unit
							&& event.getX(i) < ballX + 1.2 * unit)
						ballX = event.getX(i);
					if (event.getY(i) > ballY - 1.2 * unit
							&& event.getY(i) < ballY + 1.2 * unit)
						ballY = event.getY(i);
				}
			}

			break;
		}
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL: {
			mActivePointers.remove(pointerId);
			break;
		}
		}
		
		invalidate();
		return true;
	}

}
