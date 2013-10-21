package design.Classes;

import config.Variables.Variables;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AboutPage extends Activity {

	Button closeAboutSection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ScrollView currentScrollView = new ScrollView(this);

		final LinearLayout currentLineraLayout = new LinearLayout(this);

		currentLineraLayout.setOrientation(LinearLayout.VERTICAL);

		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);

		currentScrollView.addView(currentLineraLayout);

		TextView titleText = new TextView(this);
		titleText.setSingleLine(true);

		titleText.setText("Mobility Collector");
		titleText.setPadding(0, 0, 0, 50);
		titleText.setTextSize(20);
		titleText.setGravity(Gravity.CENTER_HORIZONTAL);

		TextView contentText = new TextView(this);
		contentText.setText(Variables.aboutSectionString.replace("***", ",").replace("****", "\n"));
		contentText.setTextSize(15);
		contentText.setGravity(Gravity.CENTER_HORIZONTAL);
		contentText.setPadding(0, 0, 0, 50);

		closeAboutSection = new Button(this);
		closeAboutSection.setGravity(Gravity.CENTER_HORIZONTAL);
		closeAboutSection.setText("Close");

		// layoutParams.setMargins(100, 500, 100, 200);

		currentLineraLayout.addView(titleText, layoutParams);
		currentLineraLayout.addView(contentText);
		currentLineraLayout.addView(closeAboutSection, layoutParams);

		closeAboutSection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AboutPage.this.finish();
			}
		});

		this.setContentView(currentScrollView);
	}
}
