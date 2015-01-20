package com.dpkm95.maze.view;

import java.util.Timer;
import java.util.TimerTask;

import com.dpkm95.maze.activity.ChallengeActivity;
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
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.dpkm95.maze.utils.MazeGenerator;

@SuppressLint("DrawAllocation")
public class ChallengeMode extends View {
	private Paint paint, paint0, paint1, paint2, paint2i, paint3, paint3i;
	private float W, H;
	private float ballX, ballY, ballXf, ballYf;
	private int x, y,delay=0;
	private int state = MazeConstants.STATE_PLAY;
	private float mazeX, mazeY, mazeXf, mazeYf,control_width;
	private float unit;
	private MazeGenerator mg;
	private int[][] maze;
	private LongestPathFinder lpf;
	private Stack retPath, fwPath, keys;
	private int destX, destY;
	private int oppSpeed,total_path_length;
	private boolean archive;
	private MediaPlayer mp_win, mp_end;
	private Pawn player, opponent;
	private ChallengeActivity root;
	private Context m_context;
	private GameControl up, down, left, right;
	private Vibrator vibrator;
	private long[] pattern = { 50, 500, 50 };

	public ChallengeMode(Context context) {
		super(context);
		this.m_context = context;
		this.root = (ChallengeActivity) context;

		W = getResources().getDisplayMetrics().widthPixels;
		H = getResources().getDisplayMetrics().heightPixels;

		if (MazeConstants.SIZE) {
			x = 16;
			y = 10;
			unit = (float) ((H * 0.8) / (y * 5));
		} else {
			x = 12;
			y = 8;
			unit = (float) ((H * 0.8) / (y * 5.5));
		}

		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		mp_win = MediaPlayer.create(context, com.dpkm95.maze.R.raw.win);
		mp_end = MediaPlayer.create(context, com.dpkm95.maze.R.raw.end);
		
		switch (MazeConstants.DIFFICULTY) {
		case 1:
			this.oppSpeed = 30;
			break;
		case 2:
			this.oppSpeed = 23;
			break;
		case 3:
			this.oppSpeed = 15;
			break;
		}
		mazeX = (W - (unit * 5 * x + unit)) / 2;
		mazeY = unit;
		mazeXf = mazeX + unit * 5 * x + unit;
		mazeYf = mazeY + unit * 5 * y + unit;
		control_width = (H-mazeYf);
		archive = true;

		mg = new MazeGenerator(x, y);
		maze = mg.getMaze();
		lpf = new LongestPathFinder(maze, x, y);
		retPath = lpf.getLongestPath();
		fwPath = new Stack();
		fwPath.copy(retPath);		
		fwPath.reverse();
		fwPath.pop();
		total_path_length = retPath.getSize(); 
		keys = lpf.getEndPoints();

		destX = retPath.topX();
		destY = retPath.topY();

		player = new Pawn(0, 0);
		opponent = new Pawn(destX, destY);
		opponent.fx=destX;
		opponent.fy=destY;
				
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
	}

	public void onDraw(Canvas canvas) {
		switch (state) {
		case MazeConstants.STATE_PLAY:
			paintMaze(canvas);
			paintBackground(canvas);
			paintGameControls(canvas);
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
		float px = mazeX, py = mazeY;
		for (int i = 0; i < y; i++) {
			// print horizontal lines
			for (int j = 0; j < x; j++) {
				if ((maze[j][i] & 1) == 0) {
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
			for (int j = 0; j < x; j++) {
				if ((maze[j][i] & 8) == 0) {
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
		for (int i = 0; i < x; ++i) {
			checkCollision(px + 5 * i * unit, py, false);
			canvas.drawRect(px + 5 * i * unit, py, px + 5 * (i + 1) * unit, py
					+ unit, paint1);
		}
		canvas.drawRect(px + 5 * x * unit, py, px + 5 * x * unit + unit,
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
				state = MazeConstants.STATE_CRASH;
			}
			// move left
			if (left.pressed && ballXf < px + unit && px + unit < ballX
					&& py < ballY && ballY < py + 4 * unit) {
				state = MazeConstants.STATE_CRASH;
			}
		} else {
			// move down
			if (down.pressed && ballY < py && py < ballYf && px < ballX
					&& ballX < px + 4 * unit) {
				state = MazeConstants.STATE_CRASH;
			}
			// move up
			if (up.pressed && ballYf < py + unit && py + unit < ballY
					&& px < ballX && ballX < px + 4 * unit) {
				state = MazeConstants.STATE_CRASH;
			}
		}
	}

	private void paintBackground(Canvas canvas) {
		canvas.drawRect(0, 0, mazeX, H, paint1);
		canvas.drawRect(0, 0, W, mazeY, paint1);
		canvas.drawRect(mazeXf, mazeY, W, H, paint1);
		canvas.drawRect(mazeX, mazeYf, W, H, paint1);
		paint0.setTextSize(3 * unit);
		canvas.drawText("Opp:", mazeXf + (W - mazeXf) / 2
				- (float) (4.5 * unit), mazeY + 4 * unit, paint0);
		canvas.drawText(Integer.toString((int)(opponent.path_covered*100/total_path_length))+"%", mazeXf
				+ (W - mazeXf) / 2 - (float) (4.5 * unit), mazeY + 8 * unit,
				paint0);
		if(MazeConstants.DIFFICULTY==1){
			canvas.drawText("Me:", mazeXf + (W - mazeXf) / 2
					- (float) (4.5 * unit), mazeY + 16 * unit, paint0);
			canvas.drawText(Integer.toString((int)(player.path_covered*100/total_path_length))+"%", mazeXf + (W - mazeXf)
					/ 2 - (float) (4.5 * unit), mazeY + 20 * unit, paint0);
		}
	}

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
		if(player.x == fwPath.topX() && player.y == fwPath.topY()){			
			player.path_covered+=1;			
			fwPath.pop();
		}
		if (player.x == destX && player.y == destY)
			state = MazeConstants.STATE_WIN;
		paint.setColor(Color.GRAY);
		canvas.drawCircle(mazeX + 5 * unit * player.x + 3 * unit, mazeY + 5
				* unit * player.y + 3 * unit, unit, paint);
	}

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
			if (delay == oppSpeed) {
				opponent.path_covered+=1;
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
		paint0.setTextSize(5 * unit);
		canvas.drawText("Nasty bump!", (W - 11 * 3 * unit) / 2, H / 2, paint0);
	}

	private void paintWinner(Canvas canvas) {
		paint.setColor(Color.rgb(189, 233, 59));
		canvas.drawRect(0, 0, W, H, paint);
		paint0.setTextSize(5 * unit);
		canvas.drawText("You Won!", (W - 8 * 3 * unit) / 2, H / 2, paint0);
	}

	private void paintLoss(Canvas canvas) {
		paint.setColor(Color.rgb(154, 137, 211));
		canvas.drawRect(0, 0, W, H, paint);
		paint0.setTextSize(5 * unit);		
		canvas.drawText("   You Lost!", (W - 10 * 3 * unit) / 2, H / 2, paint0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int maskedAction = event.getActionMasked();
		switch (maskedAction) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
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
