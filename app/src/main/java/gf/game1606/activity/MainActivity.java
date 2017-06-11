package gf.game1606.activity;

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

import gf.game1606.Application;
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
	private Block recordBtn;
	private TextView recordBtnText;
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
		double toX, toY;

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
		GFtext.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		GFtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		GFtext.setSingleLine();
		GFtext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		GFtext.setY((float) (Application.getHEIGHT() - 48 * Application.getRATIO()));
		//relativeLayout.addView(GFtext, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		*/

		toX = Application.getWIDTH() * 0.35;
		toY = Application.getHEIGHT() * 0.4;
		// startBtn
		startBtn = new Block(this, Application.getBLOCK_SIZE() * 9, toX, toY, 3);
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

		toX = toX - Application.getBLOCK_SIZE() * 7 * Math.cos(Math.toRadians(rotation));
		toY = toY - Application.getBLOCK_SIZE() * 7 * Math.sin(Math.toRadians(rotation));
		// tutorialBtn
		tutorialBtn = new Block(this, Application.getBLOCK_SIZE() * 6, toX, toY, 0);
		tutorialBtn.setPivotX(0);
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
		tutorialBtnText.setWidth((int) (Application.getBLOCK_SIZE() * 5.5));
		tutorialBtnText.setX((float) (tutorialBtn.getToX() - Application.getBLOCK_SIZE() * Math.sin(Math.toRadians(rotation))));
		tutorialBtnText.setY((float) (tutorialBtn.getToY() + Application.getBLOCK_SIZE() * Math.cos(Math.toRadians(rotation))));
		tutorialBtnText.setRotation(rotation);
		tutorialBtnText.setTextColor(Color.WHITE);
		tutorialBtnText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		tutorialBtnText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
		tutorialBtnText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
		relativeLayout.addView(tutorialBtnText, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		toX = toX + Application.getBLOCK_SIZE() * Math.cos(Math.toRadians(rotation)) - Application.getBLOCK_SIZE() * 7 * Math.sin(Math.toRadians(rotation));
		toY = toY + Application.getBLOCK_SIZE() * Math.sin(Math.toRadians(rotation)) + Application.getBLOCK_SIZE() * 7 * Math.cos(Math.toRadians(rotation));
		// recordBtn
		recordBtn = new Block(this, Application.getBLOCK_SIZE() * 5, toX, toY, 1);
		recordBtn.setPivotX(0);
		recordBtn.setPivotY(0);
		recordBtn.setRotation(rotation);
		recordBtn.setEnabled(true);
		recordBtn.setOnClickListener(this);
		relativeLayout.addView(recordBtn, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		blocks.add(recordBtn);
		recordBtn.setWidthHeight();

		// tutorialBtnText
		recordBtnText = new TextView(this);
		recordBtnText.setText("RECORD");
		recordBtnText.setPivotX(0);
		recordBtnText.setPivotY(0);
		recordBtnText.setWidth((int) (Application.getBLOCK_SIZE() * 5));
		recordBtnText.setX((float) (recordBtn.getToX() - Application.getBLOCK_SIZE() / 2 * Math.cos(Math.toRadians(rotation))));
		recordBtnText.setY((float) (recordBtn.getToY() - Application.getBLOCK_SIZE() / 2 * Math.sin(Math.toRadians(rotation))));
		recordBtnText.setRotation(rotation);
		recordBtnText.setTextColor(Color.WHITE);
		recordBtnText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		recordBtnText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
		recordBtnText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
		relativeLayout.addView(recordBtnText, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	@Override
	public void onClick(View v)
	{
		System.out.println("MainActivity onClick");

		Intent intent;
		if (v.equals(startBtn))
			intent = new Intent(getApplicationContext(), GameActivity.class);
		else if (v.equals(tutorialBtn))
			intent = new Intent(getApplicationContext(), HowToPlayActivity.class);
		else if (v.equals(recordBtn))
			intent = new Intent(getApplicationContext(), RecordActivity.class);
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