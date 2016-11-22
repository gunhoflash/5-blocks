package gf.game1606;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppActivity implements View.OnClickListener
{
	private ArrayList<Block> blocks = new ArrayList<>();

	private RelativeLayout relativeLayout;

	private TextView titleText;

	private Block startBtn;
	private TextView startBtnText;

	private Block tutorialBtn;
	private TextView tutorialBtnText;

	private TextView GFtext;
	// layout

	private Boolean        realTimeThreadIsRunning = true;
	private Handler        realTimeThreadHandler;
	private ThreadRunnable realTimeThreadRunnable;
	private Thread         realTimeThread;
	// thread

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("MainActivity onCreate");
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		relativeLayout = new RelativeLayout(this);
		relativeLayout.setBackgroundColor(ColorSet.LightGray);
		relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		setContentView(relativeLayout);

		initializeVariable();
		initializeLayout();
		loadData();
		realTimeThread.start();
	}

	private void initializeVariable()
	{
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Point point = new Point();
		((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);

		WIDTH = point.x;
		HEIGHT = point.y; // - 48*displayMetrics.densityDpi/160; // NoActionBar and NoTitleBar
		ratio = displayMetrics.densityDpi / 160.0;
		blockSize = (int) Math.min(WIDTH * 80 / 720.0, HEIGHT * 80 / 1280.0);
		blockGap = (int) (blockSize * 0.2);

		System.out.println("DisplayMetrics: " + displayMetrics + ", DPI: " + displayMetrics.densityDpi);
		System.out.println("WIDTH: " + WIDTH + ", HEIGHT: " + HEIGHT);
		System.out.println("blockSize: " + blockSize);

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
							Thread.sleep(36);
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
		Block background = new Block(this, blockSize*14, WIDTH*0.8f - blockSize*7f, HEIGHT*0.45f - blockSize*7f, 1);
		background.toColor = Color.WHITE;
		background.i = -1;
		background.setToScaleX(1.8f);
		background.setRotation(-26f);
		relativeLayout.addView(background, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		blocks.add(background);

		titleText = new TextView(this);
		titleText.setText("5 Blocks");
		titleText.setTextColor(Color.parseColor("#414141"));
		titleText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 56);
		titleText.setSingleLine();
		titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		titleText.setX(WIDTH*0.225f);
		titleText.setY(HEIGHT*0.14f);
		titleText.setScaleX(2f);
		titleText.setScaleY(2f);
		titleText.setRotation(background.getRotation());
		relativeLayout.addView(titleText, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		GFtext = new TextView(this);
		GFtext.setText("GF");
		GFtext.setTextColor(Color.parseColor("#CFCFCF"));
		GFtext.setTypeface(Typeface.createFromAsset(getAssets(), "DroidSansMono.ttf"));
		GFtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		GFtext.setSingleLine();
		GFtext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		GFtext.setY((float) (HEIGHT - 48 * ratio));
		//relativeLayout.addView(GFtext, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		startBtn = new Block(this, blockSize*9, WIDTH*0.5f, HEIGHT*0.25f, 1);
		startBtn.setRotation(background.getRotation());
		startBtn.setEnabled(true);
		startBtn.setOnClickListener(this);
		relativeLayout.addView(startBtn, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		blocks.add(startBtn);
		startBtn.setWidthHeight();

		startBtnText = new TextView(this);
		startBtnText.setText(" PLAY");
		startBtnText.setTextColor(background.toColor);
		startBtnText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		startBtnText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 64);
		startBtnText.setSingleLine();
		startBtnText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		startBtnText.setPivotX(startBtn.getPivotX());
		startBtnText.setPivotY(startBtn.getPivotY());
		startBtnText.setX(startBtn.getToX());
		startBtnText.setY(startBtn.getToY());
		startBtnText.setScaleX(0.9f);
		startBtnText.setScaleY(0.9f);
		startBtnText.setRotation(background.getRotation());
		relativeLayout.addView(startBtnText, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		//startBtnText.setOnClickListener(this);

		tutorialBtn = new Block(this, blockSize*6, startBtn.getToX(), startBtn.getToY(), 2);
		//tutorialBtn.setX(tutorialBtn.toX);
		tutorialBtn.setPivotX(startBtn.getPivotX());
		tutorialBtn.setPivotY(startBtn.getPivotY());
		tutorialBtn.setRotation(background.getRotation());
		tutorialBtn.setToX(tutorialBtn.getToX() - blockSize*7*Math.cos(Math.toRadians((double)startBtn.getRotation())));
		tutorialBtn.setToY(tutorialBtn.getToY() - blockSize*7*Math.sin(Math.toRadians((double)startBtn.getRotation())));
		tutorialBtn.setEnabled(true);
		tutorialBtn.setOnClickListener(this);
		relativeLayout.addView(tutorialBtn, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		blocks.add(tutorialBtn);
		tutorialBtn.setWidthHeight();

		tutorialBtnText = new TextView(this);
		tutorialBtnText.setText("HOW\nTO\nPLAY");
		tutorialBtnText.setTextColor(background.toColor);
		tutorialBtnText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		tutorialBtnText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
		tutorialBtnText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
		tutorialBtnText.setPivotX(startBtn.getPivotX());
		tutorialBtnText.setPivotY(startBtn.getPivotY());
		tutorialBtnText.setX(startBtn.getToX() - blockSize*4.7f*(float)Math.cos(Math.toRadians((double)startBtn.getRotation()))); // 4.75f
		tutorialBtnText.setY(startBtn.getToY() - blockSize*4.75f*(float)Math.sin(Math.toRadians((double)startBtn.getRotation()))); // 4.75f
		tutorialBtnText.setScaleX(0.9f);
		tutorialBtnText.setScaleY(0.9f);
		tutorialBtnText.setRotation(background.getRotation());
		relativeLayout.addView(tutorialBtnText, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		//tutorialBtnText.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		System.out.println(startBtn.getClipBounds().toString());
		System.out.println(tutorialBtn.getClipBounds().toString());
		System.out.println(startBtn.getX() + ", " + startBtn.getY() + ", " + startBtn.getWidth() + ", " + startBtn.getHeight());
		System.out.println(tutorialBtn.getX() + ", " + tutorialBtn.getY() + ", " + tutorialBtn.getWidth() + ", " + tutorialBtn.getHeight());
		System.out.println("MainActivity onClick");
		Intent intent = new Intent(getApplicationContext(), GameActivity.class);
		if (v.equals(startBtn))// || v.equals(startBtnText))
		{
			System.out.println("startBtn");
		}
		else if (v.equals(tutorialBtn))// || v.equals(tutorialBtnText))
		{
			System.out.println("tutorialBtn");
			intent = new Intent(getApplicationContext(), HowToPlayActivity.class);
		}
		else
		{
			System.out.println("else");
			return;
		}
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
		for (int i = 0; i < blocks.size(); i++)
		{
			blocks.get(i).update();
		}
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
			inputStream = openFileInput(filename);
			int i = inputStream.read();
			while (i != -1)
			{
				stringBuilder.append(Character.toString((char)i));
				i = inputStream.read();
			}
			inputStream.close();
		}
		catch (FileNotFoundException e)
		{
			stringBuilder = new StringBuilder(defaultString);
			try
			{
				outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
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

		loadedData = stringBuilder.substring(0);
		setIntegerDataList();
		setVariablesWithData();

		System.out.println("loadedData: " + loadedData);
	}
}
