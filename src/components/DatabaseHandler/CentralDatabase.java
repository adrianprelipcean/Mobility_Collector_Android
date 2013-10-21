package components.DatabaseHandler;

import android.content.Context;
import android.util.Log;
import config.Variables.Constants;
import config.Variables.Variables;

public class CentralDatabase {
	Context mContext;

	public CentralDatabase(Context ctx) {
		mContext = ctx;
	}

	public String getUploadStatement() {

		String uploadString = "method=upload";

		try {
			if (Variables.isAccelerometerEmbedded) {
				LocationAccelerationDatabase locationDb = new LocationAccelerationDatabase(
						Constants.databaseName, Constants.locationTable,
						mContext);
				String embeddedLocation = locationDb
						.getJSONFromLocationsForUploadFromDatabase();
				uploadString = uploadString + "&embeddedLocations_="
						+ embeddedLocation + "&simpleLocations_=";
			} else {
				LocationSimpleDatabase locationDb = new LocationSimpleDatabase(
						Constants.databaseName, Constants.locationTable,
						mContext);
				String locationsSimple = locationDb
						.getJSONFromLocationsForUploadFromDatabase();
				uploadString = uploadString + "&embeddedLocations_="
						+ "&simpleLocations_=" + locationsSimple;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.d("error1", e1.toString());
		}

		try {
			if (Variables.areAnnotationsAllowed) {
				AnnotationDatabase annotationDb = new AnnotationDatabase(
						Constants.databaseName, Constants.annotationTable,
						mContext);
				String annotations = annotationDb.getJSONFromAllForUpload();
				uploadString = uploadString + "&annotations_=" + annotations;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("error", e.toString());
		}

	//	Log.d("upload string ", uploadString);

		return uploadString;
	}

	public void setUploadToTrue() {

		if (Variables.isAccelerometerEmbedded) {
			LocationAccelerationDatabase locationDb = new LocationAccelerationDatabase(
					Constants.databaseName, Constants.locationTable, mContext);
			locationDb.setUploadToTrue();
		} else {
			LocationSimpleDatabase locationDb = new LocationSimpleDatabase(
					Constants.databaseName, Constants.locationTable, mContext);
			locationDb.setUploadToTrue();
		}

		if (Variables.areAnnotationsAllowed) {
			AnnotationDatabase annotationDb = new AnnotationDatabase(
					Constants.databaseName, Constants.annotationTable, mContext);
			annotationDb.setUploadToTrue();
		}
	}

}
