package gf.game1606;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class HowToPlayActivity extends AppActivity implements View.OnTouchListener
{
	private RelativeLayout relativeLayout;
	private RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	private TextView textView;

	private ArrayList strings = new ArrayList<>(Arrays.asList("튜토리얼을 시작합니다.\n화면을 터치하세요",
	                                                          "같은 색 블록을 연결해 제거하십시오",
	                                                          "선택한 색과 같은 색의 나머지 블록들은 다음과 같이 색이 바뀝니다",
	                                                          "빨간색 - 노란색 - 초록색 - 파란색 - 남색 - 검은색",
	                                                          "검은색 블록은 제거할 수 없으니 주의하십시오"));

	private ArrayList<Block> blocks = new ArrayList<>();

	private Boolean        realTimeThreadIsRunning = true;
	private Handler        realTimeThreadHandler;
	private ThreadRunnable realTimeThreadRunnable;
	private Thread         realTimeThread;

	//private Timer

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_how_to_play);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		relativeLayout = (RelativeLayout) findViewById(R.id.activity_how_to_play);
		if (relativeLayout != null)
			relativeLayout.setOnTouchListener(this);

		int i, n = 9;

		for (i = 0; i < n; i++)
		{
			Block blockTemp = new Block(this, blockSize, Block.getXPosition(i%3,3), Block.getYPosition(i/3,3), 2);
			blockTemp.setPivotY(blockSize/2);
			relativeLayout.addView(blockTemp);
			blocks.add(blockTemp);
		}

		textView = new TextView(this);
		textView.setText((String)strings.get(0));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		relativeLayout.addView(textView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		textView.setBackgroundColor(Color.parseColor("#EAEAEA"));
		//textView.setAlpha(0.8f);
		textView.post(new Runnable() {
			@Override
			public void run()
			{
				textView.setY(HEIGHT*0.625f);
				for (int i = 0; i < blocks.size(); i++)
					blocks.get(i).setWidthHeight();
			}
		});

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
							Thread.sleep(50);
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
		realTimeThread.start();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		System.out.println("Touch");
		for (int i = 0; i < strings.size(); i++)
		{
			if (i == strings.size() - 1)
			{
				break;
			}
			if (strings.get(i).equals(textView.getText()))
			{
				textView.setText((String) strings.get(i + 1));
				break;
			}
		}
		return false;
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
		int i;
		for (i = 0; i < blocks.size(); i++)
		{
			blocks.get(i).update();
		}
	}
}
