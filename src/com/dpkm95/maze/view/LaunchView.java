package com.dpkm95.maze.view;

import com.dpkm95.maze.activity.ChallengeActivity;
import com.dpkm95.maze.activity.ClassicActivity;
import com.dpkm95.maze.activity.ClassicResumeActivity;
import com.dpkm95.maze.activity.ConnectActivity;
import com.dpkm95.maze.activity.DeviceListActivity;
import com.dpkm95.maze.activity.MainActivity;
import com.dpkm95.maze.bluetooth.BluetoothChatService;
import com.dpkm95.maze.utils.Archiver;
import com.dpkm95.maze.utils.MazeConstants;
import com.dpkm95.maze.R;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class LaunchView extends View {
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	private BluetoothAdapter mBluetoothAdapter = null;

	private SparseArray<PointF> mActivePointers = new SparseArray<PointF>();
	private static float classic_ix, classic_iy, classic_fx, classic_fy;
	private static float challenge_ix, challenge_iy, challenge_fx,
			challenge_fy;
	private static float duel_ix, duel_iy, duel_fx, duel_fy;
	private static float acheivements_ix, acheivements_iy, acheivements_fx,
			acheivements_fy;
	private static float instructions_ix, instructions_iy, instructions_fx,
			instructions_fy;
	private static float hot_b1_ix, hot_b1_iy, hot_b1_fx, hot_b1_fy;
	private static float hot_b2_ix, hot_b2_iy, hot_b2_fx, hot_b2_fy;
	private static float hot_b3_ix, hot_b3_iy, hot_b3_fx, hot_b3_fy;
	private static float settings_ix, settings_iy, settings_fx, settings_fy;
	private boolean bluetooth_pressed, discoverable_pressed, search_pressed;
	private boolean classic_pressed, resume_pressed, challenge_pressed,
			duel_pressed;
	private int a_widget_pressed = 1, i_widget_pressed = 1;
	private float unit, sel_w, hot_w;
	private Paint paint = new Paint();
	private float H, W;
	private MainActivity root;
	private Context m_context;
	private int selection = 1;

	public LaunchView(Context context) {
		super(context);
	}

	public LaunchView(Context m_context, float width, float height) {
		super(m_context);
		this.m_context = m_context;
		root = (MainActivity) m_context;
		Archiver.load_game_constants(root);

		W = width;
		H = height;
		unit = H / 64;
		sel_w = (60 * H) / 192;
		hot_w = (W - 6 * unit - 2 * sel_w) / 3;
		classic_ix = unit;
		classic_iy = unit;
		classic_fx = unit + sel_w;
		classic_fy = unit + sel_w;

		challenge_ix = 2 * unit + sel_w;
		challenge_iy = unit;
		challenge_fx = 2 * unit + 2 * sel_w;
		challenge_fy = unit + sel_w;

		duel_ix = unit;
		duel_iy = 2 * unit + sel_w;
		duel_fx = unit + sel_w;
		duel_fy = 2 * unit + 2 * sel_w;

		acheivements_ix = 2 * unit + sel_w;
		acheivements_iy = 2 * unit + sel_w;
		acheivements_fx = 2 * unit + 2 * sel_w;
		acheivements_fy = 2 * unit + 2 * sel_w;

		instructions_ix = unit;
		instructions_iy = 3 * unit + 2 * sel_w;
		instructions_fx = unit + sel_w;
		instructions_fy = 3 * unit + 3 * sel_w;

		settings_ix = 2 * unit + sel_w;
		settings_iy = 3 * unit + 2 * sel_w;
		settings_fx = 2 * unit + 2 * sel_w;
		settings_fy = 3 * unit + 3 * sel_w;

		hot_b1_ix = 3 * unit + 2 * sel_w;
		hot_b1_iy = unit;
		hot_b1_fx = hot_b1_ix + hot_w;
		hot_b1_fy = hot_b1_iy + hot_w / 2;

		hot_b2_ix = hot_b1_fx + unit;
		hot_b2_iy = unit;
		hot_b2_fx = hot_b2_ix + hot_w;
		hot_b2_fy = hot_b2_iy + hot_w / 2;

		hot_b3_ix = hot_b2_fx + unit;
		hot_b3_iy = unit;
		hot_b3_fx = hot_b3_ix + hot_w;
		hot_b3_fy = hot_b3_iy + hot_w / 2;
	}

	public void onDraw(Canvas canvas) {
		draw_options(canvas);
		render_widgets(canvas, selection);
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!hasFocus) {
			Archiver.save_game_constants(root, MazeConstants.SIZE,
					MazeConstants.DIFFICULTY, MazeConstants.TONE,
					MazeConstants.VIBRATION, MazeConstants.RESUMABLE);
		}
	}

	private void draw_options(Canvas canvas) {
		paint.setColor(Color.rgb(0, 162, 232));
		canvas.drawRect(0, 0, W, H, paint);

		paint.setColor(Color.WHITE);
		canvas.drawRect(classic_ix + unit, classic_iy + unit,
				classic_fx - unit, classic_fy - unit, paint);
		canvas.drawRect(challenge_ix + unit, challenge_iy + unit, challenge_fx
				- unit, challenge_fy - unit, paint);
		canvas.drawRect(duel_ix + unit, duel_iy + unit, duel_fx - unit, duel_fy
				- unit, paint);
		canvas.drawRect(acheivements_ix + unit, acheivements_iy + unit,
				acheivements_fx - unit, acheivements_fy - unit, paint);
		canvas.drawRect(instructions_ix + unit, instructions_iy + unit,
				instructions_fx - unit, instructions_fy - unit, paint);
		canvas.drawRect(settings_ix + unit, settings_iy + unit, settings_fx
				- unit, settings_fy - unit, paint);
		select_button(selection, canvas);

		Bitmap classic = BitmapFactory.decodeResource(getResources(),
				R.drawable.classic);
		Bitmap r_classic = getResizedBitmap(classic, (int) sel_w, (int) sel_w);
		canvas.drawBitmap(r_classic, classic_ix, classic_iy, null);
		Bitmap challenge = BitmapFactory.decodeResource(getResources(),
				R.drawable.challenge);
		Bitmap r_challenge = getResizedBitmap(challenge, (int) sel_w,
				(int) sel_w);
		canvas.drawBitmap(r_challenge, challenge_ix, challenge_iy, null);
		Bitmap duel = BitmapFactory.decodeResource(getResources(),
				R.drawable.duel);
		Bitmap r_duel = getResizedBitmap(duel, (int) sel_w, (int) sel_w);
		canvas.drawBitmap(r_duel, duel_ix, duel_iy, null);
		Bitmap acheivements = BitmapFactory.decodeResource(getResources(),
				R.drawable.acheivements);
		Bitmap r_acheivements = getResizedBitmap(acheivements, (int) sel_w,
				(int) sel_w);
		canvas.drawBitmap(r_acheivements, acheivements_ix, acheivements_iy,
				null);
		Bitmap instructions = BitmapFactory.decodeResource(getResources(),
				R.drawable.instructions);
		Bitmap r_instructions = getResizedBitmap(instructions, (int) sel_w,
				(int) sel_w);
		canvas.drawBitmap(r_instructions, instructions_ix, instructions_iy,
				null);
		Bitmap settings = BitmapFactory.decodeResource(getResources(),
				R.drawable.settings);
		Bitmap r_settings = getResizedBitmap(settings, (int) sel_w, (int) sel_w);
		canvas.drawBitmap(r_settings, settings_ix, settings_iy, null);
	}

	private void select_button(int selection, Canvas canvas) {
		paint.setColor(Color.rgb(0, 162, 232));
		switch (selection) {
		case 1:
			canvas.drawRect(classic_ix, classic_iy, classic_fx, classic_fy,
					paint);
			break;
		case 2:
			canvas.drawRect(challenge_ix, challenge_iy, challenge_fx,
					challenge_fy, paint);
			break;
		case 3:
			canvas.drawRect(duel_ix, duel_iy, duel_fx, duel_fy, paint);
			break;
		case 4:
			canvas.drawRect(acheivements_ix, acheivements_iy, acheivements_fx,
					acheivements_fy, paint);
			break;
		case 5:
			canvas.drawRect(instructions_ix, instructions_iy, instructions_fx,
					instructions_fy, paint);
			break;
		case 6:
			canvas.drawRect(settings_ix, settings_iy, settings_fx, settings_fy,
					paint);
			break;
		}
	}

	public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth,
			int bitmapHeight) {
		return Bitmap
				.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	private void render_widgets(Canvas canvas, int selection) {
		switch (selection) {
		case 1:
			paint.setColor(Color.rgb(153, 217, 234));
			canvas.drawRect(3 * unit + 2 * sel_w, unit, W - unit, unit
					+ (H - 3 * unit) / 2, paint);
			canvas.drawRect(3 * unit + 2 * sel_w,
					2 * unit + (H - 3 * unit) / 2, W - unit, H - unit, paint);

			paint.setTextSize(5 * unit);
			paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
					"fonts/gisha.ttf"));
			if (classic_pressed) {
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawText("New Game", 3 * unit + 2 * sel_w
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 4, 3
						* unit + (H - 3 * unit) / 4, paint);
			} else {
				paint.setColor(Color.WHITE);
				canvas.drawText("New Game", 3 * unit + 2 * sel_w
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 4, 3
						* unit + (H - 3 * unit) / 4, paint);
			}

			if (resume_pressed) {
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawText("  Resume", 3 * unit + 2 * sel_w
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 4, 4
						* unit + 3 * (H - 3 * unit) / 4, paint);
			} else {
				paint.setColor(Color.WHITE);
				canvas.drawText("  Resume", 3 * unit + 2 * sel_w
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 4, 4
						* unit + 3 * (H - 3 * unit) / 4, paint);
			}

			break;
		case 2:
			paint.setColor(Color.WHITE);
			canvas.drawRect(hot_b1_ix + unit, hot_b1_iy + unit, hot_b1_fx
					- unit, hot_b1_fy - unit, paint);
			canvas.drawRect(hot_b2_ix + unit, hot_b2_iy + unit, hot_b2_fx
					- unit, hot_b2_fy - unit, paint);
			canvas.drawRect(hot_b3_ix + unit, hot_b3_iy + unit, hot_b3_fx
					- unit, hot_b3_fy - unit, paint);
			switch (MazeConstants.DIFFICULTY) {
			case 1:
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b1_ix, hot_b1_iy, hot_b1_fx, hot_b1_fy,
						paint);
				break;
			case 2:
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b2_ix, hot_b2_iy, hot_b2_fx, hot_b2_fy,
						paint);
				break;
			case 3:
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b3_ix, hot_b3_iy, hot_b3_fx, hot_b3_fy,
						paint);

				break;
			}
			Bitmap easy = BitmapFactory.decodeResource(getResources(),
					R.drawable.easy);
			Bitmap r_easy = getResizedBitmap(easy, (int) hot_w, (int) hot_w / 2);
			canvas.drawBitmap(r_easy, hot_b1_ix, hot_b1_iy, null);

			Bitmap normal = BitmapFactory.decodeResource(getResources(),
					R.drawable.normal);
			Bitmap r_normal = getResizedBitmap(normal, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_normal, hot_b2_ix, hot_b2_iy, null);

			Bitmap hard = BitmapFactory.decodeResource(getResources(),
					R.drawable.hard);
			Bitmap r_hard = getResizedBitmap(hard, (int) hot_w, (int) hot_w / 2);
			canvas.drawBitmap(r_hard, hot_b3_ix, hot_b3_iy, null);

			paint.setColor(Color.rgb(153, 217, 234));
			canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit, H - unit,
					paint);

			paint.setColor(Color.WHITE);
			paint.setTextSize(5 * unit);
			paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
					"fonts/gisha.ttf"));
			if (challenge_pressed) {
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawText("  Start", hot_b1_ix
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 3,
						hot_b1_fy + 3 * unit + (H - 5 * unit - hot_w / 2) / 2,
						paint);
			} else {
				paint.setColor(Color.WHITE);
				canvas.drawText("  Start", hot_b1_ix
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 3,
						hot_b1_fy + 3 * unit + (H - 5 * unit - hot_w / 2) / 2,
						paint);
			}

			break;
		case 3:
			paint.setColor(Color.WHITE);
			canvas.drawRect(hot_b1_ix + unit, hot_b1_iy + unit, hot_b1_fx
					- unit, hot_b1_fy - unit, paint);
			canvas.drawRect(hot_b2_ix + unit, hot_b2_iy + unit, hot_b2_fx
					- unit, hot_b2_fy - unit, paint);
			canvas.drawRect(hot_b3_ix + unit, hot_b3_iy + unit, hot_b3_fx
					- unit, hot_b3_fy - unit, paint);
			if (bluetooth_pressed) {
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b1_ix, hot_b1_iy, hot_b1_fx, hot_b1_fy,
						paint);
			}
			if (discoverable_pressed) {
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b2_ix, hot_b2_iy, hot_b2_fx, hot_b2_fy,
						paint);
			}
			if (search_pressed) {
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b3_ix, hot_b3_iy, hot_b3_fx, hot_b3_fy,
						paint);
			}
			Bitmap bluetooth = BitmapFactory.decodeResource(getResources(),
					R.drawable.bluetooth);
			Bitmap r_bluetooth = getResizedBitmap(bluetooth, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_bluetooth, hot_b1_ix, hot_b1_iy, null);

			Bitmap discoverable = BitmapFactory.decodeResource(getResources(),
					R.drawable.discoverable);
			Bitmap r_discoverable = getResizedBitmap(discoverable, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_discoverable, hot_b2_ix, hot_b2_iy, null);

			Bitmap search = BitmapFactory.decodeResource(getResources(),
					R.drawable.search);
			Bitmap r_search = getResizedBitmap(search, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_search, hot_b3_ix, hot_b3_iy, null);

			paint.setColor(Color.rgb(153, 217, 234));
			canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit, H - unit,
					paint);
			paint.setTextSize(5 * unit);
			paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
					"fonts/gisha.ttf"));

			if (duel_pressed) {
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawText("  Start", hot_b1_ix
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 3,
						hot_b1_fy + 3 * unit + (H - 5 * unit - hot_w / 2) / 2,
						paint);
			} else {
				paint.setColor(Color.WHITE);
				canvas.drawText("  Start", hot_b1_ix
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 3,
						hot_b1_fy + 3 * unit + (H - 5 * unit - hot_w / 2) / 2,
						paint);
			}

			break;
		case 4:
			paint.setColor(Color.WHITE);
			canvas.drawRect(hot_b1_ix + unit, hot_b1_iy + unit, hot_b1_fx
					- unit, hot_b1_fy - unit, paint);
			canvas.drawRect(hot_b2_ix + unit, hot_b2_iy + unit, hot_b2_fx
					- unit, hot_b2_fy - unit, paint);
			canvas.drawRect(hot_b3_ix + unit, hot_b3_iy + unit, hot_b3_fx
					- unit, hot_b3_fy - unit, paint);
			switch (a_widget_pressed) {
			case 1:
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b1_ix, hot_b1_iy, hot_b1_fx, hot_b1_fy,
						paint);
				break;
			case 2:
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b2_ix, hot_b2_iy, hot_b2_fx, hot_b2_fy,
						paint);
				break;
			case 3:
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b3_ix, hot_b3_iy, hot_b3_fx, hot_b3_fy,
						paint);

				break;
			}
			Bitmap aclassic = BitmapFactory.decodeResource(getResources(),
					R.drawable.a_classic);
			Bitmap r_aclassic = getResizedBitmap(aclassic, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_aclassic, hot_b1_ix, hot_b1_iy, null);

			Bitmap achallenge = BitmapFactory.decodeResource(getResources(),
					R.drawable.a_challenge);
			Bitmap r_achallenge = getResizedBitmap(achallenge, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_achallenge, hot_b2_ix, hot_b2_iy, null);

			Bitmap aduel = BitmapFactory.decodeResource(getResources(),
					R.drawable.a_duel);
			Bitmap r_aduel = getResizedBitmap(aduel, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_aduel, hot_b3_ix, hot_b3_iy, null);

			paint.setColor(Color.rgb(153, 217, 234));
			canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit, H - unit,
					paint);

			switch (a_widget_pressed) {
			case 1:
				paint.setColor(Color.WHITE);
				paint.setTextSize(4 * unit);
				paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
						"fonts/gisha.ttf"));
				int[] scores = Archiver.get_classic_scores(root);
				paint.setColor(Color.WHITE);
				canvas.drawText("Classic mode top scores:", hot_b1_ix + 2
						* unit, hot_b1_fy + 6 * unit, paint);
				paint.setColor(Color.rgb(0, 162, 232));
				for (int i = 0; i < 5; ++i) {
					canvas.drawText(i + 1 + ") " + Integer.toString(scores[i]),
							hot_b1_ix + hot_w / 4, hot_b1_fy + (12 + 5 * i)
									* unit, paint);
				}
				paint.setTextSize(18 * unit);
				int i = 0,
				j = scores[0];
				while (j > 0) {
					j /= 10;
					i++;
				}
				canvas.drawText(Integer.toString(scores[0]), W - i
						* (hot_w / 2), settings_fy, paint);
				break;
			case 2:
				paint.setColor(Color.WHITE);
				paint.setTextSize(4 * unit);
				paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
						"fonts/gisha.ttf"));
				int[] ch_scores = Archiver.get_challenge_scores(root);
				paint.setColor(Color.WHITE);
				canvas.drawText("Challenge mode records:",
						hot_b1_ix + 2 * unit, hot_b1_fy + 6 * unit, paint);
				paint.setTextSize(3 * unit);
				canvas.drawText("Easy", hot_b1_ix + 4 * unit, hot_b1_fy + 12
						* unit, paint);
				canvas.drawText("Normal", hot_b1_ix + 4 * unit, hot_b1_fy + 24
						* unit, paint);
				canvas.drawText("Hard", hot_b1_ix + 4 * unit, hot_b1_fy + 36
						* unit, paint);

				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawText("won     :  " + Integer.toString(ch_scores[0]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 16 * unit, paint);
				canvas.drawText("played :  " + Integer.toString(ch_scores[1]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 20 * unit, paint);
				canvas.drawText("won     :  " + Integer.toString(ch_scores[2]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 28 * unit, paint);
				canvas.drawText("played :  " + Integer.toString(ch_scores[3]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 32 * unit, paint);
				canvas.drawText("won     :  " + Integer.toString(ch_scores[4]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 40 * unit, paint);
				canvas.drawText("played :  " + Integer.toString(ch_scores[5]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 44 * unit, paint);

				paint.setTextSize(8 * unit);
				i = 0;
				j = ch_scores[0];
				while (j > 0) {
					j /= 10;
					i++;
				}
				canvas.drawText(Integer.toString(ch_scores[0]), W - i
						* (hot_w / 4) - unit, hot_b1_fy + 20 * unit, paint);

				paint.setTextSize(12 * unit);
				i = 0;
				j = ch_scores[4];
				while (j > 0) {
					j /= 10;
					i++;
				}
				canvas.drawText(Integer.toString(ch_scores[2]), W - i
						* (hot_w / 3) - unit, hot_b1_fy + 34 * unit, paint);

				paint.setTextSize(16 * unit);
				i = 0;
				j = ch_scores[4];
				while (j > 0) {
					j /= 10;
					i++;
				}
				canvas.drawText(Integer.toString(ch_scores[4]), W - i
						* (hot_w / 2), settings_fy, paint);

				break;
			case 3:
				paint.setColor(Color.WHITE);
				paint.setTextSize(4 * unit);
				paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
						"fonts/gisha.ttf"));
				canvas.drawText("Duel mode stats:", hot_b1_ix + 2 * unit,
						hot_b1_fy + 6 * unit, paint);
				paint.setTextSize(3 * unit);
				canvas.drawText("Initiated ", hot_b1_ix + 4 * unit,
						acheivements_iy + 2.5f * unit, paint);
				canvas.drawText("Accepted ", hot_b1_ix + 4 * unit, settings_iy
						+ 2.5f * unit, paint);

				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawText("won :  ", hot_b1_ix + 6 * unit,
						acheivements_iy + 6.5f * unit, paint);
				canvas.drawText("keys :  ", hot_b1_ix + 6 * unit,
						acheivements_iy + 10.5f * unit, paint);
				canvas.drawText("won :  ", hot_b1_ix + 6 * unit, settings_iy
						+ 6.5f * unit, paint);
				canvas.drawText("keys :  ", hot_b1_ix + 6 * unit, settings_iy
						+ 10.5f * unit, paint);
				// Bluetooth scores(I'll handle this)
				// paint.setTextSize(12*unit);
				// i=0;j=10;
				// while(j>0){
				// j/=10;
				// i++;
				// }
				// canvas.drawText(Integer.toString(10),W-i*(hot_w/3),acheivements_fy-8*unit,
				// paint);
				//
				// paint.setTextSize(12*unit);
				// i=0;j=175;
				// while(j>0){
				// j/=10;
				// i++;
				// }
				// canvas.drawText(Integer.toString(175),W-i*(hot_w/3),settings_fy-8*unit,
				// paint);
				break;
			}

			break;
		case 5:
			paint.setColor(Color.WHITE);
			canvas.drawRect(hot_b1_ix + unit, hot_b1_iy + unit, hot_b1_fx
					- unit, hot_b1_fy - unit, paint);
			canvas.drawRect(hot_b2_ix + unit, hot_b2_iy + unit, hot_b2_fx
					- unit, hot_b2_fy - unit, paint);
			canvas.drawRect(hot_b3_ix + unit, hot_b3_iy + unit, hot_b3_fx
					- unit, hot_b3_fy - unit, paint);
			switch (i_widget_pressed) {
			case 1:
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b1_ix, hot_b1_iy, hot_b1_fx, hot_b1_fy,
						paint);
				break;
			case 2:
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b2_ix, hot_b2_iy, hot_b2_fx, hot_b2_fy,
						paint);
				break;
			case 3:
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawRect(hot_b3_ix, hot_b3_iy, hot_b3_fx, hot_b3_fy,
						paint);

				break;
			}
			Bitmap iclassic = BitmapFactory.decodeResource(getResources(),
					R.drawable.a_classic);
			Bitmap r_iclassic = getResizedBitmap(iclassic, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_iclassic, hot_b1_ix, hot_b1_iy, null);

			Bitmap ichallenge = BitmapFactory.decodeResource(getResources(),
					R.drawable.a_challenge);
			Bitmap r_ichallenge = getResizedBitmap(ichallenge, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_ichallenge, hot_b2_ix, hot_b2_iy, null);

			Bitmap iduel = BitmapFactory.decodeResource(getResources(),
					R.drawable.a_duel);
			Bitmap r_iduel = getResizedBitmap(iduel, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_iduel, hot_b3_ix, hot_b3_iy, null);

			paint.setColor(Color.rgb(153, 217, 234));
			canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit, H - unit,
					paint);
			paint.setTextSize(3 * unit);
			paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
					"fonts/gisha.ttf"));
			switch (i_widget_pressed) {
			case 1:
				paint.setColor(Color.WHITE);
				canvas.drawText("Classic mode:", hot_b1_ix + 2 * unit,
						hot_b1_fy + 5 * unit, paint);
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawText("Grey dot - player", hot_b1_ix + 2 * unit,
						hot_b1_fy + 11 * unit, paint);
				canvas.drawText("Yellow dot - key", hot_b1_ix + 2 * unit,
						hot_b1_fy + 14 * unit, paint);
				canvas.drawText("Move grey dot using two fingers", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 17 * unit, paint);
				canvas.drawText("Use bottom region for horizontal motion",
						hot_b1_ix + 2 * unit, hot_b1_fy + 20 * unit, paint);
				canvas.drawText("Use left region for vertical motion",
						hot_b1_ix + 2 * unit, hot_b1_fy + 23 * unit, paint);
				canvas.drawText("Do NOT touch the walls of the maze", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 26 * unit, paint);
				canvas.drawText("Collect yellow keys to increment score/life",
						hot_b1_ix + 2 * unit, hot_b1_fy + 32 * unit, paint);
				canvas.drawText("Lives get costlier each time you loose one",
						hot_b1_ix + 2 * unit, hot_b1_fy + 35 * unit, paint);
				canvas.drawText("Score increases only if you have a life",
						hot_b1_ix + 2 * unit, hot_b1_fy + 38 * unit, paint);
				canvas.drawText("Touch on grey dot to set a teleport point",
						hot_b1_ix + 2 * unit, hot_b1_fy + 44 * unit, paint);
				canvas.drawText("Touch on it again to teleport back!",
						hot_b1_ix + 2 * unit, hot_b1_fy + 47 * unit, paint);
				break;
			case 2:
				paint.setColor(Color.WHITE);
				canvas.drawText("Challenge mode:", hot_b1_ix + 2 * unit,
						hot_b1_fy + 5 * unit, paint);
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawText("Grey dot - player", hot_b1_ix + 2 * unit,
						hot_b1_fy + 11 * unit, paint);
				canvas.drawText("Orange dot - opponent(pc)", hot_b1_ix + 2
						* unit, hot_b1_fy + 14 * unit, paint);
				canvas.drawText("Move grey dot using two fingers", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 17 * unit, paint);
				canvas.drawText("Use bottom region for horizontal motion",
						hot_b1_ix + 2 * unit, hot_b1_fy + 20 * unit, paint);
				canvas.drawText("Use left region for vertical motion",
						hot_b1_ix + 2 * unit, hot_b1_fy + 23 * unit, paint);
				canvas.drawText("Do NOT touch the walls of the maze", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 26 * unit, paint);
				canvas.drawText("Your destination is opponent's initial point",
						hot_b1_ix + 2 * unit, hot_b1_fy + 32 * unit, paint);
				canvas.drawText("Opponent's destination is your initial point",
						hot_b1_ix + 2 * unit, hot_b1_fy + 35 * unit, paint);
				canvas.drawText("The challenge starts once you start!",
						hot_b1_ix + 2 * unit, hot_b1_fy + 38 * unit, paint);
				break;
			case 3:
				paint.setColor(Color.WHITE);
				canvas.drawText("Duel mode:", hot_b1_ix + 2 * unit, hot_b1_fy
						+ 5 * unit, paint);
				paint.setColor(Color.rgb(0, 162, 232));
				canvas.drawText("Grey dot - player", hot_b1_ix + 2 * unit,
						hot_b1_fy + 11 * unit, paint);
				canvas.drawText("Orange dot - opponent", hot_b1_ix + 2 * unit,
						hot_b1_fy + 14 * unit, paint);
				canvas.drawText("Move grey dot using two fingers", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 17 * unit, paint);
				canvas.drawText("Use bottom region for horizontal motion",
						hot_b1_ix + 2 * unit, hot_b1_fy + 20 * unit, paint);
				canvas.drawText("Use left region for vertical motion",
						hot_b1_ix + 2 * unit, hot_b1_fy + 23 * unit, paint);
				canvas.drawText("Do NOT touch the walls of the maze", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 26 * unit, paint);
				canvas.drawText("Enable bluetooth", hot_b1_ix + 2 * unit,
						hot_b1_fy + 32 * unit, paint);
				canvas.drawText("Make device discoverable", hot_b1_ix + 2
						* unit, hot_b1_fy + 35 * unit, paint);
				canvas.drawText("Search for opponent device", hot_b1_ix + 2
						* unit, hot_b1_fy + 38 * unit, paint);
				canvas.drawText("Select opponent device to start duel!",
						hot_b1_ix + 2 * unit, hot_b1_fy + 41 * unit, paint);
				break;
			}
			break;
		case 6:
			paint.setColor(Color.rgb(0, 162, 232));
			canvas.drawRect(hot_b2_ix + unit, hot_b2_iy + unit, hot_b2_fx
					- unit, hot_b2_fy - unit, paint);
			canvas.drawRect(hot_b3_ix + unit, hot_b3_iy + unit, hot_b3_fx
					- unit, hot_b3_fy - unit, paint);
			if (MazeConstants.SIZE) {
				Bitmap large = BitmapFactory.decodeResource(getResources(),
						R.drawable.large);
				Bitmap r_large = getResizedBitmap(large, (int) hot_w,
						(int) hot_w / 2);
				canvas.drawBitmap(r_large, hot_b1_ix, hot_b1_iy, null);
			} else {
				Bitmap small = BitmapFactory.decodeResource(getResources(),
						R.drawable.small);
				Bitmap r_small = getResizedBitmap(small, (int) hot_w,
						(int) hot_w / 2);
				canvas.drawBitmap(r_small, hot_b1_ix, hot_b1_iy, null);
			}

			if (!MazeConstants.TONE) {
				paint.setColor(Color.WHITE);
				canvas.drawRect(hot_b2_ix + unit, hot_b2_iy + unit, hot_b2_fx
						- unit, hot_b2_fy - unit, paint);
			}
			if (!MazeConstants.VIBRATION) {
				paint.setColor(Color.WHITE);
				canvas.drawRect(hot_b3_ix + unit, hot_b3_iy + unit, hot_b3_fx
						- unit, hot_b3_fy - unit, paint);
			}

			Bitmap tone = BitmapFactory.decodeResource(getResources(),
					R.drawable.tone);
			Bitmap r_tone = getResizedBitmap(tone, (int) hot_w, (int) hot_w / 2);
			canvas.drawBitmap(r_tone, hot_b2_ix, hot_b2_iy, null);

			Bitmap vibration = BitmapFactory.decodeResource(getResources(),
					R.drawable.vibration);
			Bitmap r_vibration = getResizedBitmap(vibration, (int) hot_w,
					(int) hot_w / 2);
			canvas.drawBitmap(r_vibration, hot_b3_ix, hot_b3_iy, null);

			paint.setColor(Color.rgb(153, 217, 234));
			canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit, H - unit,
					paint);
			paint.setColor(Color.WHITE);
			paint.setTextSize(3 * unit);
			paint.setTypeface(Typeface.createFromAsset(root.getAssets(),
					"fonts/gisha.ttf"));
			canvas.drawText(" Developed by:", hot_b1_ix
					+ (W - 4 * unit - 2 * sel_w) / 2 - unit * 14, hot_b1_fy + 6
					* unit + (H - unit - hot_w) / 2 - 5 * unit, paint);
			paint.setColor(Color.rgb(0, 162, 232));
			canvas.drawText("adityaphilip", hot_b1_ix
					+ (W - 4 * unit - 2 * sel_w) / 2 - unit * 10, hot_b1_fy + 6
					* unit + (H - unit - hot_w) / 2, paint);
			canvas.drawText("dpkm95", hot_b1_ix + (W - 4 * unit - 2 * sel_w)
					/ 2 - unit * 10, hot_b1_fy + 6 * unit + (H - unit - hot_w)
					/ 2 + 5 * unit, paint);
			break;
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		int pointerIndex = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIndex);
		int maskedAction = event.getActionMasked();

		switch (maskedAction) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN: {

			if (classic_ix < event.getX() && event.getX() < classic_fx
					&& classic_iy < event.getY() && event.getY() < classic_fy)
				selection = 1;
			if (challenge_ix < event.getX() && event.getX() < challenge_fx
					&& challenge_iy < event.getY()
					&& event.getY() < challenge_fy)
				selection = 2;
			if (duel_ix < event.getX() && event.getX() < duel_fx
					&& duel_iy < event.getY() && event.getY() < duel_fy)
				selection = 3;
			if (acheivements_ix < event.getX()
					&& event.getX() < acheivements_fx
					&& acheivements_iy < event.getY()
					&& event.getY() < acheivements_fy)
				selection = 4;
			if (instructions_ix < event.getX()
					&& event.getX() < instructions_fx
					&& instructions_iy < event.getY()
					&& event.getY() < instructions_fy)
				selection = 5;
			if (settings_ix < event.getX() && event.getX() < settings_fx
					&& settings_iy < event.getY() && event.getY() < settings_fy)
				selection = 6;

			switch (selection) {
			case 1:
				if (3 * unit + 2 * sel_w < event.getX()
						&& event.getX() < W - unit && unit < event.getY()
						&& event.getY() < unit + (H - 3 * unit) / 2) {
					classic_pressed = true;
				}
				if (3 * unit + 2 * sel_w < event.getX()
						&& event.getX() < W - unit
						&& 2 * unit + (H - 3 * unit) / 2 < event.getY()
						&& event.getY() < H - unit) {
					resume_pressed = true;
				}
				break;
			case 2:
				if (hot_b1_ix < event.getX() && event.getX() < hot_b1_fx
						&& hot_b1_iy < event.getY() && event.getY() < hot_b1_fy)
					MazeConstants.DIFFICULTY = 1;
				if (hot_b2_ix < event.getX() && event.getX() < hot_b2_fx
						&& hot_b2_iy < event.getY() && event.getY() < hot_b2_fy)
					MazeConstants.DIFFICULTY = 2;
				if (hot_b3_ix < event.getX() && event.getX() < hot_b3_fx
						&& hot_b3_iy < event.getY() && event.getY() < hot_b3_fy)
					MazeConstants.DIFFICULTY = 3;
				if (hot_b1_ix < event.getX() && event.getX() < W - unit
						&& hot_b1_fy + unit < event.getY()
						&& event.getY() < H - unit) {
					challenge_pressed = true;
				}
				break;
			case 3:
				if (hot_b1_ix < event.getX() && event.getX() < hot_b1_fx
						&& hot_b1_iy < event.getY() && event.getY() < hot_b1_fy)
					bluetooth_pressed = true;
				if (hot_b2_ix < event.getX() && event.getX() < hot_b2_fx
						&& hot_b2_iy < event.getY() && event.getY() < hot_b2_fy
						&& bluetooth_pressed)
					discoverable_pressed = true;
				if (hot_b3_ix < event.getX() && event.getX() < hot_b3_fx
						&& hot_b3_iy < event.getY() && event.getY() < hot_b3_fy
						&& discoverable_pressed)
					search_pressed = true;
				if (hot_b1_ix < event.getX() && event.getX() < W - unit
						&& hot_b1_fy + unit < event.getY()
						&& event.getY() < H - unit && search_pressed)
					duel_pressed = true;
				break;
			case 4:
				if (hot_b1_ix < event.getX() && event.getX() < hot_b1_fx
						&& hot_b1_iy < event.getY() && event.getY() < hot_b1_fy)
					a_widget_pressed = 1;
				if (hot_b2_ix < event.getX() && event.getX() < hot_b2_fx
						&& hot_b2_iy < event.getY() && event.getY() < hot_b2_fy)
					a_widget_pressed = 2;
				if (hot_b3_ix < event.getX() && event.getX() < hot_b3_fx
						&& hot_b3_iy < event.getY() && event.getY() < hot_b3_fy)
					a_widget_pressed = 3;
				break;
			case 5:
				if (hot_b1_ix < event.getX() && event.getX() < hot_b1_fx
						&& hot_b1_iy < event.getY() && event.getY() < hot_b1_fy)
					i_widget_pressed = 1;
				if (hot_b2_ix < event.getX() && event.getX() < hot_b2_fx
						&& hot_b2_iy < event.getY() && event.getY() < hot_b2_fy)
					i_widget_pressed = 2;
				if (hot_b3_ix < event.getX() && event.getX() < hot_b3_fx
						&& hot_b3_iy < event.getY() && event.getY() < hot_b3_fy)
					i_widget_pressed = 3;
				break;
			case 6:
				if (hot_b1_ix < event.getX() && event.getX() < hot_b1_fx
						&& hot_b1_iy < event.getY() && event.getY() < hot_b1_fy) {
					MazeConstants.SIZE = !MazeConstants.SIZE;
					MazeConstants.RESUMABLE = false;
				}
				if (hot_b2_ix < event.getX() && event.getX() < hot_b2_fx
						&& hot_b2_iy < event.getY() && event.getY() < hot_b2_fy)
					MazeConstants.TONE = !MazeConstants.TONE;
				if (hot_b3_ix < event.getX() && event.getX() < hot_b3_fx
						&& hot_b3_iy < event.getY() && event.getY() < hot_b3_fy)
					MazeConstants.VIBRATION = !MazeConstants.VIBRATION;
				break;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP: {
			switch (selection) {
			case 1:
				if (3 * unit + 2 * sel_w < event.getX()
						&& event.getX() < W - unit && unit < event.getY()
						&& event.getY() < unit + (H - 3 * unit) / 2
						&& classic_pressed == true) {
					classic_pressed = false;
					Intent i = new Intent(m_context, ClassicActivity.class);
					root.startActivity(i);
				}
				if (3 * unit + 2 * sel_w < event.getX()
						&& event.getX() < W - unit
						&& 2 * unit + (H - 3 * unit) / 2 < event.getY()
						&& event.getY() < H - unit && resume_pressed == true) {
					resume_pressed = false;
					if (MazeConstants.RESUMABLE) {
						Intent i = new Intent(m_context,
								ClassicResumeActivity.class);
						root.startActivity(i);
					}
				}
				break;
			case 2:
				if (hot_b1_ix < event.getX() && event.getX() < W - unit
						&& hot_b1_fy + unit < event.getY()
						&& event.getY() < H - unit && challenge_pressed == true) {
					challenge_pressed = false;
					Intent i = new Intent(m_context, ChallengeActivity.class);
					root.startActivity(i);
				}
				break;
			case 3:
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

				// If the adapter is null, then Bluetooth is not supported
				if (mBluetoothAdapter == null)
					Toast.makeText(root, "Bluetooth is not available",
							Toast.LENGTH_LONG).show();
				else {

				}
				Intent i = new Intent(m_context, ConnectActivity.class);
				root.startActivity(i);
				if (hot_b1_ix < event.getX() && event.getX() < hot_b1_fx
						&& hot_b1_iy < event.getY() && event.getY() < hot_b1_fy
						&& bluetooth_pressed) {
					// code to enable bluetooth
					if (!mBluetoothAdapter.isEnabled()) {
						Intent enableIntent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						root.startActivityForResult(enableIntent,
								REQUEST_ENABLE_BT);
						// Otherwise, setup the chat session
					} else {
						/*if (ConnectActivity.mChatService == null)
							// setupChat();
							ConnectActivity.mChatService = new BluetoothChatService(root,
									ConnectActivity.mHandler);*/
					}
				}
				if (hot_b2_ix < event.getX() && event.getX() < hot_b2_fx
						&& hot_b2_iy < event.getY() && event.getY() < hot_b2_fy
						&& bluetooth_pressed && discoverable_pressed) {
					// code to make device discoverable
					if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
						Intent discoverableIntent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
						discoverableIntent.putExtra(
								BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
								300);
						root.startActivity(discoverableIntent);
					}
				}
				if (hot_b3_ix < event.getX() && event.getX() < hot_b3_fx
						&& hot_b3_iy < event.getY() && event.getY() < hot_b3_fy
						&& bluetooth_pressed && search_pressed) {
					// code to switch to another listview screen to choose
					// opponent
					Intent serverIntent = new Intent(root,
							DeviceListActivity.class);
					root.startActivityForResult(serverIntent,
							REQUEST_CONNECT_DEVICE);
				}
				if (hot_b1_ix < event.getX() && event.getX() < W - unit
						&& hot_b1_fy + unit < event.getY()
						&& event.getY() < H - unit && bluetooth_pressed
						&& duel_pressed) {
					Intent serverIntent = new Intent(root,
							ConnectActivity.class);
					root.startActivityForResult(serverIntent,
							REQUEST_CONNECT_DEVICE);
				}
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			}
			challenge_pressed = false;
			classic_pressed = false;
			resume_pressed = false;
			duel_pressed = false;
			if (selection != 4 && selection != 5) {
				a_widget_pressed = 1;
				i_widget_pressed = 1;
			}
			if (selection != 3) {
				bluetooth_pressed = false;
				discoverable_pressed = false;
				search_pressed = false;
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			mActivePointers.remove(pointerId);
			break;
		}
		}

		invalidate();
		return true;
	}
}
