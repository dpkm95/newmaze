package com.dpkm95.maze.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import com.dpkm95.maze.activity.ChallengeActivity;
import com.dpkm95.maze.activity.ClassicActivity;
import com.dpkm95.maze.activity.ClassicResumeActivity;
import com.dpkm95.maze.activity.MainActivity;
import android.content.Context;

public class Archiver {
	public static void save_classic_score(ClassicActivity root, int score) {
		String scores = read_classic_score(root);
		List<String> score_list = Arrays.asList(scores.split(","));
		int[] temp = new int[6];
		for (int i = 0; i < 5; ++i) {
			temp[i] = Integer.parseInt(score_list.get(i));
		}

		for (int i = 0; i < 5; ++i) {
			if (temp[i] == score)
				return;
		}

		temp[5] = score;
		Arrays.sort(temp);

		String top_scores = "";
		for (int i = 1; i < 6; ++i) {
			top_scores += Integer.toString(temp[i]) + ",";
		}
		if (MazeConstants.SIZE) {
			try {
				FileOutputStream fOut = root.openFileOutput(
						"large_classic_scores", Context.MODE_PRIVATE);
				fOut.write(top_scores.getBytes());
				fOut.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				FileOutputStream fOut = root.openFileOutput(
						"small_classic_scores", Context.MODE_PRIVATE);
				fOut.write(top_scores.getBytes());
				fOut.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void save_challenge_score(ChallengeActivity root,
			Context m_context, int i) {
		String scores = read_challenge_score(root);
		List<String> score_list = Arrays.asList(scores.split(","));
		int[] temp = new int[6];
		for (int j = 0; j < 6; ++j) {
			temp[j] = Integer.parseInt(score_list.get(j));
		}
		switch (MazeConstants.DIFFICULTY) {
		case 1:
			if (i == 1)
				temp[0]++;
			temp[1]++;
			break;
		case 2:
			if (i == 1)
				temp[2]++;
			temp[3]++;
			break;
		case 3:
			if (i == 1)
				temp[4]++;
			temp[5]++;
			break;
		}
		String score_card = "";
		for (int j = 0; j < 6; ++j)
			score_card += Integer.toString(temp[j]) + ",";

		if (MazeConstants.SIZE) {
			try {
				FileOutputStream fOut = root.openFileOutput(
						"large_challenge_scores", Context.MODE_PRIVATE);
				fOut.write(score_card.getBytes());
				fOut.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				FileOutputStream fOut = root.openFileOutput(
						"small_challenge_scores", Context.MODE_PRIVATE);
				fOut.write(score_card.getBytes());
				fOut.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static String read_challenge_score(ChallengeActivity root) {
		if (MazeConstants.SIZE) {
			try {
				FileInputStream fin = root
						.openFileInput("large_challenge_scores");
				int c;
				String temp = "";
				while ((c = fin.read()) != -1) {
					temp = temp + Character.toString((char) c);
				}
				fin.close();
				return temp;
			} catch (Exception e) {

			}
		} else {
			try {
				FileInputStream fin = root
						.openFileInput("small_challenge_scores");
				int c;
				String temp = "";
				while ((c = fin.read()) != -1) {
					temp = temp + Character.toString((char) c);
				}
				fin.close();
				return temp;
			} catch (Exception e) {

			}
		}
		return "0,0,0,0,0,0";
	}

	private static String read_classic_score(ClassicActivity root) {
		if (MazeConstants.SIZE) {
			try {
				FileInputStream fin = root
						.openFileInput("large_classic_scores");
				int c;
				String temp = "";
				while ((c = fin.read()) != -1) {
					temp = temp + Character.toString((char) c);
				}
				fin.close();
				return temp;
			} catch (Exception e) {

			}
		} else {
			try {
				FileInputStream fin = root
						.openFileInput("small_classic_scores");
				int c;
				String temp = "";
				while ((c = fin.read()) != -1) {
					temp = temp + Character.toString((char) c);
				}
				fin.close();
				return temp;
			} catch (Exception e) {

			}
		}
		return "0,0,0,0,0";
	}

	public static int[] get_classic_scores(MainActivity root) {
		int[] temp = new int[5];
		if (MazeConstants.SIZE) {
			try {
				FileInputStream fin = root
						.openFileInput("large_classic_scores");
				int c;
				String scores = "";
				while ((c = fin.read()) != -1) {
					scores = scores + Character.toString((char) c);
				}
				List<String> score_list = Arrays.asList(scores.split(","));
				for (int i = 0; i < 5; ++i) {
					temp[i] = Integer.parseInt(score_list.get(4 - i));
				}
				fin.close();
			} catch (Exception e) {

			}
		} else {
			try {
				FileInputStream fin = root
						.openFileInput("small_classic_scores");
				int c;
				String scores = "";
				while ((c = fin.read()) != -1) {
					scores = scores + Character.toString((char) c);
				}
				List<String> score_list = Arrays.asList(scores.split(","));
				for (int i = 0; i < 5; ++i) {
					temp[i] = Integer.parseInt(score_list.get(4 - i));
				}
				fin.close();
			} catch (Exception e) {

			}
		}
		return temp;
	}

	public static int[] get_challenge_scores(MainActivity root) {
		int[] temp = new int[6];
		if (MazeConstants.SIZE) {
			try {
				FileInputStream fin = root
						.openFileInput("large_challenge_scores");
				int c;
				String scores = "";
				while ((c = fin.read()) != -1) {
					scores = scores + Character.toString((char) c);
				}
				List<String> score_list = Arrays.asList(scores.split(","));
				for (int i = 0; i < 6; ++i) {
					temp[i] = Integer.parseInt(score_list.get(i));
				}
				fin.close();
			} catch (Exception e) {

			}
		} else {
			try {
				FileInputStream fin = root
						.openFileInput("small_challenge_scores");
				int c;
				String scores = "";
				while ((c = fin.read()) != -1) {
					scores = scores + Character.toString((char) c);
				}
				List<String> score_list = Arrays.asList(scores.split(","));
				for (int i = 0; i < 6; ++i) {
					temp[i] = Integer.parseInt(score_list.get(i));
				}
				fin.close();
			} catch (Exception e) {

			}
		}
		return temp;
	}

	public static void save_game_state(Context m_context, ClassicActivity root,
			int[][] maze, int dx, int dy, Stack keys, int key_count,
			int key_score, int lives, float ballX, float ballY, float teleX,
			float teleY, boolean teleport, int life_number) throws IOException {
		FileOutputStream fos = m_context.openFileOutput("o_maze",
				Context.MODE_PRIVATE);
		ObjectOutputStream os = new ObjectOutputStream(fos);
		os.writeObject(maze);
		os.close();

		int s = keys.getSize();
		int[][] k = new int[s][2];
		for (int i = 0; i < s; ++i) {
			k[i][0] = keys.topX();
			k[i][1] = keys.topY();
			keys.pop();
		}
		fos = m_context.openFileOutput("o_keys", Context.MODE_PRIVATE);
		os = new ObjectOutputStream(fos);
		os.writeObject(k);
		os.close();

		try {
			FileOutputStream fOut = root.openFileOutput("key_data",
					Context.MODE_PRIVATE);
			String key = Integer.toString(dx) + "," + Integer.toString(dy)
					+ "," + Integer.toString(key_count) + ","
					+ Integer.toString(key_score) + ","
					+ Integer.toString(lives) + "," + Float.toString(ballX)
					+ "," + Float.toString(ballY) + "," + Float.toString(teleX)
					+ "," + Float.toString(teleY) + ","
					+ Boolean.toString(teleport) + ","
					+ Integer.toString(life_number);
			fOut.write(key.getBytes());
			fOut.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void save_game_state(Context m_context,
			ClassicResumeActivity root, int[][] maze, int dx, int dy,
			Stack keys, int key_count, int key_score, int lives, float ballX,
			float ballY, float teleX, float teleY, boolean teleport,
			int life_number) throws IOException {
		FileOutputStream fos = m_context.openFileOutput("o_maze",
				Context.MODE_PRIVATE);
		ObjectOutputStream os = new ObjectOutputStream(fos);
		os.writeObject(maze);
		os.close();
		int s = keys.getSize();
		int[][] k = new int[s][2];
		for (int i = 0; i < s; ++i) {
			k[i][0] = keys.topX();
			k[i][1] = keys.topY();
			keys.pop();
		}
		os.close();
		fos = m_context.openFileOutput("o_keys", Context.MODE_PRIVATE);
		os = new ObjectOutputStream(fos);
		os.writeObject(k);
		os.close();
		try {
			FileOutputStream fOut = root.openFileOutput("key_data",
					Context.MODE_PRIVATE);
			String key = Integer.toString(dx) + "," + Integer.toString(dy)
					+ "," + Integer.toString(key_count) + ","
					+ Integer.toString(key_score) + ","
					+ Integer.toString(lives) + "," + Float.toString(ballX)
					+ "," + Float.toString(ballY) + "," + Float.toString(teleX)
					+ "," + Float.toString(teleY) + ","
					+ Boolean.toString(teleport) + ","
					+ Integer.toString(life_number);
			fOut.write(key.getBytes());
			fOut.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Object[] get_game_state(Context m_context,
			ClassicResumeActivity root) throws IOException,
			ClassNotFoundException {
		Object[] o = new Object[13];
		FileInputStream fis = m_context.openFileInput("o_maze");
		ObjectInputStream is = new ObjectInputStream(fis);
		o[0] = (int[][]) is.readObject();
		is.close();
		fis = m_context.openFileInput("o_keys");
		is = new ObjectInputStream(fis);
		o[1] = (int[][]) is.readObject();
		is.close();
		String temp = "";
		try {
			FileInputStream fin = root.openFileInput("key_data");
			int c;

			while ((c = fin.read()) != -1) {
				temp = temp + Character.toString((char) c);
			}
			List<String> key = Arrays.asList(temp.split(","));
			o[2] = (int) Integer.parseInt(key.get(0));
			o[3] = (int) Integer.parseInt(key.get(1));
			o[4] = (int) Integer.parseInt(key.get(2));
			o[5] = (int) Integer.parseInt(key.get(3));
			o[6] = (int) Integer.parseInt(key.get(4));
			o[7] = (float) Float.parseFloat(key.get(5));
			o[8] = (float) Float.parseFloat(key.get(6));
			o[9] = (float) Float.parseFloat(key.get(7));
			o[10] = (float) Float.parseFloat(key.get(8));
			o[11] = (boolean) Boolean.parseBoolean(key.get(9));
			o[12] = (int) Integer.parseInt((key.get(10)));
			fin.close();
		} catch (Exception e) {

		}
		return o;
	}

	public static void save_classic_score(ClassicResumeActivity root, int score) {
		String scores = read_classic_score(root);
		List<String> score_list = Arrays.asList(scores.split(","));
		int[] temp = new int[6];
		for (int i = 0; i < 5; ++i) {
			temp[i] = Integer.parseInt(score_list.get(i));
		}

		for (int i = 0; i < 5; ++i) {
			if (temp[i] == score)
				return;
		}

		temp[5] = score;
		Arrays.sort(temp);

		String top_scores = "";
		for (int i = 1; i < 6; ++i) {
			top_scores += Integer.toString(temp[i]) + ",";
		}
		if (MazeConstants.SIZE) {
			try {
				FileOutputStream fOut = root.openFileOutput(
						"large_classic_scores", Context.MODE_PRIVATE);
				fOut.write(top_scores.getBytes());
				fOut.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				FileOutputStream fOut = root.openFileOutput(
						"small_classic_scores", Context.MODE_PRIVATE);
				fOut.write(top_scores.getBytes());
				fOut.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static String read_classic_score(ClassicResumeActivity root) {
		if (MazeConstants.SIZE) {
			try {
				FileInputStream fin = root
						.openFileInput("large_classic_scores");
				int c;
				String temp = "";
				while ((c = fin.read()) != -1) {
					temp = temp + Character.toString((char) c);
				}
				fin.close();
				return temp;
			} catch (Exception e) {

			}
		} else {
			try {
				FileInputStream fin = root
						.openFileInput("small_classic_scores");
				int c;
				String temp = "";
				while ((c = fin.read()) != -1) {
					temp = temp + Character.toString((char) c);
				}
				fin.close();
				return temp;
			} catch (Exception e) {

			}
		}
		return "0,0,0,0,0";
	}

	public static void save_game_constants(MainActivity root, boolean sIZE,
			int dIFFICULTY, boolean tONE, boolean vIBRATION, boolean rESUMABLE) {
		String constants = sIZE + "," + dIFFICULTY + "," + tONE + ","
				+ vIBRATION + "," + rESUMABLE;
		try {
			FileOutputStream fOut = root.openFileOutput("game_constants",
					Context.MODE_PRIVATE);
			fOut.write(constants.getBytes());
			fOut.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void load_game_constants(MainActivity root) {
		try {
			String temp = "";
			FileInputStream fin = root.openFileInput("game_constants");
			int c;

			while ((c = fin.read()) != -1) {
				temp = temp + Character.toString((char) c);
			}
			List<String> constants = Arrays.asList(temp.split(","));
			MazeConstants.SIZE = (boolean) Boolean.parseBoolean(constants
					.get(0));
			MazeConstants.DIFFICULTY = (int) Integer.parseInt(constants.get(1));
			MazeConstants.TONE = (boolean) Boolean.parseBoolean(constants
					.get(2));
			MazeConstants.VIBRATION = (boolean) Boolean.parseBoolean(constants
					.get(3));
			MazeConstants.RESUMABLE = (boolean) Boolean.parseBoolean(constants
					.get(4));
			fin.close();
		} catch (Exception e) {

		}
	}

}
