package gf.game1606;

import android.content.Context;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import gf.game1606.block.Block;
import gf.game1606.block.BlockManager;

public abstract class Application
{
	// final
	public final static String LANGUAGE = Locale.getDefault().getLanguage();
	public final static String FILENAME = "5Blocks_1002_data_00";
	public final static int BLOCK_NUM = 5;
	public final static int COLOR_NUM = BlockManager.color.length - 1;

	// be final
	private static int WIDTH;
	private static int HEIGHT;
	private static double RATIO;
	private static float BLOCK_SIZE;
	private static float BLOCK_GAP;

	// sound
	private static SoundPool soundPool;
	private static SoundPool.Builder soundPoolBuilder;
	private static int soundID;

	// data
	private static String loadedData_string;
	private static ArrayList<Integer> loadedData_integer;
	private static int toScore = 0;
	private static int score = 0;
	private static int totalScore = 0;
	private static int highScore = 0;

	public static void initializeVariables(Context context)
	{
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
		Point point = new Point();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);

		setWIDTH(point.x);
		setHEIGHT(point.y);
		setRATIO(displayMetrics.densityDpi / 160f);
		setBLOCK_SIZE((int) Math.min(getWIDTH() * 80 / 720.0, getHEIGHT() * 80 / 1280.0));
		setBLOCK_GAP((int) (getBLOCK_SIZE() * 0.2));

		if (Build.VERSION.SDK_INT >= 21)
		{
			setSoundPoolBuilder(new SoundPool.Builder());
			getSoundPoolBuilder().setMaxStreams(25);
			getSoundPoolBuilder().setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
			setSoundPool(getSoundPoolBuilder().build());
		}
		else
			setSoundPool(new SoundPool(25, AudioManager.STREAM_MUSIC, 0));

		setSoundID(getSoundPool().load(context, R.raw.tick, 1));
	}

	public static void setIntegerDataList()
	{
		StringTokenizer stringTokenizer = new StringTokenizer(loadedData_string, "_");
		// get data as String

		loadedData_integer = new ArrayList<>();
		for (int i = 0; i < 33; i++)
		{
			loadedData_integer.add(Integer.parseInt(stringTokenizer.nextToken()));
		}
		// get data as Integer
	}

	public static void saveData(Context context, Block[] nextBlocks, Block[][] blocks, ArrayList<Block> removeBlocks)
	{
		int i, j;
		StringBuilder stringBuilder = new StringBuilder();

		for (j = 0; j < Application.BLOCK_NUM; j++)
		{
			if (nextBlocks[j] == null)
				stringBuilder.append("0_");
			else
			{
				stringBuilder.append(String.valueOf(nextBlocks[j].getLevel() + 1));
				stringBuilder.append("_");
			}
		}
		for (i = 0; i < Application.BLOCK_NUM; i++)
		{
			for (j = 0; j < Application.BLOCK_NUM; j++)
			{
				if (blocks[i][j] == null)
					stringBuilder.append("0_");
				else if (removeBlocks.contains(blocks[i][j]))
					stringBuilder.append("0_");
				else
				{
					stringBuilder.append(String.valueOf(blocks[i][j].getLevel() + 1));
					stringBuilder.append("_");
				}
			}
		}
		stringBuilder.append(String.valueOf(Application.getToScore()));
		stringBuilder.append("_");
		stringBuilder.append(String.valueOf(Application.getTotalScore()));
		stringBuilder.append("_");
		stringBuilder.append(String.valueOf(Application.getHighScore()));
		stringBuilder.append("_null");

		Application.setLoadedData_String(stringBuilder.substring(0));

		FileOutputStream outputStream;
		try
		{
			outputStream = context.openFileOutput(Application.FILENAME, Context.MODE_PRIVATE);
			outputStream.write(Application.getLoadedData_String().getBytes());
			outputStream.close();
		}
		catch (Exception er)
		{
			er.printStackTrace();
			Toast.makeText(context, "Error code: DE2\nCannot use userData", Toast.LENGTH_SHORT).show();
		}
	}

	public static void loadData(Context context)
	{
		String defaultString = "0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_null";
		/*

		0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_1234567890_1234567890_1234567890_null
		5 nextBlocks level, 25 blocks level, now score, total score, high score, not use yet

		block.level = saveFile.level - 1;
		if 'level' == 0, then it means 'null'

		*/
		FileOutputStream outputStream;
		FileInputStream inputStream;
		StringBuilder stringBuilder = new StringBuilder("");
		try
		{
			inputStream = context.openFileInput(Application.FILENAME);
			int i = inputStream.read();
			while (i != -1)
			{
				stringBuilder.append(Character.toString((char) i));
				i = inputStream.read();
			}
			inputStream.close();
		}
		catch (FileNotFoundException e)
		{
			stringBuilder = new StringBuilder(defaultString);
			try
			{
				outputStream = context.openFileOutput(Application.FILENAME, Context.MODE_PRIVATE);
				outputStream.write(defaultString.getBytes());
				outputStream.close();
			}
			catch (Exception er)
			{
				er.printStackTrace();
				Toast.makeText(context, "Error code: DE1\nCannot use userData", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(context, "Error code: DE0\nCannot use userData", Toast.LENGTH_SHORT).show();
			return;
		}

		Application.setLoadedData_String(stringBuilder.substring(0));
		Application.setIntegerDataList();
		toScore = loadedData_integer.get(30);
		totalScore = loadedData_integer.get(31);
		highScore = loadedData_integer.get(32);

		System.out.println("getLoadedData_String(): " + Application.getLoadedData_String());
	}


	// get/set be final

	public static int getWIDTH()
	{
		return WIDTH;
	}

	public static void setWIDTH(int width)
	{
		Application.WIDTH = width;
	}

	public static int getHEIGHT()
	{
		return HEIGHT;
	}

	public static void setHEIGHT(int height)
	{
		Application.HEIGHT = height;
	}

	public static double getRATIO()
	{
		return RATIO;
	}

	public static void setRATIO(double ratio)
	{
		Application.RATIO = ratio;
	}

	public static float getBLOCK_SIZE()
	{
		return BLOCK_SIZE;
	}

	public static void setBLOCK_SIZE(float blockSize)
	{
		Application.BLOCK_SIZE = blockSize;
	}

	public static float getBLOCK_GAP()
	{
		return BLOCK_GAP;
	}

	public static void setBLOCK_GAP(float blockGap)
	{
		Application.BLOCK_GAP = blockGap;
	}

	// get/set sound

	public static SoundPool getSoundPool()
	{
		return soundPool;
	}

	public static void setSoundPool(SoundPool soundPool)
	{
		Application.soundPool = soundPool;
	}

	public static SoundPool.Builder getSoundPoolBuilder()
	{
		return soundPoolBuilder;
	}

	public static void setSoundPoolBuilder(SoundPool.Builder soundPoolBuilder)
	{
		Application.soundPoolBuilder = soundPoolBuilder;
	}

	public static int getSoundID()
	{
		return soundID;
	}

	public static void setSoundID(int soundID)
	{
		Application.soundID = soundID;
	}

	// get/set data

	public static String getLoadedData_String()
	{
		return loadedData_string;
	}

	public static void setLoadedData_String(String loadedData_string)
	{
		Application.loadedData_string = loadedData_string;
	}

	public static ArrayList<Integer> getLoadedData_Integer()
	{
		return loadedData_integer;
	}

	public static void setLoadedData_Integer(ArrayList<Integer> loadedData_integer)
	{
		Application.loadedData_integer = loadedData_integer;
	}

	public static int getToScore()
	{
		return toScore;
	}

	public static void setToScore(int toScore)
	{
		Application.toScore = toScore;
	}

	public static int getScore()
	{
		return score;
	}

	public static void setScore(int score)
	{
		Application.score = score;
	}

	public static int getTotalScore()
	{
		return totalScore;
	}

	public static void setTotalScore(int totalScore)
	{
		Application.totalScore = totalScore;
	}

	public static int getHighScore()
	{
		return highScore;
	}

	public static void setHighScore(int highScore)
	{
		Application.highScore = highScore;
	}

	public static float dpFromPx(Context context, float dp)
	{
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
}