package gf.game1606.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gf.game1606.Application;
import gf.game1606.R;
import gf.game1606.block.Block;
import gf.game1606.block.BlockManager;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener
{
	private BlockManager blockManager;

	private RelativeLayout relativeLayout;
	private ImageView replay;
	private TextView scoreTextView;
	private TextView GFtext;
	// game

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("onCreate");
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		relativeLayout = new RelativeLayout(this);
		relativeLayout.setBackgroundColor(Color.parseColor("#FAFAFA"));
		relativeLayout.setOnTouchListener(this);
		relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		setContentView(relativeLayout);

		scoreTextView = new TextView(this);
		scoreTextView.setText(String.valueOf(Application.getScore()));
		scoreTextView.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
		scoreTextView.setSingleLine();
		scoreTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		scoreTextView.setY(Block.getYPosition(0) / 2f);
		relativeLayout.addView(scoreTextView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		GFtext = new TextView(this);
		GFtext.setText("GF");
		GFtext.setTextColor(Color.parseColor("#CDCDCD"));
		GFtext.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		GFtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		GFtext.setSingleLine();
		GFtext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		GFtext.setY((float) (Application.getHEIGHT() - 48 * Application.getRATIO()));
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
				blockManager.initialize();
			}
		});
		relativeLayout.addView(replay, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		blockManager = new BlockManager(this, relativeLayout, "game", scoreTextView);
		blockManager.playing = true;
		blockManager.start();
	}

	@Override
	public void onStop()
	{
		System.out.println("onStop");
		blockManager.playing = false;
		blockManager.realTimeThreadIsRunning = false;
		finish();
		super.onStop();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		return blockManager.onTouch(v, event);
	}
}