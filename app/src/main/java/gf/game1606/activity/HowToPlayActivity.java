package gf.game1606.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import gf.game1606.Application;
import gf.game1606.R;
import gf.game1606.block.Block;
import gf.game1606.block.BlockManager;

public class HowToPlayActivity extends AppCompatActivity implements View.OnTouchListener
{
	private BlockManager blockManager;
	private RelativeLayout relativeLayout;
	private TextView textView;

	private ArrayList tutorialTextStrings_kr = new ArrayList<>(Arrays.asList("튜토리얼을 시작합니다\n(화면을 터치하세요)",
			"같은 색 블록을\n연결하여 제거합니다",
			"제거되지 않은 같은 색 블록들은\n색이 바뀝니다",
			"빨간색 - 노란색 - 초록색\n- 파란색 - 남색 - 검은색",
			"검은색 블록은 제거할 수 없으니\n주의하십시오"));
	private ArrayList tutorialTextStrings_en = new ArrayList<>(Arrays.asList("Start tutorial\n(Touch to next)",
			"Connect the same color blocks and remove them",
			"The same color blocks that are not removed will change their color",
			"Red - Yellow - Green\n- Blue - Navy - Black",
			"Be careful!\nBlack block can not be removed"));
	private ArrayList tutorialTextStrings;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_how_to_play);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		relativeLayout = (RelativeLayout) findViewById(R.id.activity_how_to_play);
		if (relativeLayout != null)
			relativeLayout.setOnTouchListener(this);

		blockManager = new BlockManager(this, relativeLayout, "tutorial", null);

		System.out.println("language: " + Application.LANGUAGE);
		if (Application.LANGUAGE == null)
			tutorialTextStrings = tutorialTextStrings_en;
		else if (Application.LANGUAGE.equals("ko"))
			tutorialTextStrings = tutorialTextStrings_kr;
		else
			tutorialTextStrings = tutorialTextStrings_en;

		textView = new TextView(this);
		textView.setY(Block.getYPosition(5));
		textView.setText((String) tutorialTextStrings.get(0));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		relativeLayout.addView(textView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		textView.setBackgroundColor(Color.parseColor("#EAEAEA"));

		blockManager.playing = true;
		blockManager.start();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		for (int i = 0; i < tutorialTextStrings.size(); i++)
		{
			if (i == tutorialTextStrings.size() - 1)
			{
				this.finish();
				break;
			}
			if (tutorialTextStrings.get(i).equals(textView.getText()))
			{
				textView.setText((String) tutorialTextStrings.get(i + 1));
				break;
			}
		}
		return false;
	}
}