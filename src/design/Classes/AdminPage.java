package design.Classes;

import utilities.CheckURL;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import components.DatabaseHandler.AdministrativeDatabase;

import config.Variables.Constants;

public class AdminPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		titleText.setText("Administrative Panel");
		titleText.setPadding(0, 0, 0, 50);
		titleText.setTextSize(20);
		titleText.setGravity(Gravity.CENTER_HORIZONTAL);

		TextView databaseStatusHead = new TextView(this);
		databaseStatusHead.setSingleLine(true);
		databaseStatusHead.setText("Database current status");
		databaseStatusHead.setPadding(0, 0, 0, 20);
		databaseStatusHead.setTextSize(15);
		databaseStatusHead.setGravity(Gravity.LEFT);

		TextView databaseStatusBody = new TextView(this);
		databaseStatusBody.setSingleLine(true);
		databaseStatusBody.setText("Database body");
		databaseStatusBody.setPadding(0, 0, 0, 20);
		databaseStatusBody.setTextSize(15);
		databaseStatusBody.setGravity(Gravity.LEFT);

		TextView connectionUrlHead = new TextView(this);
		connectionUrlHead.setSingleLine(true);
		connectionUrlHead.setText("Connection URL");
		connectionUrlHead.setPadding(0, 0, 0, 20);
		connectionUrlHead.setTextSize(15);
		connectionUrlHead.setGravity(Gravity.LEFT);

		Button testConnection = new Button(this);
		testConnection.setText("Test connection");
		testConnection.setGravity(Gravity.CENTER_HORIZONTAL);

		final EditText urlText = new EditText(this);
		urlText.setInputType(InputType.TYPE_CLASS_TEXT);
		AdministrativeDatabase adminDb = new AdministrativeDatabase(
				Constants.databaseName, Constants.adminTable,
				getApplicationContext());
		urlText.setText(adminDb.getURL());

		testConnection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					new CheckURL(urlText.getText().toString(),
							getApplicationContext()).execute();
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "invalid url",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		Button saveAndExit = new Button(this);
		saveAndExit.setText("Save and Exit");
		saveAndExit.setGravity(Gravity.CENTER_HORIZONTAL);

		// layoutParams.setMargins(100, 500, 100, 200);

		currentLineraLayout.addView(titleText, layoutParams);
		currentLineraLayout.addView(databaseStatusHead);
		currentLineraLayout.addView(databaseStatusBody);
		currentLineraLayout.addView(connectionUrlHead);
		currentLineraLayout.addView(urlText);
		currentLineraLayout.addView(testConnection, layoutParams);
		currentLineraLayout.addView(saveAndExit);

		saveAndExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(AdminPage.this, ServiceHandling.class));
				finish();

			}

		});
		this.setContentView(currentScrollView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add("About");

		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent aboutIntent = new Intent(AdminPage.this, AboutPage.class);
				try {
					startActivity(aboutIntent);
				} catch (Exception e) {
					Log.d("EXCEPTION", e.toString());
				}
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

}
