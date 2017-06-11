package gf.game1606.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gf.game1606.Application;
import gf.game1606.R;

public class RecordActivity extends AppCompatActivity
{
	private RelativeLayout relativeLayout;
	private TextView titleText;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("RecordActivity onCreate");
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		relativeLayout = new RelativeLayout(this);
		relativeLayout.setBackgroundColor(Color.WHITE);
		relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		this.setContentView(relativeLayout);

		initializeLayout();
	}

	private void initializeLayout()
	{
		float rotation = -26f;
		double toX, toY;

		titleText = new TextView(this);
		titleText.setText("RECORD");
		titleText.setTextColor(Color.parseColor("#414141"));
		titleText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 56);
		titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		titleText.setSingleLine();
		titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		//titleText.setX(Application.getWIDTH() / 2);
		//titleText.setY(Application.getHEIGHT() / 5);
		relativeLayout.addView(titleText, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}
}