package com.dpkm95.maze.view;

import com.dpkm95.maze.activity.ChallengeActivity;
import com.dpkm95.maze.activity.ClassicActivity;
import com.dpkm95.maze.activity.ClassicResumeActivity;
import com.dpkm95.maze.activity.ConnectActivity;
import com.dpkm95.maze.activity.DeviceListActivity;
import com.dpkm95.maze.activity.MainActivity;
import com.dpkm95.maze.bluetooth.BluetoothChatService;
import com.dpkm95.maze.utils.Archiver;
import com.dpkm95.maze.utils.BitmapTransformer;
import com.dpkm95.maze.utils.MazeConstants;
import com.dpkm95.maze.R;
import com.facebook.Session;
import com.facebook.widget.FacebookDialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class LaunchView extends View {
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	private BluetoothAdapter mBluetoothAdapter = null;

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
	private boolean bluetooth_pressed, discoverable_pressed, search_pressed,
			share_pressed, rate_pressed;
	private boolean classic_pressed, resume_pressed, challenge_pressed,
			duel_pressed;
	private int a_widget_pressed = 1, i_widget_pressed = 1;
	private float unit, sel_w, hot_w;
	private Paint paint, paint0, paint1, paint2, paint2i, paint3, paint3i;
	private float H, W;
	private MainActivity root;
	private Context m_context;
	private int selection = 1,touch_down_widget;
	private Bitmap classic, challenge, duel, achievements, instructions,
			settings, easy, normal, hard, bluetooth, discoverable, search,
			hot_classic, hot_challenge, hot_duel, large, small, tone,
			vibration, share, rate, theme;

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
			paint1.setColor(Color.rgb(166,165,163));
			paint2.setColor(Color.rgb(125, 125, 125));
			paint2i.setColorFilter(new LightingColorFilter(0x7D7D7D, 0));
			paint3i.setColorFilter(new LightingColorFilter(0xC9CACC, 0));
			paint3.setColor(Color.rgb(201, 202, 204));
			break;
		}

		classic = BitmapTransformer.getResizedBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.classic),
				(int) sel_w, (int) sel_w);
		challenge = BitmapTransformer.getResizedBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.challenge),
				(int) sel_w, (int) sel_w);
		duel = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.duel),
				(int) sel_w, (int) sel_w);
		achievements = BitmapTransformer.getResizedBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.achievements),
				(int) sel_w, (int) sel_w);
		instructions = BitmapTransformer.getResizedBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.instructions),
				(int) sel_w, (int) sel_w);
		settings = BitmapTransformer.getResizedBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.settings),
				(int) sel_w, (int) sel_w);
		easy = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.easy),
				(int) hot_w, (int) hot_w / 2);
		normal = BitmapTransformer
				.getResizedBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.normal), (int) hot_w, (int) hot_w / 2);
		hard = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.hard),
				(int) hot_w, (int) hot_w / 2);
		bluetooth = BitmapTransformer.getResizedBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.bluetooth),
				(int) hot_w, (int) hot_w / 2);
		discoverable = BitmapTransformer.getResizedBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.discoverable),
				(int) hot_w, (int) hot_w / 2);
		search = BitmapTransformer
				.getResizedBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.search), (int) hot_w, (int) hot_w / 2);
		hot_classic = BitmapTransformer.getResizedBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.a_classic),
				(int) hot_w, (int) hot_w / 2);
		hot_challenge = BitmapTransformer.getResizedBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.a_challenge),
				(int) hot_w, (int) hot_w / 2);
		hot_duel = BitmapTransformer
				.getResizedBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.a_duel), (int) hot_w, (int) hot_w / 2);
		large = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.large),
				(int) hot_w, (int) hot_w / 2);
		small = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.small),
				(int) hot_w, (int) hot_w / 2);
		tone = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.tone),
				(int) hot_w, (int) hot_w / 2);
		vibration = BitmapTransformer.getResizedBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.vibration),
				(int) hot_w, (int) hot_w / 2);
		share = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.share),
				(int) hot_w, (int) hot_w / 2);
		rate = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.rate),
				(int) hot_w, (int) hot_w / 2);
		theme = BitmapTransformer.getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.theme),
				(int) hot_w, (int) hot_w / 2);

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
					MazeConstants.VIBRATION, MazeConstants.RESUMABLE,
					MazeConstants.COLOR);
		}else{
			invalidate();
			Log.d("focus changed", "invalidated");
		}
	}

	private void changeColor() {
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
			paint1.setColor(Color.rgb(166,165,163));
			paint2.setColor(Color.rgb(125, 125, 125));
			paint2i.setColorFilter(new LightingColorFilter(0x7D7D7D, 0));
			paint3i.setColorFilter(new LightingColorFilter(0xC9CACC, 0));
			paint3.setColor(Color.rgb(201, 202, 204));
			break;

		}
	}

	private void draw_options(Canvas canvas) {
		canvas.drawRect(0, 0, W, H, paint1);
		canvas.drawRect(classic_ix + unit, classic_iy + unit,
				classic_fx - unit, classic_fy - unit, paint0);
		canvas.drawRect(challenge_ix + unit, challenge_iy + unit, challenge_fx
				- unit, challenge_fy - unit, paint0);
		canvas.drawRect(duel_ix + unit, duel_iy + unit, duel_fx - unit, duel_fy
				- unit, paint0);
		canvas.drawRect(acheivements_ix + unit, acheivements_iy + unit,
				acheivements_fx - unit, acheivements_fy - unit, paint0);
		canvas.drawRect(instructions_ix + unit, instructions_iy + unit,
				instructions_fx - unit, instructions_fy - unit, paint0);
		canvas.drawRect(settings_ix + unit, settings_iy + unit, settings_fx
				- unit, settings_fy - unit, paint0);
		select_button(selection, canvas);

		canvas.drawBitmap(classic, classic_ix, classic_iy, paint2i);
		canvas.drawBitmap(challenge, challenge_ix, challenge_iy, paint2i);
		canvas.drawBitmap(duel, duel_ix, duel_iy, paint2i);
		canvas.drawBitmap(achievements, acheivements_ix, acheivements_iy,
				paint2i);
		canvas.drawBitmap(instructions, instructions_ix, instructions_iy,
				paint2i);
		canvas.drawBitmap(settings, settings_ix, settings_iy, paint2i);
	}

	private void select_button(int selection, Canvas canvas) {
		switch (selection) {
		case 1:
			canvas.drawRect(classic_ix, classic_iy, classic_fx, classic_fy,
					paint1);
			break;
		case 2:
			canvas.drawRect(challenge_ix, challenge_iy, challenge_fx,
					challenge_fy, paint1);
			break;
		case 3:
			canvas.drawRect(duel_ix, duel_iy, duel_fx, duel_fy, paint1);
			break;
		case 4:
			canvas.drawRect(acheivements_ix, acheivements_iy, acheivements_fx,
					acheivements_fy, paint1);
			break;
		case 5:
			canvas.drawRect(instructions_ix, instructions_iy, instructions_fx,
					instructions_fy, paint1);
			break;
		case 6:
			canvas.drawRect(settings_ix, settings_iy, settings_fx, settings_fy,
					paint1);
			break;
		}
	}

	private void render_widgets(Canvas canvas, int selection) {
		switch (selection) {
		case 1:
			paint1.setTextSize(5 * unit);
			paint2.setTextSize(5 * unit);
			if (classic_pressed) {
				canvas.drawRect(3 * unit + 2 * sel_w, unit, W - unit, unit
						+ (H - 3 * unit) / 2, paint2);

				canvas.drawText("New Game", 3 * unit + 2 * sel_w
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 4, 3
						* unit + (H - 3 * unit) / 4, paint1);
			} else {
				canvas.drawRect(3 * unit + 2 * sel_w, unit, W - unit, unit
						+ (H - 3 * unit) / 2, paint3);

				canvas.drawText("New Game", 3 * unit + 2 * sel_w
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 4, 3
						* unit + (H - 3 * unit) / 4, paint2);
			}
			if(MazeConstants.RESUMABLE){
				if (resume_pressed) {
					canvas.drawRect(3 * unit + 2 * sel_w, 2 * unit + (H - 3 * unit)
							/ 2, W - unit, H - unit, paint2);
					canvas.drawText("  Resume", 3 * unit + 2 * sel_w
							+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 4, 4
							* unit + 3 * (H - 3 * unit) / 4, paint1);
				} else {
					canvas.drawRect(3 * unit + 2 * sel_w, 2 * unit + (H - 3 * unit)
							/ 2, W - unit, H - unit, paint3);
					canvas.drawText("  Resume", 3 * unit + 2 * sel_w
							+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 4, 4
							* unit + 3 * (H - 3 * unit) / 4, paint2);
				}
			}else{
				canvas.drawRect(3 * unit + 2 * sel_w, 2 * unit + (H - 3 * unit)
						/ 2, W - unit, H - unit, paint3);
				canvas.drawText("  Resume", 3 * unit + 2 * sel_w
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 4, 4
						* unit + 3 * (H - 3 * unit) / 4, paint1);
			}			
			break;
		case 2:
			paint1.setTextSize(5 * unit);
			paint2.setTextSize(5 * unit);
			switch (MazeConstants.DIFFICULTY) {
			case 1:
				canvas.drawRect(hot_b1_ix+unit, hot_b1_iy+unit, hot_b1_fx-unit, hot_b1_fy-unit,
						paint2);
				break;
			case 2:
				canvas.drawRect(hot_b2_ix+unit, hot_b2_iy+unit, hot_b2_fx-unit, hot_b2_fy-unit,
						paint2);
				break;
			case 3:
				canvas.drawRect(hot_b3_ix+unit, hot_b3_iy+unit, hot_b3_fx-unit, hot_b3_fy-unit,
						paint2);
				break;
			}

			canvas.drawBitmap(easy, hot_b1_ix, hot_b1_iy, paint3i);
			canvas.drawBitmap(normal, hot_b2_ix, hot_b2_iy, paint3i);
			canvas.drawBitmap(hard, hot_b3_ix, hot_b3_iy, paint3i);

			if (challenge_pressed) {
				canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit,
						H - unit, paint2);
				canvas.drawText("  Start", hot_b1_ix
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 3,
						hot_b1_fy + 3 * unit + (H - 5 * unit - hot_w / 2) / 2,
						paint1);
			} else {
				canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit,
						H - unit, paint3);
				canvas.drawText("  Start", hot_b1_ix
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 3,
						hot_b1_fy + 3 * unit + (H - 5 * unit - hot_w / 2) / 2,
						paint2);
			}

			break;
		case 3:
			paint1.setTextSize(5 * unit);
			paint2.setTextSize(5 * unit);
			if (bluetooth_pressed) {
				canvas.drawRect(hot_b1_ix+unit, hot_b1_iy+unit, hot_b1_fx-unit, hot_b1_fy-unit,
						paint2);
			}
			if (discoverable_pressed) {
				canvas.drawRect(hot_b2_ix+unit, hot_b2_iy+unit, hot_b2_fx-unit, hot_b2_fy-unit,
						paint2);
			}
			if (search_pressed) {
				canvas.drawRect(hot_b3_ix+unit, hot_b3_iy+unit, hot_b3_fx-unit, hot_b3_fy-unit,
						paint2);
			}

			canvas.drawBitmap(bluetooth, hot_b1_ix, hot_b1_iy, paint3i);
			canvas.drawBitmap(discoverable, hot_b2_ix, hot_b2_iy, paint3i);
			canvas.drawBitmap(search, hot_b3_ix, hot_b3_iy, paint3i);

			if (duel_pressed) {
				canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit,
						H - unit, paint2);
				canvas.drawText("  Start", hot_b1_ix
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 3,
						hot_b1_fy + 3 * unit + (H - 5 * unit - hot_w / 2) / 2,
						paint1);
			} else {
				canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit,
						H - unit, paint3);
				canvas.drawText("  Start", hot_b1_ix
						+ (W - 4 * unit - 2 * sel_w) / 2 - 3 * unit * 3,
						hot_b1_fy + 3 * unit + (H - 5 * unit - hot_w / 2) / 2,
						paint2);
			}

			break;
		case 4:
			switch (a_widget_pressed) {
			case 1:
				canvas.drawRect(hot_b1_ix+unit, hot_b1_iy+unit, hot_b1_fx-unit, hot_b1_fy-unit,
						paint2);
				break;
			case 2:
				canvas.drawRect(hot_b2_ix+unit, hot_b2_iy+unit, hot_b2_fx-unit, hot_b2_fy-unit,
						paint2);
				break;
			case 3:
				canvas.drawRect(hot_b3_ix+unit, hot_b3_iy+unit, hot_b3_fx-unit, hot_b3_fy-unit,
						paint2);

				break;
			}

			canvas.drawBitmap(hot_classic, hot_b1_ix, hot_b1_iy, paint3i);
			canvas.drawBitmap(hot_challenge, hot_b2_ix, hot_b2_iy, paint3i);
			canvas.drawBitmap(hot_duel, hot_b3_ix, hot_b3_iy, paint3i);

			canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit, H - unit,
					paint3);

			switch (a_widget_pressed) {
			case 1:
				paint2.setTextSize(4 * unit);
				int[] scores = Archiver.get_classic_scores(root);
				canvas.drawText("Classic mode top scores:", hot_b1_ix + 2
						* unit, hot_b1_fy + 6 * unit, paint2);
				for (int i = 0; i < 5; ++i) {
					canvas.drawText(i + 1 + ") " + Integer.toString(scores[i]),
							hot_b1_ix + hot_w / 4, hot_b1_fy + (12 + 5 * i)
									* unit, paint2);
				}
				paint1.setTextSize(18 * unit);
				int i = 0,
				j = scores[0];
				while (j > 0) {
					j /= 10;
					i++;
				}
				canvas.drawText(Integer.toString(scores[0]), W - i
						* (hot_w / 2), settings_fy, paint1);
				break;
			case 2:
				paint2.setTextSize(4 * unit);
				int[] ch_scores = Archiver.get_challenge_scores(root);
				canvas.drawText("Challenge mode records:",
						hot_b1_ix + 2 * unit, hot_b1_fy + 6 * unit, paint2);
				paint2.setTextSize(3 * unit);
				canvas.drawText("Easy", hot_b1_ix + 4 * unit, hot_b1_fy + 12
						* unit, paint2);
				canvas.drawText("Normal", hot_b1_ix + 4 * unit, hot_b1_fy + 24
						* unit, paint2);
				canvas.drawText("Hard", hot_b1_ix + 4 * unit, hot_b1_fy + 36
						* unit, paint2);

				canvas.drawText("won     :  " + Integer.toString(ch_scores[0]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 16 * unit, paint2);
				canvas.drawText("played :  " + Integer.toString(ch_scores[1]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 20 * unit, paint2);
				canvas.drawText("won     :  " + Integer.toString(ch_scores[2]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 28 * unit, paint2);
				canvas.drawText("played :  " + Integer.toString(ch_scores[3]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 32 * unit, paint2);
				canvas.drawText("won     :  " + Integer.toString(ch_scores[4]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 40 * unit, paint2);
				canvas.drawText("played :  " + Integer.toString(ch_scores[5]),
						hot_b1_ix + 6 * unit, hot_b1_fy + 44 * unit, paint2);

				paint1.setTextSize(8 * unit);
				i = 0;
				j = ch_scores[0];
				while (j > 0) {
					j /= 10;
					i++;
				}
				canvas.drawText(Integer.toString(ch_scores[0]), W - i
						* (hot_w / 4) - unit, hot_b1_fy + 20 * unit, paint1);

				paint1.setTextSize(12 * unit);
				i = 0;
				j = ch_scores[2];
				while (j > 0) {
					j /= 10;
					i++;
				}
				canvas.drawText(Integer.toString(ch_scores[2]), W - i
						* (hot_w / 3) - unit, hot_b1_fy + 34 * unit, paint1);

				paint1.setTextSize(16 * unit);
				i = 0;
				j = ch_scores[4];
				while (j > 0) {
					j /= 10;
					i++;
				}
				canvas.drawText(Integer.toString(ch_scores[4]), W - i
						* (hot_w / 2), settings_fy, paint1);

				break;
			case 3:
				paint2.setTextSize(4 * unit);
				canvas.drawText("Duel mode stats:", hot_b1_ix + 2 * unit,
						hot_b1_fy + 6 * unit, paint2);
				paint2.setTextSize(3 * unit);
				canvas.drawText("Initiated ", hot_b1_ix + 4 * unit,
						acheivements_iy + 2.5f * unit, paint2);
				canvas.drawText("Accepted ", hot_b1_ix + 4 * unit, settings_iy
						+ 2.5f * unit, paint2);

				canvas.drawText("won :  ", hot_b1_ix + 6 * unit,
						acheivements_iy + 6.5f * unit, paint2);
				canvas.drawText("keys :  ", hot_b1_ix + 6 * unit,
						acheivements_iy + 10.5f * unit, paint2);
				canvas.drawText("won :  ", hot_b1_ix + 6 * unit, settings_iy
						+ 6.5f * unit, paint2);
				canvas.drawText("keys :  ", hot_b1_ix + 6 * unit, settings_iy
						+ 10.5f * unit, paint2);
				paint1.setTextSize(12*unit);
				i=0;j=0;
				while(j>0){
					j/=10;
					i++;
				}
			    canvas.drawText(Integer.toString(0),W-i*(hot_w/3),acheivements_fy-8*unit, paint1);
				
				paint1.setTextSize(12*unit);
				i=0;j=0;
				while(j>0){
					j/=10;
					i++;
				}
			    canvas.drawText(Integer.toString(0),W-i*(hot_w/3),settings_fy-8*unit,paint1);
				break;
			}

			break;
		case 5:
			switch (i_widget_pressed) {
			case 1:
				canvas.drawRect(hot_b1_ix+unit, hot_b1_iy+unit, hot_b1_fx-unit, hot_b1_fy-unit,
						paint2);
				break;
			case 2:
				canvas.drawRect(hot_b2_ix+unit, hot_b2_iy+unit, hot_b2_fx-unit, hot_b2_fy-unit,
						paint2);
				break;
			case 3:
				canvas.drawRect(hot_b3_ix+unit, hot_b3_iy+unit, hot_b3_fx-unit, hot_b3_fy-unit,
						paint2);
			}

			canvas.drawBitmap(hot_classic, hot_b1_ix, hot_b1_iy, paint3i);
			canvas.drawBitmap(hot_challenge, hot_b2_ix, hot_b2_iy, paint3i);
			canvas.drawBitmap(hot_duel, hot_b3_ix, hot_b3_iy, paint3i);

			canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, W - unit, H - unit,
					paint3);
			
			switch (i_widget_pressed) {
			case 1:
				paint2.setTextSize(4 * unit);				
				canvas.drawText("Classic mode:", hot_b1_ix + 2 * unit,
						hot_b1_fy + 5 * unit, paint2);
				paint2.setTextSize(3 * unit);
				canvas.drawText("Grey dot - player", hot_b1_ix + 2 * unit,
						hot_b1_fy + 11 * unit, paint2);
				canvas.drawText("Yellow dot - key", hot_b1_ix + 2 * unit,
						hot_b1_fy + 14 * unit, paint2);
				canvas.drawText("Use arrow keys to navigate", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 17 * unit, paint2);
				canvas.drawText("Avoid crashing into wall", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 20 * unit, paint2);
				canvas.drawText("Collect keys to increment score/life",
						hot_b1_ix + 2 * unit, hot_b1_fy + 26 * unit, paint2);
				canvas.drawText("Life gets costlier with each loss",
						hot_b1_ix + 2 * unit, hot_b1_fy + 29 * unit, paint2);
				canvas.drawText("Score increases only with full life",
						hot_b1_ix + 2 * unit, hot_b1_fy + 32 * unit, paint2);
				canvas.drawText("Touch player to set a teleport point",
						hot_b1_ix + 2 * unit, hot_b1_fy + 35 * unit, paint2);
				canvas.drawText("Touch it again to teleport back!",
						hot_b1_ix + 2 * unit, hot_b1_fy + 38 * unit, paint2);
				break;
			case 2:
				paint2.setTextSize(4 * unit);
				canvas.drawText("Challenge mode:", hot_b1_ix + 2 * unit,
						hot_b1_fy + 5 * unit, paint2);
				paint2.setTextSize(3 * unit);
				canvas.drawText("Grey dot - player", hot_b1_ix + 2 * unit,
						hot_b1_fy + 11 * unit, paint2);
				canvas.drawText("Orange dot - opponent(pc)", hot_b1_ix + 2
						* unit, hot_b1_fy + 14 * unit, paint2);
				canvas.drawText("Use arrow keys to navigate", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 17 * unit, paint2);
				canvas.drawText("Avoid crashing into wall", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 20 * unit, paint2);
				canvas.drawText("Destination - opponent's start point",
						hot_b1_ix + 2 * unit, hot_b1_fy + 26 * unit, paint2);
				break;
			case 3:
				paint2.setTextSize(4 * unit);
				canvas.drawText("Duel mode:", hot_b1_ix + 2 * unit, hot_b1_fy
						+ 5 * unit, paint2);
				paint2.setTextSize(3 * unit);
				canvas.drawText("Grey dot - player", hot_b1_ix + 2 * unit,
						hot_b1_fy + 11 * unit, paint2);
				canvas.drawText("Orange dot - opponent", hot_b1_ix + 2 * unit,
						hot_b1_fy + 14 * unit, paint2);
				canvas.drawText("Use arrow keys to navigate", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 17 * unit, paint2);
				canvas.drawText("Avoid crashing into wall", hot_b1_ix
						+ 2 * unit, hot_b1_fy + 20 * unit, paint2);
				
				canvas.drawText("Enable bluetooth", hot_b1_ix + 2 * unit,
						hot_b1_fy + 26 * unit, paint2);
				canvas.drawText("Make device discoverable", hot_b1_ix + 2
						* unit, hot_b1_fy + 29 * unit, paint2);
				canvas.drawText("Search for opponent device", hot_b1_ix + 2
						* unit, hot_b1_fy + 32 * unit, paint2);
				canvas.drawText("Select opponent device, start duel!",
						hot_b1_ix + 2 * unit, hot_b1_fy + 35 * unit, paint2);
				break;
			}
			break;
		case 6:
			canvas.drawRect(hot_b1_ix + unit, hot_b1_iy + unit, hot_b1_fx
					- unit, hot_b1_fy - unit, paint2);
			if (MazeConstants.SIZE) {
				canvas.drawBitmap(large, hot_b1_ix, hot_b1_iy, paint3i);
			} else {
				canvas.drawBitmap(small, hot_b1_ix, hot_b1_iy, paint3i);
			}

			if (MazeConstants.TONE) {
				canvas.drawRect(hot_b2_ix + unit, hot_b2_iy + unit, hot_b2_fx
						- unit, hot_b2_fy - unit, paint2);
			}
			if (MazeConstants.VIBRATION) {
				canvas.drawRect(hot_b3_ix + unit, hot_b3_iy + unit, hot_b3_fx
						- unit, hot_b3_fy - unit, paint2);
			}
			canvas.drawBitmap(tone, hot_b2_ix, hot_b2_iy, paint3i);
			canvas.drawBitmap(vibration, hot_b3_ix, hot_b3_iy, paint3i);

			if (share_pressed) {
				canvas.drawRect(hot_b1_ix + unit, settings_fy - hot_w / 2
						+ unit, hot_b1_fx - unit, settings_fy - unit, paint2);
			}
			canvas.drawBitmap(share, hot_b1_ix, settings_fy - hot_w / 2,
					paint3i);

			if (rate_pressed) {
				canvas.drawRect(hot_b2_ix + unit, settings_fy - hot_w / 2
						+ unit, hot_b2_fx - unit, settings_fy - unit, paint2);
			}
			canvas.drawBitmap(rate, hot_b2_ix, settings_fy - hot_w / 2, paint3i);
			switch (MazeConstants.COLOR) {
			case 0:
				paint.setColor(Color.rgb(247, 172, 213));
				canvas.drawRect(hot_b3_ix + unit, settings_fy - hot_w / 2
						+ unit, W - 2 * unit, H - 2 * unit, paint);
				break;
			case 1:
				paint.setColor(Color.rgb(165, 134, 191));
				canvas.drawRect(hot_b3_ix + unit, settings_fy - hot_w / 2
						+ unit, W - 2 * unit, H - 2 * unit, paint);
				break;
			case 2:
				paint.setColor(Color.rgb(232, 208, 182));
				canvas.drawRect(hot_b3_ix + unit, settings_fy - hot_w / 2
						+ unit, W - 2 * unit, H - 2 * unit, paint);
				break;
			case 3:
				paint.setColor(Color.rgb(147, 149, 152));
				canvas.drawRect(hot_b3_ix + unit, settings_fy - hot_w / 2
						+ unit, W - 2 * unit, H - 2 * unit, paint);
				break;
			case 4:
				paint.setColor(Color.rgb(108, 185, 225));
				canvas.drawRect(hot_b3_ix + unit, settings_fy - hot_w / 2
						+ unit, W - 2 * unit, H - 2 * unit, paint);
				break;
			}
			canvas.drawBitmap(theme, hot_b3_ix, settings_fy - hot_w / 2,
					paint3i);
			canvas.drawRect(hot_b1_ix, hot_b1_fy + unit, hot_b3_fx, H - hot_w
					/ 2 - 2 * unit, paint3);
			paint2.setTextSize(3 * unit);
			canvas.drawText(" Developed by:", hot_b1_ix
					+ (W - 4 * unit - 2 * sel_w) / 2 - unit * 14, hot_b1_fy
					+ unit + (H - unit - hot_w) / 2 - 5 * unit, paint2);
			canvas.drawText("adithyaphilip", hot_b1_ix
					+ (W - 4 * unit - 2 * sel_w) / 2 - unit * 10, hot_b1_fy
					+ unit + (H - unit - hot_w) / 2, paint2);
			canvas.drawText("dpkm95", hot_b1_ix + (W - 4 * unit - 2 * sel_w)
					/ 2 - unit * 10, hot_b1_fy + unit + (H - unit - hot_w) / 2
					+ 5 * unit, paint2);
			break;
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
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
					touch_down_widget = 1;
				}
				if (3 * unit + 2 * sel_w < event.getX()
						&& event.getX() < W - unit
						&& 2 * unit + (H - 3 * unit) / 2 < event.getY()
						&& event.getY() < H - unit) {
					resume_pressed = true;
					touch_down_widget  = 2;
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
					touch_down_widget = 1;
				}
				break;
			case 3:
				if (hot_b1_ix < event.getX() && event.getX() < hot_b1_fx
						&& hot_b1_iy < event.getY() && event.getY() < hot_b1_fy)
					//bluetooth_pressed = true;
				if (hot_b2_ix < event.getX() && event.getX() < hot_b2_fx
						&& hot_b2_iy < event.getY() && event.getY() < hot_b2_fy
						&& bluetooth_pressed)
					//discoverable_pressed = true;
				if (hot_b3_ix < event.getX() && event.getX() < hot_b3_fx
						&& hot_b3_iy < event.getY() && event.getY() < hot_b3_fy
						&& discoverable_pressed)
					//search_pressed = true;
				if (hot_b1_ix < event.getX() && event.getX() < W - unit
						&& hot_b1_fy + unit < event.getY()
						&& event.getY() < H - unit && search_pressed){
					//duel_pressed = true;
					touch_down_widget = 1;
				}					
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
				if (hot_b1_ix < event.getX() && event.getX() < hot_b1_fx
						&& settings_fy - hot_w / 2 < event.getY()
						&& event.getY() < settings_fy) {
					share_pressed = true;

				}
				if (hot_b2_ix < event.getX() && event.getX() < hot_b2_fx
						&& settings_fy - hot_w / 2 < event.getY()
						&& event.getY() < settings_fy) {
					rate_pressed = true;

				}
				if (hot_b3_ix < event.getX() && event.getX() < hot_b3_fx
						&& settings_fy - hot_w / 2 < event.getY()
						&& event.getY() < H - unit) {
					MazeConstants.COLOR = (MazeConstants.COLOR + 1) % 5;
					changeColor();
				}
				break;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE:
			switch (selection) {
			case 1:
				if (3 * unit + 2 * sel_w < event.getX()
						&& event.getX() < W - unit && unit < event.getY()
						&& event.getY() < unit + (H - 3 * unit) / 2 
						&& touch_down_widget == 1) {
					classic_pressed = true;
				}
				if (3 * unit + 2 * sel_w < event.getX()
						&& event.getX() < W - unit
						&& 2 * unit + (H - 3 * unit) / 2 < event.getY()
						&& event.getY() < H - unit
						&& touch_down_widget == 2) {
					resume_pressed = true;
				}
				break;
			case 2:
				if (hot_b1_ix < event.getX() && event.getX() < W - unit
						&& hot_b1_fy + unit < event.getY()
						&& event.getY() < H - unit
						&& touch_down_widget == 1) {
					challenge_pressed = true;
				}
				break;
			case 3:
				if (hot_b1_ix < event.getX() && event.getX() < W - unit
						&& hot_b1_fy + unit < event.getY()
						&& event.getY() < H - unit && search_pressed
						&& touch_down_widget == 1)
					duel_pressed = true;
				break;			
			}
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
						
//						 if (ConnectActivity.mChatService == null) //
//						 setupChat(); ConnectActivity.mChatService = new
//						 BluetoothChatService(root, ConnectActivity.mHandler);
						 
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
				if (hot_b1_ix < event.getX() && event.getX() < hot_b1_fx
						&& settings_fy - hot_w / 2 < event.getY()
						&& event.getY() < settings_fy && share_pressed) {
					share_pressed = false;
					
					String[] items = {"Facebook", "Others"};
					final ArrayAdapter<String> adapter;					
					final AlertDialog.Builder listBuilder = new AlertDialog.Builder(root);
					listBuilder.setIcon(R.drawable.ic_launcher);
					listBuilder.setTitle("Share your top score via");
					adapter = new ArrayAdapter<String>(root, android.R.layout.select_dialog_singlechoice, items);
					listBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					}
					});
					listBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						switch(which){
						case 0:
							if(FacebookDialog.canPresentShareDialog(root, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)){
								root.shareFacebookDialog();
								
							}else{
								Toast.makeText(root,"Facebook app not available", Toast.LENGTH_SHORT).show();
								listBuilder.show();
							}
							break;
						case 1:
							Intent sendIntent = new Intent();
							 sendIntent.setAction(Intent.ACTION_SEND);
							 sendIntent.putExtra(Intent.EXTRA_TEXT,
							 "Maze Challenge:\nMy top score: "+Archiver.get_top_score(root)+"\nBeat that!\nhttp://play.google.com/store/apps/details?id="
							 + root.getPackageName());
							 sendIntent.setType("text/plain");
							 root.startActivity(sendIntent);
							break;
					}
					}
					});
					if(FacebookDialog.canPresentShareDialog(root, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)){
						listBuilder.show();
					}else{
						Intent sendIntent = new Intent();
						 sendIntent.setAction(Intent.ACTION_SEND);
						 sendIntent.putExtra(Intent.EXTRA_TEXT,
						 "Maze Challenge:\nMy top score: "+Archiver.get_top_score(root)+"\nBeat that!\nhttp://play.google.com/store/apps/details?id="
						 + root.getPackageName());
						 sendIntent.setType("text/plain");
						 root.startActivity(sendIntent);
					}
																			 
				}
				if (hot_b2_ix < event.getX() && event.getX() < hot_b2_fx
						&& settings_fy - hot_w / 2 < event.getY()
						&& event.getY() < settings_fy && rate_pressed) {
					rate_pressed = false;
					Uri uri = Uri.parse("market://details?id="
							+ root.getPackageName());
					Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
					try {
						root.startActivity(goToMarket);
					} catch (ActivityNotFoundException e) {
						root.startActivity(new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("http://play.google.com/store/apps/details?id="
										+ root.getPackageName())));
					}
				}
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
		case MotionEvent.ACTION_CANCEL:
		}

		invalidate();
		return true;
	}
}
