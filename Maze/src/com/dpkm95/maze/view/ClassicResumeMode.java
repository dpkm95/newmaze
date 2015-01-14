package com.dpkm95.maze.view;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dpkm95.maze.R;
import com.dpkm95.maze.activity.ClassicResumeActivity;
import com.dpkm95.maze.utils.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private SparseArray<PointF> mActivePointers = new SparseArray<PointF>();
	private Paint paint = new Paint();
	private float W, H;
	private float ballX,ballY,ballXf, ballYf;
	private int x, y;
	private int state = MazeConstants.STATE_PLAY;
	private float mazeX, mazeY, mazeXf, mazeYf, offset;
	private int lcl, bcl;
	private float unit;
	private MazeGenerator mg;
	private int[][] maze;
	private LongestPathFinder lpf;
	private Stack retPath, keys;
	private int destX, destY;
	private int key_count,life_number;
	private int restoreX, restoreY, teleX, teleY;
	private boolean teleport, archive;
	private Bitmap arrow;
	private Pawn player;
	private ClassicResumeActivity root;
	private Context m_context;
	private Vibrator vibrator;
	private MediaPlayer mp_teleport, mp_key, mp_transition, mp_end, mp_bump;
	private long[] pattern_crash = { 50, 50, 50 };
	private long[] pattern_win = { 50, 500, 50 };

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
		arrow = BitmapTransformer.getResizedBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.arrow), (int)(4*unit) , (int)(4*unit));

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
		offset = (mazeYf - mazeY) / 3;
		player = new Pawn(0,0);
		player.fx = teleX = 0;
		player.fy = teleY = 0;
		key_count = 1;
		player.score = 0;
		player.lives = 1;
		life_number = 0;
				
		mg = new MazeGenerator(x, y);
		keys = new Stack();
		try {
			Object[] o = Archiver.get_game_state(m_context, root);
			maze = (int[][]) o[0];
			int[][] k = (int[][]) o[1];
			for (int i = 0; i < k.length; ++i)
				keys.insert(k[i][0], k[i][1]);
			player.fx = player.x = (int) o[2];
			player.fy = player.y = (int) o[3];
			destX    = (int) o[4];
			destY    = (int) o[5];
			key_count = (int) o[6];
			player.score = (int) o[7];
			player.lives = (int) o[8];
			life_number = (int) o[9];
			teleX = (int) o[10];
			teleY = (int) o[11];
			teleport = (boolean) o[12];

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// super class method called when invalidate(), it renders the graphics
	@Override
	public void onDraw(Canvas canvas) {
		switch (state) {
		case MazeConstants.STATE_PLAY:
			paintMaze(canvas);
			paintBackground(canvas);
			paintControlLine(canvas);
			paintDestination(canvas);
			paintKeys(canvas);
			if (state != MazeConstants.STATE_CRASH)
				paintPlayer(canvas);
			break;
		case MazeConstants.STATE_CRASH:
			if (MazeConstants.VIBRATION)
				vibrator.vibrate(pattern_crash, -1);
			paintLoading(canvas);
			if (player.lives != 1) {
				if (MazeConstants.TONE)
					mp_end.start();
				paintCrash(canvas);
				MazeConstants.RESUMABLE=false;
				if (archive) {
					Archiver.save_classic_score(root, player.score);
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
				player.lives = -1 * life_number++;
				restoreBall();
			}
			break;
		case MazeConstants.STATE_WIN:
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
		try {
			Stack keys_copy = new Stack();
			keys_copy.copy(keys);
			Archiver.save_game_state(m_context, root, maze
					,player.x, player.y,destX, destY
					,keys_copy, key_count, player.score, player.lives, life_number
					,teleX, teleY, teleport);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void paintMaze(Canvas canvas) {
		paint.setColor(Color.rgb(0, 162, 232));
		paint.setStrokeWidth(unit);
		float px = mazeX, py = mazeY;
		for (int i = 0; i < y; i++) {
			// print horizontal lines
			for (int j = 0; j < x; j++) {
				if ((maze[j][i] & 1) == 0) {
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
			for (int j = 0; j < x; j++) {
				if ((maze[j][i] & 8) == 0) {
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
		for (int i = 0; i < x; ++i) {
			checkCollision(px + 5 * i * unit, py, false);
			canvas.drawRect(px + 5 * i * unit, py, px + 5 * (i + 1) * unit, py
					+ unit, paint);
		}
		canvas.drawRect(px + 5 * x * unit, py, px + 5 * x * unit + unit + unit,
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
				state = MazeConstants.STATE_CRASH;
			}
			// move left
			if (bcl == 1 && ballXf < px + unit && px + unit < ballX
					&& py < ballY && ballY < py + 4 * unit) {
				state = MazeConstants.STATE_CRASH;
			}
		} else {
			// move down
			if (lcl == 3 && ballY < py && py < ballYf && px < ballX
					&& ballX < px + 4 * unit) {
				state = MazeConstants.STATE_CRASH;
			}
			// move up
			if (lcl == 1 && ballYf < py + unit && py + unit < ballY
					&& px < ballX && ballX < px + 4 * unit) {
				state = MazeConstants.STATE_CRASH;
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
		canvas.drawText("Score:", mazeXf + (W - mazeXf) / 2
				- (float) (4.5 * unit), H / 4 - 4 * unit, paint);
		canvas.drawText(Integer.toString(player.score), mazeXf + (W - mazeXf) / 2
				- (float) (1.5 * unit), H / 4, paint);
		canvas.drawText("Lives:", mazeXf + (W - mazeXf) / 2
				- (float) (4.5 * unit), H / 2, paint);
		canvas.drawText(Integer.toString(player.lives), mazeXf + (W - mazeXf) / 2
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
		Bitmap down_arrow = BitmapTransformer.RotateBitmap(arrow,180);
		canvas.drawBitmap(down_arrow, mazeX - 5 * unit, mazeYf-4*unit, null);
		Bitmap left_arrow = BitmapTransformer.RotateBitmap(down_arrow,90);
		canvas.drawBitmap(left_arrow, mazeXf-3*offset-unit, mazeYf + unit, null);
		Bitmap right_arrow = BitmapTransformer.RotateBitmap(left_arrow, 180);
		canvas.drawBitmap(right_arrow, mazeXf-4*unit, mazeYf + unit, null);
		
		paint.setStrokeWidth(unit);
		canvas.drawLine(mazeX - 3 * unit, mazeY + (int)(1.5*unit), mazeX - 3 * unit,
				mazeYf - (int)(1.5*unit), paint);
		canvas.drawLine(mazeXf - 3 * offset+(int)(0.5*unit), mazeYf + 3 * unit, mazeXf-(int)(1.5*unit),
				mazeYf + 3 * unit, paint);
		paint.setColor(Color.rgb(0, 162, 232));
		switch (lcl) {
		case 1:
			
			canvas.drawRect(mazeX - 5 * unit, mazeY, mazeX - unit,
					mazeY + offset, paint);
			break;
		case 2:
			canvas.drawRect(mazeX - 5 * unit, mazeY + offset, mazeX - unit,
					mazeY + 2 * offset, paint);
			break;
		case 3:
			canvas.drawRect(mazeX - 5 * unit, mazeY + 2 * offset, mazeX - unit, mazeYf, paint);
			break;
		}
		switch (bcl) {
		case 1:
			canvas.drawRect(mazeXf - 3 * offset-unit, mazeYf + unit, mazeXf - 2
					* offset, mazeYf + 5 * unit, paint);
			break;
		case 2:
			canvas.drawRect(mazeXf - 2 * offset, mazeYf + unit, mazeXf
					- offset, mazeYf + 5 * unit, paint);
			break;
		case 3:
			canvas.drawRect(mazeXf - offset, mazeYf + unit, mazeXf,
					mazeYf + 5 * unit, paint);
			break;
		}
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
			if (player.x == key.getX() && player.y == key.getY()) {
				--key_count;
				if (MazeConstants.TONE)
					mp_key.start();
				if (player.lives != 1)
					++player.lives;
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
			state = MazeConstants.STATE_WIN;
		}
		paint.setColor(Color.GRAY);
		canvas.drawCircle(mazeX + 5 * unit * player.x + 3 * unit, mazeY + 5
				* unit * player.y + 3 * unit, unit, paint);
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
		state = MazeConstants.STATE_PLAY;
		player.fx = restoreX;
		player.fy = restoreY;
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
		state = MazeConstants.STATE_PLAY;
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
		teleX = player.x;
		teleY = player.y;
		invalidate();
	}

	public boolean onTouchEvent(MotionEvent event) {
		int pointerIndex = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIndex);
		int maskedAction = event.getActionMasked();
		switch (maskedAction) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			if (event.getX() > (ballX - 3 * unit)
					&& event.getX() < (ballX + 3 * unit)
					&& event.getY() > (ballY - 3 * unit)
					&& event.getY() < (ballY + 3 * unit)) {
				teleport = !teleport;
				if (teleport) {
					if (MazeConstants.TONE)
						mp_teleport.start();
					teleX = player.fx;
					teleY = player.fy;
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
						Log.d("up", "");
						lcl = 1;
					} else if (mazeY + offset < event.getY(i)
							&& event.getY(i) < mazeY + 2 * offset) {
						lcl = 2;
					} else if (mazeY + 2 * offset < event.getY(i)
							&& event.getY(i) < mazeYf) {
						Log.d("down", "");
						lcl = 3;
					}
				} else if (event.getY(i) > mazeYf && event.getX(i) > mazeX) {
					if (mazeXf - 3 * offset < event.getX(i)
							&& event.getX(i) < mazeXf - 2 * offset) {
						Log.d("left", "");
						bcl = 1;
					} else if (mazeXf - 2 * offset < event.getX(i)
							&& event.getX(i) < mazeXf - offset) {
						bcl = 2;
					} else if (mazeXf - offset < event.getX(i)
							&& event.getX(i) < mazeXf) {
						Log.d("right", "");
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
