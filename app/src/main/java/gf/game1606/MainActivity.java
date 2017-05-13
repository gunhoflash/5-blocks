package gf.game1606;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import gf.game1606.block.Block;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
	private ArrayList<Block> blocks = new ArrayList<>();

	private RelativeLayout relativeLayout;

	private TextView titleText;
	private TextView GFtext;
	private Block startBtn;
	private TextView startBtnText;
	private Block tutorialBtn;
	private TextView tutorialBtnText;
	// layout

	private Boolean realTimeThreadIsRunning = true;
	private Handler realTimeThreadHandler;
	private ThreadRunnable realTimeThreadRunnable;
	private Thread realTimeThread;
	private int realTimeThreadSleepTime = 36;
	// thread

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("MainActivity onCreate");
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		relativeLayout = new RelativeLayout(this);
		relativeLayout.setBackgroundColor(Color.WHITE);
		relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		this.setContentView(relativeLayout);

		Application.initializeVariables(this);
		initializeVariable();
		initializeLayout();
		loadData();
		realTimeThread.start();
	}

	private void initializeVariable()
	{
		realTimeThreadHandler = new Handler();
		realTimeThreadRunnable = new ThreadRunnable();
		realTimeThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					while (true)
					{
						if (realTimeThreadIsRunning)
						{
							Thread.sleep(realTimeThreadSleepTime);
							realTimeThreadHandler.post(realTimeThreadRunnable);
						}
					}
				}
				catch (Exception e)
				{
					System.out.println("Error:" + e.toString());
				}
			}
		});
	}

	private void initializeLayout()
	{
		float rotation = -26f;

		titleText = new TextView(this);
		titleText.setText("5 Blocks");
		titleText.setTextColor(Color.parseColor("#414141"));
		titleText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 56);
		titleText.setSingleLine();
		titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		titleText.setX(Application.getWIDTH() * 0.225f);
		titleText.setY(Application.getHEIGHT() * 0.14f);
		titleText.setScaleX(2f);
		titleText.setScaleY(2f);
		titleText.setRotation(rotation);
		relativeLayout.addView(titleText, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		/*
		GFtext = new TextView(this);
		GFtext.setText("GF");
		GFtext.setTextColor(Color.parseColor("#CFCFCF"));
		GFtext.setTypeface(Typeface.createFromAsset(getAssets(), "DroidSansMono.ttf"));
		GFtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		GFtext.setSingleLine();
		GFtext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		GFtext.setY((float) (Application.getHEIGHT() - 48 * Application.getRATIO()));
		//relativeLayout.addView(GFtext, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		*/

		// startBtn
		startBtn = new Block(this, Application.getBLOCK_SIZE() * 9, Application.getWIDTH() * 0.35, Application.getHEIGHT() * 0.4, 1);
		startBtn.setPivotX(0);
		startBtn.setPivotY(0);
		startBtn.setRotation(rotation);
		startBtn.setEnabled(true);
		startBtn.setOnClickListener(this);
		relativeLayout.addView(startBtn, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		blocks.add(startBtn);
		startBtn.setWidthHeight();

		// startBtnText
		startBtnText = new TextView(this);
		startBtnText.setText("PLAY");
		startBtnText.setPivotX(0);
		startBtnText.setPivotY(0);
		startBtnText.setX(startBtn.getToX() + Application.getBLOCK_SIZE());
		startBtnText.setY(startBtn.getToY());
		startBtnText.setRotation(rotation);
		startBtnText.setTextColor(Color.WHITE);
		startBtnText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		startBtnText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 64);
		startBtnText.setSingleLine();
		startBtnText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		relativeLayout.addView(startBtnText, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		// tutorialBtn
		tutorialBtn = new Block(this, Application.getBLOCK_SIZE() * 6, Application.getWIDTH() * 0.25 -  Application.getBLOCK_SIZE() * 6, Application.getHEIGHT() * 0.429, 2);
		tutorialBtn.setPivotX(Application.getBLOCK_SIZE() * 6);
		tutorialBtn.setPivotY(0);
		tutorialBtn.setRotation(rotation);
		tutorialBtn.setEnabled(true);
		tutorialBtn.setOnClickListener(this);
		relativeLayout.addView(tutorialBtn, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		blocks.add(tutorialBtn);
		tutorialBtn.setWidthHeight();

		// tutorialBtnText
		tutorialBtnText = new TextView(this);
		tutorialBtnText.setText("HOW\nTO\nPLAY");
		tutorialBtnText.setPivotX(0);
		tutorialBtnText.setPivotY(0);
		tutorialBtnText.setX(tutorialBtn.getToX() + Application.getBLOCK_SIZE() * 4);
		tutorialBtnText.setY(tutorialBtn.getToY() + Application.getBLOCK_SIZE() * 3);
		tutorialBtnText.setRotation(rotation);
		tutorialBtnText.setTextColor(Color.WHITE);
		tutorialBtnText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		tutorialBtnText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
		tutorialBtnText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
		relativeLayout.addView(tutorialBtnText, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		System.out.println("tutorialBtnText.getWidth(), tutorialBtnText.getHeight():" + tutorialBtnText.getWidth() + ", " + tutorialBtnText.getHeight());
	}

	@Override
	public void onClick(View v)
	{
		System.out.println(startBtn.getClipBounds().toString());
		System.out.println(tutorialBtn.getClipBounds().toString());
		System.out.println(startBtn.getX() + ", " + startBtn.getY() + ", " + startBtn.getWidth() + ", " + startBtn.getHeight());
		System.out.println(tutorialBtn.getX() + ", " + tutorialBtn.getY() + ", " + tutorialBtn.getWidth() + ", " + tutorialBtn.getHeight());
		System.out.println(tutorialBtnText.getX() + ", " + tutorialBtnText.getY() + ", " + tutorialBtnText.getWidth() + ", " + tutorialBtnText.getHeight());
		System.out.println("MainActivity onClick");

		Intent intent;
		if (v.equals(startBtn))
			intent = new Intent(getApplicationContext(), GameActivity.class);
		else if (v.equals(tutorialBtn))
			intent = new Intent(getApplicationContext(), HowToPlayActivity.class);
		else
			return;
		intent.putExtra("select", "start");
		startActivity(intent);
	}

	private class ThreadRunnable implements Runnable
	{
		public void run()
		{
			realTimeThread();
		}
	}

	private void realTimeThread()
	{
		boolean needToUpdate = false;
		for (int i = 0; i < blocks.size(); i++)
		{
			if (blocks.get(i).isNeedToUpdate())
			{
				needToUpdate = true;
				blocks.get(i).update();
			}
		}
		if (!needToUpdate)
			realTimeThreadSleepTime = 1000;
		else if (realTimeThreadSleepTime == 1000)
			realTimeThreadSleepTime = 36;
	}

	private void loadData()
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
			inputStream = openFileInput(Application.FILENAME);
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
				outputStream = openFileOutput(Application.FILENAME, Context.MODE_PRIVATE);
				outputStream.write(defaultString.getBytes());
				outputStream.close();
			}
			catch (Exception er)
			{
				er.printStackTrace();
				Toast.makeText(this, "Error code: DE1\nCannot use userData", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(this, "Error code: DE0\nCannot use userData", Toast.LENGTH_SHORT).show();
			return;
		}

		Application.setLoadedData(stringBuilder.substring(0));
		Application.setIntegerDataList();
		Application.loadScoresWithData();

		System.out.println("loadedData: " + Application.getLoadedData());
	}
}