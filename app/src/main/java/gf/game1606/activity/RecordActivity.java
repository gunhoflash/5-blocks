package gf.game1606.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import gf.game1606.Application;
import gf.game1606.R;

public class RecordActivity extends AppCompatActivity
{
	private ConstraintLayout constraintLayout;
	private ConstraintSet constraintSet;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("RecordActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		constraintLayout = findViewById(R.id.activity_record);
		constraintSet = new ConstraintSet();
		constraintSet.clone(constraintLayout);
		initializeLayout();
	}

	private void initializeLayout()
	{
		TextView titleText;
		TextView totalScoreText;
		TextView totalScoreValueText;
		TextView highScoreText;
		TextView highScoreValueText;
		TextView toBeContinuedText;

		//

		titleText = new TextView(this);
		titleText.setId(View.generateViewId());
		titleText.setText("RECORD");
		titleText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36);
		titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		constraintLayout.addView(titleText);
		constraintSet.centerHorizontally(titleText.getId(), ConstraintSet.PARENT_ID);
		constraintSet.constrainWidth(titleText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.constrainHeight(titleText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.connect(titleText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 96);
		constraintSet.applyTo(constraintLayout);

		totalScoreText = new TextView(this);
		totalScoreText.setId(View.generateViewId());
		totalScoreText.setText("TOTAL SCORE");
		totalScoreText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		totalScoreText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		totalScoreText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		constraintLayout.addView(totalScoreText);
		constraintSet.constrainWidth(totalScoreText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.constrainHeight(totalScoreText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.connect(totalScoreText.getId(), ConstraintSet.TOP, titleText.getId(), ConstraintSet.BOTTOM, 96);
		constraintSet.applyTo(constraintLayout);

		//

		totalScoreValueText = new TextView(this);
		totalScoreValueText.setId(View.generateViewId());
		totalScoreValueText.setText(String.valueOf(Application.getTotalScore()));
		totalScoreValueText.setTextColor(getResources().getColor(R.color.DeepOrange));
		totalScoreValueText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		totalScoreValueText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		totalScoreValueText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		constraintLayout.addView(totalScoreValueText);
		constraintSet.constrainWidth(totalScoreValueText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.constrainHeight(totalScoreValueText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.connect(totalScoreValueText.getId(), ConstraintSet.TOP, totalScoreText.getId(), ConstraintSet.TOP);
		constraintSet.connect(totalScoreValueText.getId(), ConstraintSet.LEFT, totalScoreText.getId(), ConstraintSet.RIGHT);
		constraintSet.applyTo(constraintLayout);

		constraintSet.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
				new int[]{totalScoreText.getId(), totalScoreValueText.getId()}, null, ConstraintSet.CHAIN_SPREAD);
		constraintSet.applyTo(constraintLayout);

		highScoreText = new TextView(this);
		highScoreText.setId(View.generateViewId());
		highScoreText.setText("HIGH SCORE");
		highScoreText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		highScoreText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		highScoreText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		constraintLayout.addView(highScoreText);
		constraintSet.constrainWidth(highScoreText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.constrainHeight(highScoreText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.connect(highScoreText.getId(), ConstraintSet.TOP, totalScoreText.getId(), ConstraintSet.BOTTOM, 36);
		constraintSet.connect(highScoreText.getId(), ConstraintSet.LEFT, totalScoreText.getId(), ConstraintSet.LEFT);
		constraintSet.applyTo(constraintLayout);

		highScoreValueText = new TextView(this);
		highScoreValueText.setId(View.generateViewId());
		highScoreValueText.setText(String.valueOf(Application.getHighScore()));
		highScoreValueText.setTextColor(getResources().getColor(R.color.DeepOrange));
		highScoreValueText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		highScoreValueText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		highScoreValueText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		constraintLayout.addView(highScoreValueText);
		constraintSet.constrainWidth(highScoreValueText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.constrainHeight(highScoreValueText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.connect(highScoreValueText.getId(), ConstraintSet.TOP, highScoreText.getId(), ConstraintSet.TOP);
		constraintSet.connect(highScoreValueText.getId(), ConstraintSet.RIGHT, totalScoreValueText.getId(), ConstraintSet.RIGHT);
		constraintSet.applyTo(constraintLayout);

		//

		toBeContinuedText = new TextView(this);
		toBeContinuedText.setId(View.generateViewId());
		toBeContinuedText.setText("Want more record?\nShare idea to :\ngunhoflash@gmail.com");
		toBeContinuedText.setTextColor(getResources().getColor(R.color.Gray));
		toBeContinuedText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
		toBeContinuedText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		toBeContinuedText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		constraintLayout.addView(toBeContinuedText);
		constraintSet.centerHorizontally(toBeContinuedText.getId(), ConstraintSet.PARENT_ID);
		constraintSet.constrainWidth(toBeContinuedText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.constrainHeight(toBeContinuedText.getId(), ConstraintSet.WRAP_CONTENT);
		constraintSet.connect(toBeContinuedText.getId(), ConstraintSet.TOP, highScoreText.getId(), ConstraintSet.BOTTOM);
		constraintSet.connect(toBeContinuedText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
		constraintSet.applyTo(constraintLayout);

		//

	}
}