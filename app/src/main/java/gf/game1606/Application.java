package gf.game1606;

import android.content.Context;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

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
	private static String loadedData;
	private static ArrayList<Integer> integerDataList;
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

	public static void loadScoresWithData()
	{
		toScore = integerDataList.get(30);
		totalScore = integerDataList.get(31);
		highScore = integerDataList.get(32);
	}

	public static void setIntegerDataList()
	{
		StringTokenizer stringTokenizer = new StringTokenizer(loadedData, "_");
		// get data as String

		integerDataList = new ArrayList<>();
		for (int i = 0; i < 33; i++)
		{
			integerDataList.add(Integer.parseInt(stringTokenizer.nextToken()));
		}
		// get data as Integer
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

	public static String getLoadedData()
	{
		return loadedData;
	}

	public static void setLoadedData(String loadedData)
	{
		Application.loadedData = loadedData;
	}

	public static ArrayList<Integer> getIntegerDataList()
	{
		return integerDataList;
	}

	public static void setIntegerDataList(ArrayList<Integer> integerDataList)
	{
		Application.integerDataList = integerDataList;
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
}