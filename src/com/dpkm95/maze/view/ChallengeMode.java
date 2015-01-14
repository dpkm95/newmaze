package com.dpkm95.maze.view;

import java.util.Timer;
import java.util.TimerTask;

import com.dpkm95.maze.R;
import com.dpkm95.maze.activity.ChallengeActivity;
import com.dpkm95.maze.utils.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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

@SuppressLint("DrawAllocation")
public class ChallengeMode extends View {
	private SparseArray<PointF> mActivePointers;
	private Paint paint = new Paint();
	private float W, H;
	private float ballX, ballY, ballXf, ballYf;
	private int x, y,delay=0;
	private int state = MazeConstants.STATE_PLAY;
	private float mazeX, mazeY, mazeXf, mazeYf, offset;
	private float unit;
	private MazeGenerator mg;
	private int[][] maze;
	private LongestPathFinder lpf;
	private Stack retPath, keys;
	private int destX, destY;
	private int pcSpeed, lcl, bcl;
	private boolean archive,play;
	private MediaPlayer mp_win, mp_end;
	private Bitmap arrow;
	private Pawn player, opponent;
	private Timer t1;
	private ChallengeActivity root;
	private Context m_context;
	private Vibrator vibrator;
	private long[] pattern = { 50, 500, 50 };

	public ChallengeMode(Context context) {
		super(context);
	}

	public ChallengeMode(Context context, float width, float height,
			float unit, int x, int y) {
		super(context);
		mActivePointers = new SparseArray<PointF>();
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		mp_win = MediaPlayer.create(context, com.dpkm95.maze.R.raw.win);
		mp_end = MediaPlayer.create(context, com.dpkm95.maze.R.raw.end);

		this.m_context = context;
		this.root = (ChallengeActivity) context;

		switch (MazeConstants.DIFFICULTY) {
		case 1:
			this.pcSpeed = 30;
			break;
		case 2:
			this.pcSpeed = 23;
			break;
		case 3:
			this.pcSpeed = 15;
			break;
		}
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
		archive = true;

		mg = new MazeGenerator(x, y);
		maze = mg.getMaze();
		lpf = new LongestPathFinder(maze, x, y);
		retPath = lpf.getLongestPath();
		keys = lpf.getEndPoints();

		destX = retPath.topX();
		destY = retPath.topY();

		player = new Pawn(0, 0);
		opponent = new Pawn(destX, destY);
		opponent.fx=destX;
		opponent.fy=destY;
		arrow = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.arrow),
				(int) (4 * unit), (int) (4 * unit));

//		t1=new Timer();
//		t1.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				if(play){
//					updateOpponent();
//					//root.forceInvalidate();
//				}
//			}
//		}, 0, 500);
	}

	public void onDraw(Canvas canvas) {
		switch (state) {
		case MazeConstants.STATE_PLAY:
			paintMaze(canvas);
			paintBackgroundColor(canvas);
			paintControlLine(canvas);
			paintOpponentDestination(canvas);
			paintDestination(canvas);
			paintOpponent(canvas);
			if (state != MazeConstants.STATE_CRASH)
				paintPlayer(canvas);		    
			break;
		case MazeConstants.STATE_CRASH:			
			if (MazeConstants.VIBRATION)
				vibrator.vibrate(pattern, -1);
			if (MazeConstants.TONE)
				mp_end.start();
			paintCrash(canvas);
			if (archive) {
				Archiver.save_challenge_score(root, m_context, 0);
				archive = false;
			}
			new Timer().schedule(new TimerTask() {
				public void run() {
					root.finish();
				}
			}, 1500);
			break;
		case MazeConstants.STATE_WIN:			
			if (archive) {				
				Archiver.save_challenge_score(root, m_context, 1);
				archive = false;
			}
			if (MazeConstants.VIBRATION)
				vibrator.vibrate(pattern, -1);
			if (MazeConstants.TONE)
				mp_win.start();
			paintWinner(canvas);
			new Timer().schedule(new TimerTask() {
				public void run() {
					root.finish();
				}
			}, 1500);
			break;
		case MazeConstants.STATE_LOSS:			
			if (archive) {
				Archiver.save_challenge_score(root, m_context, 0);
				archive = false;
			}
			if (MazeConstants.VIBRATION)
				vibrator.vibrate(pattern, -1);
			if (MazeConstants.TONE)
				mp_end.start();
			paintLoss(canvas);
			new Timer().schedule(new TimerTask() {
				public void run() {
					root.finish();
				}
			}, 1500);
			break;
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

	private void paintBackgroundColor(Canvas canvas) {
		paint.setColor(Color.rgb(0, 162, 232));
		canvas.drawRect(0, 0, mazeX, H, paint);
		canvas.drawRect(0, 0, W, mazeY, paint);
		canvas.drawRect(mazeXf, mazeY, W, H, paint);
		canvas.drawRect(mazeX, mazeYf, W, H, paint);
	}

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

	private void paintOpponentDestination(Canvas canvas) {
		paint.setColor(Color.rgb(255, 168, 111));
		canvas.drawCircle(mazeX + 3 * unit, mazeY + 3 * unit, unit, paint);
	}

	private void paintDestination(Canvas canvas) {
		paint.setColor(Color.rgb(200, 200, 200));
		canvas.drawCircle(mazeX + 5 * unit * destX + 3 * unit, mazeY + 5 * unit
				* destY + 3 * unit, unit, paint);
	}

	private void paintPlayer(Canvas canvas) {
		// reached end point
		player.x = player.fx;
		player.y = player.fy;
		if (player.x == destX && player.y == destY)
			state = MazeConstants.STATE_WIN;
		paint.setColor(Color.GRAY);
		canvas.drawCircle(mazeX + 5 * unit * player.x + 3 * unit, mazeY + 5
				* unit * player.y + 3 * unit, unit, paint);
	}

//	public void updateOpponent(){
//		retPath.pop();
//		opponent.x = retPath.topX();
//		opponent.y = retPath.topY();
//	}
//	
//	public void doInvalidate() {
//		invalidate();		
//	}
//	
//	private void paintOpponent(Canvas canvas) {		
//		if (retPath.isEmpty())
//			state = MazeConstants.STATE_LOSS;
//		else {
//			paint.setColor(Color.rgb(255, 127, 39));
//			canvas.drawCircle(mazeX + 5 * unit * opponent.x + 3 * unit,
//					mazeY + 5 * unit * opponent.y + 3 * unit, unit, paint);
//		}
//	}

	private void paintOpponent(Canvas canvas) {
		if (player.x!=0 || player.y!=0) {
			if (opponent.x == opponent.fx && opponent.y == opponent.fy) {
				retPath.pop();
				if (retPath.isEmpty())
					state = MazeConstants.STATE_LOSS;
				else {
					opponent.fx = retPath.topX();
					opponent.fy = retPath.topY();
				}
			}
			delay++;
			if (delay == pcSpeed) {
				opponent.x = opponent.fx;
				opponent.y = opponent.fy;
				delay = 0;
			}
		}
		paint.setColor(Color.rgb(255, 127, 39));
		canvas.drawCircle(mazeX + 5 * unit * opponent.x + 3 * unit, mazeY + 5 * unit
				* opponent.y + 3 * unit, unit, paint);
		invalidate();
	}

	private void paintCrash(Canvas canvas) {
		paint.setColor(Color.rgb(255, 145, 70));
		canvas.drawRect(0, 0, W, H, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(5 * unit);
		paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
				"fonts/gisha.ttf"));
		canvas.drawText("Nasty bump!", (W - 11 * 3 * unit) / 2, H / 2, paint);
	}

	private void paintWinner(Canvas canvas) {
		paint.setColor(Color.rgb(189, 233, 59));
		canvas.drawRect(0, 0, W, H, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(5 * unit);
		paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
				"fonts/gisha.ttf"));
		canvas.drawText("You Won!", (W - 8 * 3 * unit) / 2, H / 2, paint);
	}

	private void paintLoss(Canvas canvas) {
		paint.setColor(Color.rgb(154, 137, 211));
		canvas.drawRect(0, 0, W, H, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(5 * unit);
		paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
				"fonts/gisha.ttf"));
		canvas.drawText("   You Lost!", (W - 10 * 3 * unit) / 2, H / 2, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pointerIndex = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIndex);
		int maskedAction = event.getActionMasked();
		switch (maskedAction) {		
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			if (event.getX() < mazeX && event.getY() < mazeYf) {
				//play = true;
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
				//play = true;
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
				// PointF point = mActivePointers.get(event.getPointerId(i));
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
