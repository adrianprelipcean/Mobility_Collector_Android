package design.Classes;

import utilities.GetInfo;
import utilities.RegisterAsync;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
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

public class RegisterPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final GetInfo gI = new GetInfo(this);

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

		TextView emailText = new TextView(this);
		emailText.setSingleLine(true);
		emailText.setText("Username");
		emailText.setPadding(0, 0, 0, 20);
		emailText.setTextSize(15);
		emailText.setGravity(Gravity.LEFT);

		TextView passwordText = new TextView(this);
		passwordText.setSingleLine(true);
		passwordText.setText("Password");
		passwordText.setPadding(0, 0, 0, 20);
		passwordText.setTextSize(15);
		passwordText.setGravity(Gravity.LEFT);

		TextView confirmPasswordText = new TextView(this);
		confirmPasswordText.setSingleLine(true);
		confirmPasswordText.setText("Confirm Password");
		confirmPasswordText.setPadding(0, 0, 0, 20);
		confirmPasswordText.setTextSize(15);
		confirmPasswordText.setGravity(Gravity.LEFT);

		final EditText emailEdit = new EditText(this);
		emailEdit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);

		final EditText passwordEdit = new EditText(this);
		passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		passwordEdit.setTransformationMethod(PasswordTransformationMethod
				.getInstance());

		final EditText confirmPasswordEdit = new EditText(this);
		confirmPasswordEdit
				.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		confirmPasswordEdit
				.setTransformationMethod(PasswordTransformationMethod
						.getInstance());

		Button registerAndLoginButton = new Button(this);
		registerAndLoginButton.setText("Register and login");
		registerAndLoginButton.setGravity(Gravity.CENTER_HORIZONTAL);

		// layoutParams.setMargins(100, 500, 100, 200);

		currentLineraLayout.addView(titleText, layoutParams);
		currentLineraLayout.addView(emailText);
		currentLineraLayout.addView(emailEdit);
		currentLineraLayout.addView(passwordText);
		currentLineraLayout.addView(passwordEdit);
		currentLineraLayout.addView(confirmPasswordText);
		currentLineraLayout.addView(confirmPasswordEdit);
		currentLineraLayout.addView(registerAndLoginButton, layoutParams);

		registerAndLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (gI.isOnline()) {
					if (completeCredentialsPassed())
						registrationComplete();
				} else
					Toast.makeText(
							getApplicationContext(),
							"Make sure you have internet access when trying to login or register",
							Toast.LENGTH_LONG).show();

			}

			private boolean registrationComplete() {
				new RegisterAsync(RegisterPage.this, emailEdit.getText()
						.toString(), passwordEdit.getText().toString(),
						getApplicationContext()).execute();
				return true;
			}

			private boolean completeCredentialsPassed() {
				if (!emailEdit.getText().toString().equals(""))
					if (!passwordEdit.getText().toString().equals(""))
						if (!confirmPasswordEdit.getText().toString()
								.equals("")) {
							if (passwordEdit
									.getText()
									.toString()
									.equals(confirmPasswordEdit.getText()
											.toString()))
								return true;
							else {
								Toast.makeText(getApplicationContext(),
										"Password error", Toast.LENGTH_LONG)
										.show();
								return false;
							}
						}
				Toast.makeText(getApplicationContext(),
						"Please fill in all fields accordingly",
						Toast.LENGTH_LONG).show();
				return false;
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
				Intent aboutIntent = new Intent(RegisterPage.this,
						AboutPage.class);
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
