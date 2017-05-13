package gf.game1606.block;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import gf.game1606.Application;

public class Block extends View
{
	private static final int UN_SELECTED_INDEX = -1;
	private Context context;
	private boolean needToUpdate;

	private int blockSize;
	private int color = Color.WHITE;
	private int level; // 0 ~ BlockManager.color.length-1
	private int i, j;

	private int toColor;
	private int toX, toY;
	private float toAlpha = 1;
	private float toScaleX = 1;
	private float toScaleY = 1;

	private Rect hitBox;
	private int selectedIndex = UN_SELECTED_INDEX;

	public Block(Context context, Number blockSize, Number toX, Number toY, int level)
	{
		super(context);
		if (blockSize.intValue() == 0) return;

		this.context = context;
		this.blockSize = blockSize.intValue();
		this.level = level;
		this.toColor = ContextCompat.getColor(context, BlockManager.color[level]);

		setClipBounds(new Rect(0, 0, this.blockSize, this.blockSize));
		setEnabled(false);
		setToX(toX);
		setToY(toY);
		setPivotX(this.blockSize / 2);
		setPivotY(this.blockSize / 2);
		setAlpha(0);

		this.hitBox = new Rect(toX.intValue(), toY.intValue(), toX.intValue() + this.blockSize, toY.intValue() + this.blockSize);
	}

	public Block(Context context)
	{
		this(context, 0, 0, 0, 0);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		canvas.clipRect(0, 0, blockSize, blockSize);
		canvas.drawColor(color);
		this.invalidate();
	}

	public void update()
	{
		if (isNeedToUpdate())
		{
			boolean isUpdated = false;
			hitBox.set((int) getX(), (int) getY(), (int) getX() + blockSize, (int) getY() + blockSize);

			if (i != -1)
			{
				this.toColor = ContextCompat.getColor(context, BlockManager.color[level]);
				if (selectedIndex != UN_SELECTED_INDEX)
					setToScaleXY(1.2, 1.2);
				else
					setToScaleXY(1, 1);
			}

			if (color != toColor)
			{
				int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};
				int[] torgb = {Color.red(toColor), Color.green(toColor), Color.blue(toColor)};
				for (int i = 0; i < 3; i++)
				{
					if (rgb[i] > torgb[i] + 6)
					{
						rgb[i] -= 6;
						isUpdated = true;
					}
					else if (rgb[i] < torgb[i] - 6)
					{
						rgb[i] += 6;
						isUpdated = true;
					}
					else rgb[i] = torgb[i];
				}
				color = Color.rgb(rgb[0], rgb[1], rgb[2]);
			}

			if (getX() != getToX())
			{
				if (Math.abs(getToX() - getX()) < 1)
					setX(getToX());
				else
				{
					setX(getX() + (getToX() - getX()) * 0.35f);
					isUpdated = true;
				}
			}
			if (getY() != getToY())
			{
				if (Math.abs(getToY() - getY()) < 1)
					setY(getToY());
				else
				{
					setY(getY() + (getToY() - getY()) * 0.35f);
					isUpdated = true;
				}
			}

			if (getAlpha() != getToAlpha())
			{
				if (Math.abs(getToAlpha() - getAlpha()) < 0.06f)
					setAlpha(getToAlpha());
				else
				{
					setAlpha(getAlpha() + (getToAlpha() - getAlpha()) * 0.55f);
					isUpdated = true;
				}
			}

			if (getScaleX() != getToScaleX())
			{
				if (Math.abs(getScaleX() - getToScaleX()) < 0.01f)
					setScaleX(getToScaleX());
				else
				{
					setScaleX(getScaleX() + (getToScaleX() - getScaleX()) * 0.5f);
					isUpdated = true;
				}
			}
			if (getScaleY() != getToScaleY())
			{
				if (Math.abs(getScaleY() - getToScaleY()) < 0.01f)
					setScaleY(getToScaleY());
				else
				{
					setScaleY(getScaleY() + (getToScaleY() - getScaleY()) * 0.5f);
					isUpdated = true;
				}
			}

			needToUpdate = isUpdated;
		}
	}

	public void setWidthHeight()
	{
		ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		try
		{
			params = this.getLayoutParams();
		}
		catch (Exception e)
		{
			params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			System.out.println("Error: Block - setWidthHeight");
		}
		finally
		{
			params.width = blockSize;
			params.height = blockSize;
			this.setLayoutParams(params);
		}
	}

	// update
	public boolean isNeedToUpdate()
	{
		return this.needToUpdate;
	}

	// select
	public void select(int selectedIndex)
	{
		this.selectedIndex = selectedIndex;
		needToUpdate = true;
	}

	public void unSelect()
	{
		this.select(UN_SELECTED_INDEX);
	}

	public boolean isSelected()
	{
		return (this.selectedIndex != UN_SELECTED_INDEX);
	}

	// getter
	public int getToColor()
	{
		return this.toColor;
	}

	public int getLevel()
	{
		return this.level;
	}

	public int getI()
	{
		return this.i;
	}

	public int getJ()
	{
		return this.j;
	}

	public int getToX()
	{
		return this.toX;
	}

	public int getToY()
	{
		return this.toY;
	}

	public float getToAlpha()
	{
		return this.toAlpha;
	}

	public float getToScaleX()
	{
		return this.toScaleX;
	}

	public float getToScaleY()
	{
		return this.toScaleY;
	}

	public int getSelectedIndex()
	{
		return this.selectedIndex;
	}

	// setter
	public void setToColor(int toColor)
	{
		this.toColor = toColor;
		needToUpdate = true;
	}

	public void setLevel(int level)
	{
		this.level = level;
		needToUpdate = true;
	}

	public void setIJ(int i, int j)
	{
		this.i = i;
		this.j = j;
	}

	public void setToX(Number toX)
	{
		this.toX = toX.intValue();
		needToUpdate = true;
	}

	public void setToY(Number toY)
	{
		this.toY = toY.intValue();
		needToUpdate = true;
	}

	public void setToAlpha(Number toAlpha)
	{
		this.toAlpha = toAlpha.floatValue();
		needToUpdate = true;
	}

	public void setToScaleX(Number toScaleX)
	{
		this.toScaleX = toScaleX.floatValue();
		needToUpdate = true;
	}

	public void setToScaleY(Number toScaleY)
	{
		this.toScaleY = toScaleY.floatValue();
		needToUpdate = true;
	}

	public void setToScaleXY(Number toScaleX, Number toScaleY)
	{
		setToScaleX(toScaleX);
		setToScaleY(toScaleY);
	}

	public boolean isHit(Number x, Number y)
	{
		return this.hitBox.contains(x.intValue(), y.intValue());
	}

	static boolean isNear(Block block1, Block block2)
	{
		return ((Math.abs(block1.i - block2.i) == 1 && block1.j == block2.j) || (Math.abs(block1.j - block2.j) == 1 && block1.i == block2.i));
	}

	static public int getXPosition(float i, int blockNum)
	{
		return (int) (i * (Application.getBLOCK_SIZE() + Application.getBLOCK_GAP()) + (Application.getWIDTH() - (Application.getBLOCK_SIZE() + Application.getBLOCK_GAP()) * blockNum) * 0.5f + Application.getBLOCK_GAP() / 2f);
	}

	static public int getYPosition(float i, int blockNum)
	{
		return (int) (i * (Application.getBLOCK_SIZE() + Application.getBLOCK_GAP()) + (Application.getHEIGHT() - (Application.getBLOCK_SIZE() + Application.getBLOCK_GAP()) * blockNum) * 0.5f + Application.getBLOCK_GAP() / 2f);
	}

	static public int getXPosition(float i)
	{
		return getXPosition(i, Application.BLOCK_NUM);
	}

	static public int getYPosition(float i)
	{
		return getYPosition(i, Application.BLOCK_NUM);
	}
}