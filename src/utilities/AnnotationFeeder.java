package utilities;

import android.content.Context;
import android.widget.Toast;

import components.DatabaseHandler.AdministrativeDatabase;
import components.DatabaseHandler.AnnotationDatabase;
import components.DatabaseHandler.LocationAccelerationDatabase;
import components.Location.AnnotationsValues;

import config.Variables.Constants;
import config.Variables.Variables;

public class AnnotationFeeder {

	private static AnnotationFeeder instance = null;
	private Context mContext;
	AnnotationDatabase annotationDatabase;
	LocationAccelerationDatabase locationDatabase;
	private int userID;
	private String lastAnnotationName = "";
	private String currentAnnotationName = "";
	private long lastAnnotationTime;
	private long currentAnnotationTime;
	AdministrativeDatabase adminDb;

	private AnnotationFeeder(Context ctx) {

		this.mContext = ctx;
		annotationDatabase = new AnnotationDatabase(Constants.databaseName,
				Constants.annotationTable, mContext);
		adminDb = new AdministrativeDatabase(Constants.databaseName,
				Constants.adminTable, mContext);
		this.userID = adminDb.getUserId();
		locationDatabase = new LocationAccelerationDatabase(
				Constants.databaseName, Constants.locationTable, mContext);

	}

	public static AnnotationFeeder getInstance(Context ctx) {
		if (instance == null)
			instance = new AnnotationFeeder(ctx);
		return instance;
	}

	public void feedMe(String annotationName) {
		if (this.userID != 0) {
			AnnotationsValues previousAnnotation = annotationDatabase
					.getLastInsertedAnnotation();

			if (Variables.periodAnnotations) {
				// PERIOD BASED ANNOTATIONS
				if (previousAnnotation != null) {
					annotationDatabase
							.insertAnnotationIntoDatabase(new AnnotationsValues(
									userID, previousAnnotation
											.getAnnotationStopTime(), System
											.currentTimeMillis(),
									lastAnnotationName));
					lastAnnotationName = annotationName;
				} else {
					if (lastAnnotationName.equals("")) {
						lastAnnotationName = annotationName;
						lastAnnotationTime = System.currentTimeMillis();
					} else if (currentAnnotationName.equals("")) {
						currentAnnotationName = annotationName;
						currentAnnotationTime = System.currentTimeMillis();
						annotationDatabase
								.insertAnnotationIntoDatabase(new AnnotationsValues(
										userID, lastAnnotationTime,
										currentAnnotationTime,
										lastAnnotationName));
						lastAnnotationName = currentAnnotationName;
					}
				}
			}

			// POINT BASED ANNOTATIONS
			else {
				if (previousAnnotation != null) {
					currentAnnotationTime = locationDatabase.getLastLocation().time_;

					if (currentAnnotationTime != lastAnnotationTime) {
						annotationDatabase
								.insertAnnotationIntoDatabase(new AnnotationsValues(
										userID, previousAnnotation
												.getAnnotationStopTime(),
										System.currentTimeMillis(),
										lastAnnotationName));
						lastAnnotationName = annotationName;
						lastAnnotationTime = currentAnnotationTime;
					}
				} else {
					if (lastAnnotationName.equals("")) {
						lastAnnotationName = annotationName;
						lastAnnotationTime = locationDatabase.getLastLocation().time_;
					} else if (currentAnnotationName.equals("")) {
						currentAnnotationTime = locationDatabase
								.getLastLocation().time_;
						if (currentAnnotationTime != lastAnnotationTime) {
							currentAnnotationName = annotationName;
							annotationDatabase
									.insertAnnotationIntoDatabase(new AnnotationsValues(
											userID, lastAnnotationTime,
											currentAnnotationTime,
											lastAnnotationName));
							lastAnnotationName = currentAnnotationName;
							lastAnnotationTime = currentAnnotationTime;
						}
					}
				}
			}
		} else {
			this.userID = adminDb.getUserId();
			Toast.makeText(mContext,
					"Please login first new userid = " + userID,
					Toast.LENGTH_LONG).show();
		}
	}

}
