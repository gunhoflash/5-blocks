package gf.game1606;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;

/*

n*n 색깔 블록 이어 맞추기 - 흔히 볼 수 있는
한 턴이 지날 때마다 이 전에 지운 색깔이 청색편이 - 블록의 점수가 낮아지는 무지개색
마지막으로 터치한 블록과 인접한 같은 블록만 추가 선택 가능 - 최소 2개를 이어 선택할 것
선택한 블록은 전부 사라질 것
보라색의 청색편이는 회색으로, 제거 불가

*/

public class GameActivity extends AppActivity implements View.OnTouchListener
{
	private Block[][] blocks = new Block[blockNum][blockNum];
	private Block[] nextBlocks = new Block[blockNum];
	private ArrayList<Block> selectedBlocks = new ArrayList<>();
	private ArrayList<Block> removeBlocks = new ArrayList<>();
	// block

	private RelativeLayout relativeLayout;
	private ImageView replay;
	private TextView scoreTextView;
	private TextView GFtext;
	// game

	private Boolean        playing = false;
	private Boolean        realTimeThreadIsRunning = true;
	private Handler        realTimeThreadHandler;
	private ThreadRunnable realTimeThreadRunnable;
	private Thread         realTimeThread;

	private Boolean OOPS = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("onCreate");
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		Intent intent = getIntent();
		//intent.getStringExtra("select");

		relativeLayout = new RelativeLayout(this);
		relativeLayout.setBackgroundColor(Color.parseColor("#FAFAFA"));
		relativeLayout.setOnTouchListener(this);
		relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		setContentView(relativeLayout);

		scoreTextView = new TextView(this);
		scoreTextView.setText("0");
		scoreTextView.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
		scoreTextView.setSingleLine();
		scoreTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		scoreTextView.setY(getYPosition(0) / 2f);
		relativeLayout.addView(scoreTextView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		GFtext = new TextView(this);
		GFtext.setText("GF");
		GFtext.setTextColor(Color.parseColor("#CDCDCD"));
		GFtext.setTypeface(Typeface.createFromAsset(getAssets(), "DroidSansMono.ttf"));
		GFtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		GFtext.setSingleLine();
		GFtext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		GFtext.setY((float) (HEIGHT - 48 * ratio));
		relativeLayout.addView(GFtext, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		replay = new ImageView(this);
		replay.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_replay_black_24dp, null));
		replay.setBackgroundColor(Color.parseColor("#DFDFDF"));
		replay.setScaleX(0.4f);
		replay.setScaleY(0.4f);
		replay.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				initialize();
			}
		});
		relativeLayout.addView(replay, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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

		if (Build.VERSION.SDK_INT >= 21)
		{
			soundPoolBuilder = new SoundPool.Builder();
			soundPoolBuilder.setMaxStreams(25);
			soundPoolBuilder.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
			soundPool = soundPoolBuilder.build();
		}
		else
			soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 0);

		soundID = soundPool.load(this, R.raw.tick, 1);

		loadData();
		playing = true;
		//initialize();

		realTimeThread.start();
	}
	@Override
	public void onStop()
	{
		System.out.println("onStop");
		realTimeThreadIsRunning = false;
		finish();
		super.onStop();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		// Do not get event while blocks are removing
		if (removeBlocks.size() > 0)
			return false;

		int i, j, k;

		// Do not get event while blocks are removing
		for (i = 0; i < blocks.length; i++)
			for (j = 0; j < blocks[i].length; j++)
				if (blocks[i][j] == null)
					return false;

		// Get event
		switch (MotionEventCompat.getActionMasked(event))
		{
			case MotionEvent.ACTION_DOWN:
				// Exception
				if (selectedBlocks.size() != 0)
					return false;

				for (i = 0; i < blocks.length; i++)
				{
					for (j = 0; j < blocks[i].length; j++)
					{
						if (blocks[i][j].isHit(event.getX(), event.getY()))
						{
							blocks[i][j].select(0);
							selectedBlocks.add(blocks[i][j]);
							return true;
						}
					}
				}
				return true;

			case MotionEvent.ACTION_MOVE:
				// Exception
				if (selectedBlocks.size() == 0) return false;

				for (i = 0; i < blocks.length; i++)
				{
					for (j = 0; j < blocks[i].length; j++)
					{
						if (blocks[i][j].isHit(event.getX(), event.getY()))
						{
							if (selectedBlocks.get(0).getLevel() == blocks[i][j].getLevel())
							{
								if (blocks[i][j].isSelected())
								{
									while (blocks[i][j].getSelectedIndex() < selectedBlocks.size() - 1)
									{
										selectedBlocks.get(selectedBlocks.size() - 1).unSelect();
										selectedBlocks.remove(selectedBlocks.size() - 1);
									}
								}
								else if (Block.isNear(selectedBlocks.get(selectedBlocks.size() - 1), blocks[i][j]))
								{
									blocks[i][j].select(selectedBlocks.size());
									selectedBlocks.add(blocks[i][j]);
									//System.out.println(selectedBlocks.size());
								}
							}
							return true;
						}
					}
				}
				return true;

			case MotionEvent.ACTION_UP:
				if (playing)
				{
					if (selectedBlocks.size() > 1)
					{
						k = selectedBlocks.get(0).getLevel();
						if (k != colorNumber)
						{
							int plusScore = (int) (Math.pow(selectedBlocks.size(), 1.9 + 2.0/(k + 4)) * 100 / (k  + 4));
							toScore += plusScore;
							totalScore += plusScore;
							if (toScore > highScore)
								highScore = toScore;

							for (i = 0; i < blocks.length; i++)
							{
								for (j = 0; j < blocks[i].length; j++)
								{
									if (blocks[i][j].getLevel() == k && !blocks[i][j].isSelected())
									{
										if (blocks[i][j].getLevel() == colorNumber - 1)
											OOPS = true;
										blocks[i][j].setLevel(blocks[i][j].getLevel() + 1);
									}
								}
							}

							while (selectedBlocks.size() > 0)
							{
								removeBlocks.add(selectedBlocks.remove(0));
							}

							if (removeBlocks.size() > 0)
								saveData();
						}
					}
				}
				selectedBlocks = new ArrayList<>();
				for (i = 0; i < blocks.length; i++)
				{
					for (j = 0; j < blocks[i].length; j++)
					{
						blocks[i][j].unSelect();
					}
				}
				return true;

			case MotionEvent.ACTION_CANCEL:
				System.out.println("MotionEvent.ACTION_CANCEL");
				return true;

			case MotionEvent.ACTION_OUTSIDE:
				System.out.println("MotionEvent.ACTION_OUTSIDE");
				return true;

			default:
				System.out.println("MotionEvent.default");
				return false;
		}
	}

	private void initialize()
	{
		int i, j;

		if (blocks.length == 0) return;
		for (i = 0; i < blocks.length; i++)
		{
			for (j = 0; j < blocks[i].length; j++)
			{
				if (blocks[i][j] == null) return;
				if (blocks[i][j].getAlpha() < 1) return;
			}
		}

		toScore = 0;

		/** nextBlocks **/
		if (nextBlocks == null) nextBlocks = new Block[blockNum];
		for(j = 0; j < blockNum; j++)
		{
			if (nextBlocks[j] != null) relativeLayout.removeView(nextBlocks[j]);
			nextBlocks[j] = newRandomBlock(j);
		}

		/** blocks **/
		if (blocks != null)
			for (i = 0; i < blocks.length; i++)
				for (j = 0; j < blocks[i].length; j++)
					removeBlock(i, j);
		else blocks = new Block[blockNum][blockNum];

		/** selectedBlocks **/
		selectedBlocks = new ArrayList<>();

		/** removeBlocks **/
		removeBlocks = new ArrayList<>();
	}
	private void loadData()
	{
		setIntegerDataList();

		int tempInt, i, j;
		for (j = 0; j < nextBlocks.length; j++)
		{
			tempInt = integerDataList.get(j);
			if (tempInt == 0)
				nextBlocks[j] = newRandomBlock(j);
			else
				nextBlocks[j] = newBlock(j, tempInt-1);
		}
		for (i = 0; i < blocks.length; i++)
		{
			for (j = 0; j < blocks[i].length; j++)
			{
				tempInt = integerDataList.get(blockNum * (i + 1) + j);
				if (tempInt == 0)
					blocks[i][j] = null;
				else
				{
					blocks[i][j] = newBlock(j, tempInt-1);
					blocks[i][j].setPivotY(blockSize/2);
					blocks[i][j].setToScaleXY(1, 1);
					blocks[i][j].setIJ(i, j);
					blocks[i][j].setToX(getXPosition(j));
					blocks[i][j].setToY(getYPosition(i));
				}
			}
		}
	}
	private void saveData()
	{
		int i, j;
		StringBuilder stringBuilder = new StringBuilder();

		for (j = 0; j < nextBlocks.length; j++)
		{
			if (nextBlocks[j] == null)
				stringBuilder.append("0_");
			else
			{
				stringBuilder.append(String.valueOf(nextBlocks[j].getLevel() + 1));
				stringBuilder.append("_");
			}
		}
		for (i = 0; i < blocks.length; i++)
		{
			for (j = 0; j < blocks[i].length; j++)
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
		stringBuilder.append(String.valueOf(toScore));
		stringBuilder.append("_");
		stringBuilder.append(String.valueOf(totalScore));
		stringBuilder.append("_");
		stringBuilder.append(String.valueOf(highScore));
		stringBuilder.append("_null");

		loadedData = stringBuilder.substring(0);

		FileOutputStream outputStream;
		try
		{
			outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
			outputStream.write(loadedData.getBytes());
			outputStream.close();
		}
		catch (Exception er)
		{
			er.printStackTrace();
			Toast.makeText(this, "Error code: DE2\nCannot use userData", Toast.LENGTH_SHORT).show();
		}
	}

	private class ThreadRunnable implements Runnable
	{
		public void run()
		{
			realTimeThread();
		}
	}
	public void realTimeThread()
	{
		int i, j, k = 0;
		for (i = 0; i < blocks.length; i++)
		{
			for (j = 0; j < blocks[i].length; j++)
			{
				if (blocks[i][j] == null)
				{
					if (i == 0)
					{
						blocks[i][j] = nextBlocks[j];
						nextBlocks[j] = newRandomBlock(j);
					}
					else
					{
						blocks[i][j] = blocks[i-1][j];
						blocks[i-1][j] = null;
					}
					blocks[i][j].setPivotY(blockSize/2);
					blocks[i][j].setToScaleXY(1, 1);
					blocks[i][j].setIJ(i, j);
					blocks[i][j].setToX(getXPosition(j));
					blocks[i][j].setToY(getYPosition(i));
					k = 1;
				}
				blocks[i][j].update();
			}
		}
		if (k == 1) saveData();

		for (i = 0; i < nextBlocks.length; i++)
		{
			nextBlocks[i].update();
		}

		if (removeBlocks.size() > 0)
		{
			if (removeBlocks.get(0).getToAlpha() != -0.2f)
			{
				soundPool.play(soundID,1,1,0,0,1);
				removeBlocks.get(0).setToAlpha(-0.2f);
			}
			if (removeBlocks.get(0).getAlpha() < 0.1f)
			{
				removeBlock(removeBlocks.get(0).getI(), removeBlocks.get(0).getJ());
				removeBlocks.remove(0);
			}
		}

		if (score != toScore)
		{
			if (Math.abs(toScore - score) < 2)
				score = toScore;
			else if (toScore > score)
				score += (int)((toScore - score) * 0.15) + 1;
			else
				score += (int)((toScore - score) * 0.15) - 1;
		}
		scoreTextView.setText(String.valueOf(score));

		if (OOPS)
		{
			Toast.makeText(this, "Oops!", Toast.LENGTH_SHORT).show();
			OOPS = false;
		}
	}

	private Block newRandomBlock(int j)
	{
		return newBlock(j, (Math.random() > 0.025) ? (colorNumber - 2) - (int) (Math.random() * (colorNumber - 1)) : colorNumber - 1);
	}
	private Block newBlock(int j, int level)
	{
		Block block = new Block(this, (int) blockSize, getXPosition(j), getYPosition(-1), level);
		block.setX(block.getToX());
		block.setIJ(-1, j);
		block.setPivotY(blockSize);
		block.setToScaleY(0.2f);

		relativeLayout.addView(block, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		return block;
	}
	private void removeBlock(int i, int j)
	{
		if (blocks[i][j] == null) return;
		relativeLayout.removeView(blocks[i][j]);
		blocks[i][j] = null;
	}

	private int getXPosition(float i)
	{
		return Block.getXPosition(i);
	}
	private int getYPosition(float i)
	{
		return Block.getYPosition(i);
	}
}


//