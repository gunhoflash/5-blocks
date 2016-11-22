package gf.game1606;

import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class AppActivity extends AppCompatActivity
{
	protected final static String filename = "5Blocks_1002_data_00";
	protected final static int blockNum = 5;
	protected final static int colorNumber = ColorSet.color.length - 1;

	protected static int WIDTH;
	protected static int HEIGHT;
	protected static double ratio;
	protected static float blockSize;
	protected static float blockGap;
	// be final

	protected static SoundPool soundPool;
	protected static SoundPool.Builder soundPoolBuilder;
	protected static int soundID;
	// sound

	protected static String loadedData;
	protected static ArrayList<Integer> integerDataList;
	protected static int toScore = 0;
	protected static int score = 0;
	protected static int totalScore = 0;
	protected static int highScore = 0;
	// data

	protected static void setVariablesWithData()
	{
		toScore = integerDataList.get(30);
		totalScore = integerDataList.get(31);
		highScore = integerDataList.get(32);
	}

	protected static void setIntegerDataList()
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
}
