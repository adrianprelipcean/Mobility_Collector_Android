package design.Classes;

import utilities.AnnotationFeeder;
import utilities.GetInfo;
import utilities.UploadAsync;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import components.DatabaseHandler.AdministrativeDatabase;
import components.DatabaseHandler.AnnotationDatabase;
import components.DatabaseHandler.LocationAccelerationDatabase;
import components.Location.AnnotationsValues;
import components.Service.CollectingService;

import config.Variables.Constants;
import config.Variables.Variables;

public class ServiceHandling extends Activity {
	boolean isServiceOn = false;
	Button serviceHandlingButton;
	String[] annotations;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		GetInfo gI = new GetInfo(this);
		isServiceOn = gI.isServiceOn();

		AdministrativeDatabase adminDb = new AdministrativeDatabase(
				Constants.databaseName, Constants.adminTable,
				getApplicationContext());

		Log.d("TAG FOR USERID", adminDb.getUserId() + " ");

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

		titleText.setText("Mobility Collector Control Center");
		titleText.setPadding(0, 0, 0, 50);
		titleText.setTextSize(20);
		titleText.setGravity(Gravity.CENTER_HORIZONTAL);

		serviceHandlingButton = new Button(this);
		serviceHandlingButton.setGravity(Gravity.CENTER_HORIZONTAL);
		changeText(isServiceOn);

		currentLineraLayout.addView(titleText, layoutParams);
		currentLineraLayout.addView(serviceHandlingButton, layoutParams);

		if (Variables.areAnnotationsAllowed) {
			
			AnnotationDatabase annotationDatabase = new AnnotationDatabase(Constants.databaseName, Constants.annotationTable, this);
			
			AnnotationsValues previousAnnotation = annotationDatabase
					.getLastInsertedAnnotation();

			TextView annotationText = new TextView(this);
			annotationText.setSingleLine(true);

			annotationText.setText("Annotating menu");
			annotationText.setPadding(0, 50, 0, 25);
			annotationText.setTextSize(18);
			annotationText.setGravity(Gravity.LEFT);

			RadioGroup annotationRadioGroup = new RadioGroup(this);
			annotationRadioGroup.setOrientation(RadioGroup.VERTICAL);

			annotations = new String[Variables.annotationsStrings
					.split("!__!").length];
			annotations = Variables.annotationsStrings.split("!__!");

			final RadioButton[] annotationItem = new RadioButton[annotations.length];

			for (int i = 0; i < annotations.length; i++) {
				annotationItem[i] = new RadioButton(this);
				annotationRadioGroup.addView(annotationItem[i]);
				annotationItem[i].setText(annotations[i]);
			}
			
			if (previousAnnotation != null) {
				for (int i=0;i < annotations.length; i++) 
					if (annotations[i].equalsIgnoreCase(previousAnnotation.getAnnotationValues()))
				{
						annotationItem[i].setChecked(true);
				
				}
			}


			annotationRadioGroup
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							// TODO Auto-generated method stub

							try {
								AnnotationFeeder.getInstance(
										getApplicationContext()).feedMe(
										annotationItem[(checkedId - 1)%annotations.length].getText()
												.toString());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}
					});

			currentLineraLayout.addView(annotationText);

			currentLineraLayout.addView(annotationRadioGroup);
		}

		serviceHandlingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent collectionServiceIntent = new Intent(
						CollectingService.class.getName());

				if (!isServiceOn) {
					isServiceOn = true;
					changeText(isServiceOn);
					try {
						startService(collectionServiceIntent);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.d("error tag", e.toString());
					}
				} else {
					isServiceOn = false;
					stopService(collectionServiceIntent);
					changeText(isServiceOn);
				}
			}
		});
		this.setContentView(currentScrollView);
	}

	private void changeText(boolean isServiceOn2) {
		if (isServiceOn2) {
			serviceHandlingButton.setText("Stop collection");
			serviceHandlingButton.setTextColor(Color.RED);
		} else {
			serviceHandlingButton.setText("Start collection");
			serviceHandlingButton.setTextColor(Color.BLUE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem aboutItem = menu.add("About");

		aboutItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent aboutIntent = new Intent(ServiceHandling.this,
						AboutPage.class);
				try {
					startActivity(aboutIntent);
				} catch (Exception e) {
					Log.d("EXCEPTION", e.toString());
				}
				return false;
			}
		});

		MenuItem adminItem = menu.add("Admin");

		adminItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent adminIntent = new Intent(ServiceHandling.this,
						AdminLogin.class);
				try {
					startActivity(adminIntent);
				} catch (Exception e) {
					Log.d("EXCEPTION", e.toString());
				}
				return false;
			}
		});

		MenuItem dbStatus = menu.add("DB Status");

		dbStatus.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				LocationAccelerationDatabase locationDb = new LocationAccelerationDatabase(
						Constants.databaseName, Constants.locationTable,
						getApplicationContext());
				Toast.makeText(
						getApplicationContext(),
						"DB STATUS: "
								+ locationDb.getAllLocationsFromDatabase()
										.size(), Toast.LENGTH_LONG).show();
				return false;
			}
		});

		MenuItem manualDb = menu.add("Manual Upload");

		manualDb.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				new UploadAsync(getApplicationContext()).execute();
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
}
