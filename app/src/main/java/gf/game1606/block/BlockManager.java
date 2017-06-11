package gf.game1606.block;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import gf.game1606.Application;
import gf.game1606.R;

public class BlockManager
{
	// 5 colors refer to material design style
	public static final int[] color = {
			R.color.DeepOrange,
			R.color.Amber,
			R.color.LightGreen,
			R.color.LightBlue,
			R.color.Indigo,
			R.color.Gray
	};

	private static final String REMOVING_STATE_NOT_YET = "REMOVING_STATE_NOT_YET";
	private static final String REMOVING_STATE_START_REMOVING = "REMOVING_STATE_START_REMOVING";
	private static final String REMOVING_STATE_NOW_REMOVING = "REMOVING_STATE_NOW_REMOVING";

	private Block[][] blocks = new Block[Application.BLOCK_NUM][Application.BLOCK_NUM];
	private int[][] blockLevels = new int[Application.BLOCK_NUM][Application.BLOCK_NUM];
	private Block[] nextBlocks = new Block[Application.BLOCK_NUM];
	private ArrayList<Block> selectedBlocks = new ArrayList<>();
	private ArrayList<Block> removeBlocks = new ArrayList<>();
	// block

	private Context context;
	private RelativeLayout relativeLayout;
	private TextView scoreTextView;
	// game

	public Boolean playing = false;
	private String playingMode;
	public Boolean realTimeThreadIsRunning = true;
	private Handler realTimeThreadHandler;
	private ThreadRunnable realTimeThreadRunnable;
	private Thread realTimeThread;
	private int realTimeThreadSleepTime = 32;

	private Boolean OOPS = false;

	public BlockManager(Context context, RelativeLayout relativeLayout, String playingMode, TextView scoreTextView)
	{
		if (context == null)
			return;
		if (relativeLayout == null)
			return;
		if (playingMode == null)
			return;
		else if (playingMode.equals("game") || playingMode.equals("tutorial"))
			this.playingMode = playingMode;
		else
			return;

		this.context = context;
		this.relativeLayout = relativeLayout;
		this.scoreTextView = scoreTextView;

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

		if (playingMode.equals("game"))
			loadData();
		else if (playingMode.equals("tutorial"))
			setTutorialBlocks();

		playing = true;
	}

	public void initialize()
	{
		int i, j;

		for (i = 0; i < Application.BLOCK_NUM; i++)
		{
			for (j = 0; j < Application.BLOCK_NUM; j++)
			{
				if (blocks[i][j] == null) return;
				if (blocks[i][j].getAlpha() < 1) return;
			}
		}

		Application.setToScore(0);

		// Initialize nextBlocks.
		for (j = 0; j < Application.BLOCK_NUM; j++)
		{
			if (nextBlocks[j] != null)
				relativeLayout.removeView(nextBlocks[j]);
			nextBlocks[j] = newRandomBlock(j);
		}

		// Initialize blocks.
		for (i = 0; i < Application.BLOCK_NUM; i++)
			for (j = 0; j < Application.BLOCK_NUM; j++)
				removeBlock(i, j);

		// Initialize selectedBlocks.
		selectedBlocks = new ArrayList<>();

		// Initialize removeBlocks.
		removeBlocks = new ArrayList<>();
	}

	public void start()
	{
		realTimeThread.start();
	}

	private class ThreadRunnable implements Runnable
	{
		public void run()
		{
			if (playingMode.equals("game"))
				gameThreadRun();
			else if (playingMode.equals("tutorial"))
				tutorialThreadRun();
		}
	}

	private void gameThreadRun()
	{
		boolean needToUpdate = false;

		int i, j;
		boolean needToSave = false;
		for (i = 0; i < Application.BLOCK_NUM; i++)
		{
			for (j = 0; j < Application.BLOCK_NUM; j++)
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
						blocks[i][j] = blocks[i - 1][j];
						blocks[i - 1][j] = null;
					}
					blocks[i][j].setPivotY(Application.getBLOCK_SIZE() / 2);
					if (blocks[i][j].getRemovingState().equals(REMOVING_STATE_NOT_YET))
						blocks[i][j].setToScaleXY(1, 1);
					blocks[i][j].setIJ(i, j);
					blocks[i][j].setToX(Block.getXPosition(j));
					blocks[i][j].setToY(Block.getYPosition(i));
					blockLevels[i][j] = blocks[i][j].getLevel();

					blocks[i][j].update();
					needToSave = true;
				}
				else if (blocks[i][j].isNeedToUpdate())
				{
					needToUpdate = true;
					blocks[i][j].update();
				}
			}
		}
		if (needToSave) saveData();

		for (i = 0; i < Application.BLOCK_NUM; i++)
		{
			if (nextBlocks[i].isNeedToUpdate())
			{
				needToUpdate = true;
				nextBlocks[i].update();
			}
		}

		if (removeBlocks.size() > 0)
		{
			needToUpdate = true;
			if (removeBlocks.get(0).getRemovingState().equals(REMOVING_STATE_START_REMOVING))
			{
				Application.getSoundPool().play(Application.getSoundID(), 1, 1, 0, 0, 1);
				removeBlocks.get(0).setRemovingState(REMOVING_STATE_NOW_REMOVING);
				removeBlocks.get(0).setToAlpha(-0.2f);
			}
			if (removeBlocks.get(0).getAlpha() < 0.025f)
			{
				removeBlock(removeBlocks.get(0).getI(), removeBlocks.get(0).getJ());
				removeBlocks.remove(0);
			}
		}

		if (Application.getScore() != Application.getToScore())
		{
			needToUpdate = true;
			if (Application.getScore() < Application.getToScore() - 2)
				Application.setScore(Application.getScore() + (int) ((Application.getToScore() - Application.getScore()) * 0.2) + 1);
			else if (Application.getScore() > Application.getToScore() + 2)
				Application.setScore(Application.getScore() + (int) ((Application.getToScore() - Application.getScore()) * 0.2) - 1);
			else
				Application.setScore(Application.getToScore());
			scoreTextView.setText(String.valueOf(Application.getScore()));
		}

		if (OOPS)
		{
			Toast.makeText(context, "Oops!", Toast.LENGTH_SHORT).show();
			OOPS = false;
		}

		if (!needToUpdate)
			realTimeThreadSleepTime = 100;
		else if (realTimeThreadSleepTime == 100)
			realTimeThreadSleepTime = 32;
	}

	private void tutorialThreadRun()
	{
		boolean needToUpdate = false;
		int i, j;
		for (i = 0; i < Application.BLOCK_NUM; i++)
		{
			for (j = 0; j < Application.BLOCK_NUM; j++)
			{
				if (blocks[i][j].isNeedToUpdate())
				{
					needToUpdate = true;
					blocks[i][j].update();
				}
			}
		}
		if (!needToUpdate)
			realTimeThreadSleepTime = 500;
		else if (realTimeThreadSleepTime == 500)
			realTimeThreadSleepTime = 32;
	}

	private void setTutorialBlocks()
	{
		int i, j;
		for (j = 0; j < Application.BLOCK_NUM; j++)
		{
			nextBlocks[j] = newBlock(j, (int) (Math.random() * 3));
		}
		for (i = 0; i < Application.BLOCK_NUM; i++)
		{
			for (j = 0; j < Application.BLOCK_NUM; j++)
			{
				blocks[i][j] = newBlock(j, (int) (Math.random() * 3));
				blocks[i][j].setPivotY(Application.getBLOCK_SIZE() / 2);
				blocks[i][j].setToScaleXY(1, 1);
				blocks[i][j].setIJ(i, j);
				blocks[i][j].setToX(Block.getXPosition(j));
				blocks[i][j].setToY(Block.getYPosition(i));
			}
		}
	}

	public Block newRandomBlock(int j)
	{
		return newBlock(j, (Math.random() > 0.025) ? (Application.COLOR_NUM - 2) - (int) (Math.random() * (Application.COLOR_NUM - 1)) : Application.COLOR_NUM - 1);
	}

	public Block newBlock(int j, int level)
	{
		Block block = new Block(context, (int) Application.getBLOCK_SIZE(), Block.getXPosition(j), Block.getYPosition(-1), level);
		block.setX(block.getToX());
		block.setIJ(-1, j);
		block.setPivotY(Application.getBLOCK_SIZE());
		block.setToScaleY(0.2f);

		relativeLayout.addView(block, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		return block;
	}

	public void removeBlock(int i, int j)
	{
		if (blocks[i][j] == null) return;
		relativeLayout.removeView(blocks[i][j]);
		blocks[i][j] = null;
	}

	public void loadData()
	{
		Application.setIntegerDataList();

		int tempInt, i, j;
		for (j = 0; j < Application.BLOCK_NUM; j++)
		{
			tempInt = Application.getIntegerDataList().get(j);
			if (tempInt == 0)
				nextBlocks[j] = newRandomBlock(j);
			else
				nextBlocks[j] = newBlock(j, tempInt - 1);
		}
		for (i = 0; i < Application.BLOCK_NUM; i++)
		{
			for (j = 0; j < Application.BLOCK_NUM; j++)
			{
				tempInt = Application.getIntegerDataList().get(Application.BLOCK_NUM * (i + 1) + j);
				if (tempInt == 0)
				{
					blocks[i][j] = null;
					blockLevels[i][j] = -1;
				}
				else
				{
					blocks[i][j] = newBlock(j, tempInt - 1);
					blocks[i][j].setPivotY(Application.getBLOCK_SIZE() / 2);
					blocks[i][j].setToScaleXY(1, 1);
					blocks[i][j].setIJ(i, j);
					blocks[i][j].setToX(Block.getXPosition(j));
					blocks[i][j].setToY(Block.getYPosition(i));
					blockLevels[i][j] = blocks[i][j].getLevel();
				}
			}
		}
	}

	public void saveData()
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

		Application.setLoadedData(stringBuilder.substring(0));

		FileOutputStream outputStream;
		try
		{
			outputStream = context.openFileOutput(Application.FILENAME, Context.MODE_PRIVATE);
			outputStream.write(Application.getLoadedData().getBytes());
			outputStream.close();
		}
		catch (Exception er)
		{
			er.printStackTrace();
			Toast.makeText(context, "Error code: DE2\nCannot use userData", Toast.LENGTH_SHORT).show();
		}
	}

	public boolean onTouch(View v, MotionEvent event)
	{
		// Do not get event while blocks are removing
		if (removeBlocks.size() > 0)
			return false;

		int i, j, k;

		// Do not get event while blocks are removing
		for (i = 0; i < Application.BLOCK_NUM; i++)
			for (j = 0; j < Application.BLOCK_NUM; j++)
				if (blocks[i][j] == null)
					return false;

		// Get event
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				// Exception
				if (selectedBlocks.size() != 0)
					return false;

				for (i = 0; i < Application.BLOCK_NUM; i++)
				{
					for (j = 0; j < Application.BLOCK_NUM; j++)
					{
						if (blocks[i][j].isHit(event.getX(), event.getY()))
						{
							blocks[i][j].select(0);
							selectedBlocks.add(blocks[i][j]);
							BlockPathFinder.setAvailableBlocks(blockLevels, selectedBlocks, selectedBlocks.get(0).getLevel());
							return true;
						}
					}
				}
				return true;

			case MotionEvent.ACTION_MOVE:
				// Exception
				if (selectedBlocks.size() == 0) return false;

				for (i = 0; i < Application.BLOCK_NUM; i++)
				{
					for (j = 0; j < Application.BLOCK_NUM; j++)
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
									BlockPathFinder.setAvailableBlocks(blockLevels, selectedBlocks, selectedBlocks.get(0).getLevel());
								}
								else
								{
									List<int[]> list = BlockPathFinder.findPath(
											selectedBlocks.get(selectedBlocks.size() - 1).getI(),
											selectedBlocks.get(selectedBlocks.size() - 1).getJ(),
											i,
											j);
									if (list != null)
									{
										if (list.size() != 0)
										{
											for (int[] index : list)
											{
												blocks[index[0]][index[1]].select(selectedBlocks.size());
												selectedBlocks.add(blocks[index[0]][index[1]]);
											}
											BlockPathFinder.setAvailableBlocks(blockLevels, selectedBlocks, selectedBlocks.get(0).getLevel());
										}
									}
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
						if (k != Application.COLOR_NUM)
						{
							int plusScore = (int) (Math.pow(selectedBlocks.size(), 1.9 + 2.0 / (k + 4)) * 100 / (k + 4));
							Application.setToScore(Application.getToScore() + plusScore);
							Application.setTotalScore(Application.getTotalScore() + plusScore);
							if (Application.getToScore() > Application.getHighScore())
								Application.setHighScore(Application.getToScore());

							for (i = 0; i < Application.BLOCK_NUM; i++)
							{
								for (j = 0; j < Application.BLOCK_NUM; j++)
								{
									if (blocks[i][j].getLevel() == k && !blocks[i][j].isSelected())
									{
										if (blocks[i][j].getLevel() == Application.COLOR_NUM - 1)
											OOPS = true;
										blocks[i][j].setLevel(blocks[i][j].getLevel() + 1);
										blockLevels[i][j] = blocks[i][j].getLevel();
									}
								}
							}

							while (selectedBlocks.size() > 0)
							{
								selectedBlocks.get(0).setRemovingState(REMOVING_STATE_START_REMOVING);
								removeBlocks.add(selectedBlocks.remove(0));
							}

							if (removeBlocks.size() > 0)
								saveData();
						}
					}
				}
				selectedBlocks = new ArrayList<>();
				for (i = 0; i < Application.BLOCK_NUM; i++)
				{
					for (j = 0; j < Application.BLOCK_NUM; j++)
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

	// A star?

	/*private ArrayList<int[]> getPath(int[] startBlock, int[] endBlock, int level)
	{
		if (getBlockDistance(startBlock, endBlock) == 1)
		{
			return new ArrayList<>(Arrays.asList(startBlock, endBlock));
		}

		int[] searchingQueue = {0, 0, 0, 0};
		double[] blockDistances = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
		if (isConnectableBlock(startBlock[0] - 1, startBlock[1], level))
			blockDistances[0] = getBlockDistance(startBlock[0] - 1, startBlock[1], endBlock[0], endBlock[1]);
		if (isConnectableBlock(startBlock[0] + 1, startBlock[1], level))
			blockDistances[1] = getBlockDistance(startBlock[0] + 1, startBlock[1], endBlock[0], endBlock[1]);
		if (isConnectableBlock(startBlock[0], startBlock[1] - 1, level))
			blockDistances[2] = getBlockDistance(startBlock[0], startBlock[1] - 1, endBlock[0], endBlock[1]);
		if (isConnectableBlock(startBlock[0], startBlock[1] + 1, level))
			blockDistances[3] = getBlockDistance(startBlock[0], startBlock[1] + 1, endBlock[0], endBlock[1]);
		return;
	}*/

	private boolean isConnectableBlock(int i, int j, int level)
	{
		if ((i < 0 || i >= Application.BLOCK_NUM) || (j < 0 || j >= Application.BLOCK_NUM))
			return false;
		else
			return blockLevels[i][j] == level;
	}

	private double getBlockDistance(int[] block1, int[] block2)
	{
		return Math.pow(block1[0] - block2[0], 2) + Math.pow(block1[1] - block2[1], 2);
	}
	private double getBlockDistance(int i1, int j1, int i2, int j2)
	{
		return Math.pow(i1 - i2, 2) + Math.pow(j1 - j2, 2);
	}
}