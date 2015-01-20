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
import android.graphics.LightingColorFilter;
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
	private Paint paint, paint0, paint1, paint2, paint2i, paint3, paint3i;
	private float W, H;
	private float ballX,ballY,ballXf, ballYf;
	private int x, y;
	private int state = MazeConstants.STATE_PLAY;
	private float mazeX, mazeY, mazeXf, mazeYf,control_width;
	private float unit;
	private MazeGenerator mg;
	private int[][] maze;
	private LongestPathFinder lpf;
	private Stack retPath, keys;
	private int destX, destY;
	private int key_count,life_number;
	private int restoreX, restoreY, teleX, teleY;
	private boolean teleport, archive,new_high_score;
	private Pawn player;
	private ClassicResumeActivity root;
	private Context m_context;
	private GameControl up, down, left, right;
	private Vibrator vibrator;
	private MediaPlayer mp_teleport, mp_key, mp_transition, mp_end, mp_bump,mp_highscore;
	private long[] pattern_crash = { 50, 50, 50 };
	private long[] pattern_win = { 50, 500, 50 };

	public ClassicResumeMode(Context context) {
		super(context);
		this.m_context = context;
		root = (ClassicResumeActivity) context;
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
		archive = true;
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		mp_teleport = MediaPlayer.create(context,
				com.dpkm95.maze.R.raw.teleport);
		mp_key = MediaPlayer.create(context, com.dpkm95.maze.R.raw.key);
		mp_end = MediaPlayer.create(context, com.dpkm95.maze.R.raw.end);
		mp_transition = MediaPlayer.create(context,
				com.dpkm95.maze.R.raw.transit);		
		mp_bump = MediaPlayer.create(context, com.dpkm95.maze.R.raw.bump);
		mp_highscore = MediaPlayer.create(context, com.dpkm95.maze.R.raw.win);		
		
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
		
		if (MazeConstants.TONE)
			mp_transition.start();
		mazeX = (W - (unit * 5 * x + unit)) / 2;
		mazeY = unit;
		mazeXf = mazeX + unit * 5 * x + unit;
		mazeYf = mazeY + unit * 5 * y + unit;
		control_width = (H-mazeYf);
		player = new Pawn(0,0);
		player.fx = teleX = 0;
		player.fy = teleY = 0;
		key_count = 1;
		player.score = 0;
				
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
			player.life = (float) o[8];
			life_number = (int) o[9];
			teleX = (int) o[10];
			teleY = (int) o[11];
			teleport = (boolean) o[12];

		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		up = new GameControl(this, unit, 0, control_width);
		down = new GameControl(this, unit, 1,control_width);
		left = new GameControl(this, unit, 2,control_width);
		right = new GameControl(this, unit, 3,control_width);
	}

	// super class method called when invalidate(), it renders the graphics
	@Override
	public void onDraw(Canvas canvas) {
		switch (state) {
		case 0:
			paintCrash(canvas);
			break;
		case MazeConstants.STATE_PLAY:
			paintMaze(canvas);
			paintBackground(canvas);
			paintGameControls(canvas);
			paintDestination(canvas);
			paintKeys(canvas);
			if (state != MazeConstants.STATE_CRASH)
				paintPlayer(canvas);
			break;
		case MazeConstants.STATE_CRASH:
			if (MazeConstants.VIBRATION)
				vibrator.vibrate(pattern_crash, -1);
			paintLoading(canvas);
			if (Math.ceil(player.life) != 100) {
				if (Archiver.get_top_score(root) < player.score)
					new_high_score=true;
				if (MazeConstants.VIBRATION)
					vibrator.vibrate(pattern_win, -1);
				if (archive) {					
					Archiver.save_classic_score(root, player.score);															
					archive = false;
				}
				if (new_high_score) {
					if (MazeConstants.TONE)
						mp_highscore.start();
				} else {
					if (MazeConstants.TONE)
						mp_end.start();
				}
				paintCrash(canvas);
				MazeConstants.RESUMABLE = false;
				new Timer().schedule(new TimerTask() {
					public void run() {
						root.finish();
					}
				}, 1500);
			} else {
				if (MazeConstants.TONE)
					mp_bump.start();
				player.life -= life_number * (100 / (life_number + 1));
				++life_number;
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
				Archiver.save_game_state(m_context, root, maze, player.x,
						player.y, destX, destY, keys_copy, key_count,
						player.score, player.life, life_number, teleX, teleY,
						teleport);
				MazeConstants.RESUMABLE = true;
			} catch (IOException e) {
				e.printStackTrace();
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
	
	// paints the non-maze part of screen
	private void paintBackground(Canvas canvas) {		
		canvas.drawRect(0, 0, mazeX, H, paint1);
		canvas.drawRect(0, 0, W, mazeY, paint1);
		canvas.drawRect(mazeXf, mazeY, W, H, paint1);
		canvas.drawRect(mazeX, mazeYf, W, H, paint1);
		
		paint0.setTextSize(3 * unit);
		canvas.drawText("Score:", mazeXf + (W - mazeXf) / 2
				- (float) (5.5 * unit), mazeY+4*unit, paint0);
		canvas.drawText(Integer.toString(player.score), mazeXf + (W - mazeXf) / 2
				- (float) (5.5 * unit), mazeY+8*unit, paint0);
		canvas.drawText("Life:", mazeXf + (W - mazeXf) / 2
				- (float) (5.5 * unit), mazeY+16*unit, paint0);
		canvas.drawText(Integer.toString((int)player.life)+"%", mazeXf + (W - mazeXf) / 2
				- (float) (5.5 * unit), mazeY+20*unit, paint0);
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
				if (Math.ceil(player.life) != 100)
					player.life+=(100/life_number);
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
		paint.setColor(Color.rgb(255, 145, 70));
		canvas.drawRect(0, 0, W, H, paint);
		paint0.setTextSize(5 * unit);
		canvas.drawText("Nasty bump!", (W - 11 * 3 * unit) / 2, H / 2-3*unit, paint0);
		if(new_high_score)
			canvas.drawText("New High Score: "+Integer.toString(player.score), (W - 16 * 3 * unit) / 2, H / 2+3*unit, paint0);
		else
			canvas.drawText("Score: "+Integer.toString(player.score), (W - 11 * 3 * unit) / 2, H / 2+3*unit, paint0);
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
		teleport = false;
		teleX = player.x;
		teleY = player.y;
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
