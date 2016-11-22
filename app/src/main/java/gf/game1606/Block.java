package gf.game1606;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import static gf.game1606.AppActivity.blockGap;
import static gf.game1606.AppActivity.blockNum;

public class Block extends View
{
	private int blockSize;
	private int color = Color.WHITE;
	public int level; // 0 ~ ColorSet.color.length-1
	public int i, j;
	public int toColor;
	private float toScaleX = 1;
	private float toScaleY = 1;
	private float toAlpha = 1;
	private int toX, toY;

	public Rect rect;
	public boolean selected = false;
	public int selectedIndex = -1;

	public Block(Context context, int blockSize, int toX, int toY, int level)
	{
		super(context);
		if (blockSize == 0) return;

		this.blockSize = blockSize;
		this.level = level;
		this.toColor = ColorSet.color[level];

		setClipBounds(new Rect(0, 0, blockSize, blockSize));
		setEnabled(false);
		setToX(toX);
		setToY(toY);
		setPivotX(blockSize/2);
		setPivotY(blockSize/2);
		setAlpha(0);

		rect = new Rect(toX, toY, toX+blockSize, toY+blockSize);
	}
	public Block(Context context, Number blockSize, Number x, Number y, int level)
	{
		this(context, (int) blockSize, (int) x, (int) y, level);
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
		rect.left   = (int) getX();
		rect.right  = (int) getX() + blockSize;
		rect.top    = (int) getY();
		rect.bottom = (int) getY() + blockSize;

		if (i != -1)
		{
			toColor = ColorSet.color[level];
			if (selectedIndex != -1)
				setToScaleXY(1.2, 1.2);
			else
				setToScaleXY(1, 1);
		}

		if (getScaleX() != getToScaleX())
		{
			if (Math.abs(getScaleX() - getToScaleX()) < 0.01f)
				setScaleX(getToScaleX());
			else
				setScaleX(getScaleX() + (getToScaleX() - getScaleX()) * 0.5f);
		}
		if (getScaleY() != getToScaleY())
		{
			if (Math.abs(getScaleY() - getToScaleY()) < 0.01f)
				setScaleY(getToScaleY());
			else
				setScaleY(getScaleY() + (getToScaleY() - getScaleY()) * 0.5f);
		}

		if (getX() != getToX())
		{
			if (Math.abs(getToX() - getX()) < 1)
				setX(getToX());
			else
				setX(getX() + (getToX()-getX())*0.35f);
		}
		if (getY() != getToY())
		{
			if (Math.abs(getToY() - getY()) < 1)
				setY(getToY());
			else
				setY(getY() + (getToY()-getY())*0.35f);
		}

		if (getAlpha() != getToAlpha())
		{
			if (Math.abs(getToAlpha() - getAlpha()) < 0.06f)
				setAlpha(getToAlpha());
			else
				setAlpha(getAlpha() + (getToAlpha()-getAlpha())*0.55f);
		}

		if (color != toColor)
		{
			int[] rgb   = { Color.red(color)  , Color.green(color)  , Color.blue(color)   };
			int[] torgb = { Color.red(toColor), Color.green(toColor), Color.blue(toColor) };
			for (int i = 0; i < 3; i++)
			{
					 if (torgb[i] < rgb[i] - 6) rgb[i] -= 6;
				else if (torgb[i] > rgb[i] + 6) rgb[i] += 6;
				else rgb[i] = torgb[i];
			}
			color = Color.rgb(rgb[0], rgb[1], rgb[2]);
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

	public float getToScaleX()
	{
		return this.toScaleX;
	}
	public float getToScaleY()
	{
		return this.toScaleY;
	}
	public void setToScaleX(Number toScaleX)
	{
		this.toScaleX = (float)toScaleX;
	}
	public void setToScaleY(Number toScaleY)
	{
		this.toScaleY = (float)toScaleY;
	}
	public void setToScaleXY(Number toScaleX, Number toScaleY)
	{
		setToScaleX(toScaleX);
		setToScaleY(toScaleY);
	}

	public float getToAlpha()
	{
		return this.toAlpha;
	}
	public void setToAlpha(Number toAlpha)
	{
		this.toAlpha = (float)toAlpha;
	}

	public int getToX()
	{
		return this.toX;
	}
	public int getToY()
	{
		return this.toY;
	}
	public void setToX(Number toX)
	{
		this.toX = (int)toX;
	}
	public void setToY(Number toY)
	{
		this.toY = (int)toY;
	}

	static boolean isNear(Block block1, Block block2)
	{
		return ((Math.abs(block1.i - block2.i) == 1 && block1.j == block2.j) || (Math.abs(block1.j - block2.j) == 1 && block1.i == block2.i));
	}

	static public int getXPosition(float i, int blockNum)
	{
		return (int) (i * (AppActivity.blockSize + blockGap) + (AppActivity.WIDTH  - (AppActivity.blockSize + blockGap) * blockNum) * 0.5f + blockGap / 2f);
	}
	static public int getYPosition(float i, int blockNum)
	{
		return (int) (i * (AppActivity.blockSize + blockGap) + (AppActivity.HEIGHT - (AppActivity.blockSize + blockGap) * blockNum) * 0.5f + blockGap / 2f);
	}
	static public int getXPosition(float i)
	{
		return getXPosition(i, blockNum);
	}
	static public int getYPosition(float i)
	{
		return getYPosition(i, blockNum);
	}
}



