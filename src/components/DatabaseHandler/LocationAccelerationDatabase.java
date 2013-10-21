package components.DatabaseHandler;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import components.Location.AccelerometerValues;
import components.Location.EmbeddedLocation;
import components.Location.LocationValues;

public class LocationAccelerationDatabase {
	String name;
	static SQLiteDatabase myDatabase;
	Context mContext;
	static String locationTableName;
	LinkedList<LinkedHashMap<String, String>> listOfMaps;

	public LocationAccelerationDatabase(String databaseName,
			String locationTableName_, Context ctx) {
		this.name = databaseName;
		locationTableName = locationTableName_;
		this.mContext = ctx;
		myDatabase = mContext.openOrCreateDatabase(name, Context.MODE_PRIVATE,
				null);
		listOfMaps = new LinkedList<LinkedHashMap<String, String>>();
		listOfMaps.add(LocationValues.getAllElements());
		listOfMaps.add(AccelerometerValues.getAllElements());
		myDatabase.execSQL(utilities.GetInfo.generateSqlStatement(
				locationTableName, listOfMaps));
	}

	public boolean insertLocationIntoDb(EmbeddedLocation eL) {
		ContentValues newValues = new ContentValues();

		for (Field f : LocationValues.class.getDeclaredFields()) {
			f.setAccessible(true);

			String name = f.getName();
			String value;
			try {
				value = f.get(eL.getCurrentLocation()).toString();
				newValues.put(name, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			}

		}

		for (Field f : AccelerometerValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			String name = f.getName();
			String value;
			try {
				value = f.get(eL.getCurrentAcc()).toString();
				newValues.put(name, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			}
		}

		myDatabase.insert(locationTableName, null, newValues);
		newValues.clear();

		return false;
	}

	public static LinkedList<EmbeddedLocation> getAllLocationsFromDatabase() {

		int numberOfElements = AccelerometerValues.class.getDeclaredFields().length
				+ LocationValues.class.getDeclaredFields().length;
		String[] arrayOfFields = new String[numberOfElements];
		int i = 0;
		for (Field f : LocationValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			arrayOfFields[i] = f.getName();
			i++;
		}

		for (Field f : AccelerometerValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			arrayOfFields[i] = f.getName();
			i++;
		}

		Cursor cursor = myDatabase.query(locationTableName, arrayOfFields,
				null, null, null, null, null);
		LinkedList<EmbeddedLocation> eL = new LinkedList<EmbeddedLocation>();

		while (cursor.moveToNext()) {

			/*
			 * LocationValues specific part
			 */
			long time_ = cursor.getLong(cursor.getColumnIndex("time_"));
			int user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
			double lat_ = cursor.getDouble(cursor.getColumnIndex("lat_"));
			double lon_ = cursor.getDouble(cursor.getColumnIndex("lon_"));
			double speed_ = cursor.getDouble(cursor.getColumnIndex("speed_"));
			double altitude_ = cursor.getDouble(cursor
					.getColumnIndex("altitude_"));
			double bearing_ = cursor.getDouble(cursor
					.getColumnIndex("bearing_"));
			double accuracy_ = cursor.getDouble(cursor
					.getColumnIndex("accuracy_"));
			int satellites_ = cursor.getInt(cursor
					.getColumnIndex("satellites_"));
			LocationValues lV = new LocationValues(user_id, lat_, lon_, speed_,
					altitude_, bearing_, accuracy_, satellites_, time_);

			/*
			 * Accelerometer Specific Parts
			 */
			float xMean, yMean, zMean, totalMean;
			float xStdDev, yStdDev, zStdDev, totalStdDev;
			float xMin, xMax, yMin, yMax, zMin, zMax, totalMin, totalMax;
			int xNumberOfPeaks, yNumberOfPeaks, zNumberOfPeaks, totalNumberOfPeaks;
			int totalNumberOfSteps;
			boolean xIsMoving = false, yIsMoving = false, zIsMoving = false, totalIsMoving = false;
			int size;

			xMean = cursor.getFloat(cursor.getColumnIndex("xMean"));
			yMean = cursor.getFloat(cursor.getColumnIndex("yMean"));
			zMean = cursor.getFloat(cursor.getColumnIndex("zMean"));
			totalMean = cursor.getFloat(cursor.getColumnIndex("totalMean"));

			xStdDev = cursor.getFloat(cursor.getColumnIndex("xStdDev"));
			yStdDev = cursor.getFloat(cursor.getColumnIndex("yStdDev"));
			zStdDev = cursor.getFloat(cursor.getColumnIndex("zStdDev"));
			totalStdDev = cursor.getFloat(cursor.getColumnIndex("totalStdDev"));

			xMin = cursor.getFloat(cursor.getColumnIndex("xMinimum"));
			yMin = cursor.getFloat(cursor.getColumnIndex("yMin"));
			zMin = cursor.getFloat(cursor.getColumnIndex("zMin"));
			totalMin = cursor.getFloat(cursor.getColumnIndex("totalMin"));

			xMax = cursor.getFloat(cursor.getColumnIndex("xMaximum"));
			yMax = cursor.getFloat(cursor.getColumnIndex("yMax"));
			zMax = cursor.getFloat(cursor.getColumnIndex("zMax"));
			totalMax = cursor.getFloat(cursor.getColumnIndex("totalMax"));

			xNumberOfPeaks = cursor.getInt(cursor
					.getColumnIndex("xNumberOfPeaks"));
			yNumberOfPeaks = cursor.getInt(cursor
					.getColumnIndex("yNumberOfPeaks"));
			zNumberOfPeaks = cursor.getInt(cursor
					.getColumnIndex("zNumberOfPeaks"));
			totalNumberOfPeaks = cursor.getInt(cursor
					.getColumnIndex("totalNumberOfPeaks"));

			totalNumberOfSteps = cursor.getInt(cursor
					.getColumnIndex("totalNumberOfSteps"));

			xIsMoving = cursor.getString(cursor.getColumnIndex("xIsMoving"))
					.equalsIgnoreCase("true");
			yIsMoving = cursor.getString(cursor.getColumnIndex("yIsMoving"))
					.equalsIgnoreCase("true");
			zIsMoving = cursor.getString(cursor.getColumnIndex("zIsMoving"))
					.equalsIgnoreCase("true");
			totalIsMoving = cursor.getString(
					cursor.getColumnIndex("totalIsMoving")).equalsIgnoreCase(
					"true");

			size = cursor.getInt(cursor.getColumnIndex("size"));

			AccelerometerValues aV = new AccelerometerValues(xMean, yMean,
					zMean, totalMean, xStdDev, yStdDev, zStdDev, totalStdDev,
					xMin, xMax, yMin, yMax, zMin, zMax, totalMin, totalMax,
					xNumberOfPeaks, yNumberOfPeaks, zNumberOfPeaks,
					totalNumberOfPeaks, totalNumberOfSteps, xIsMoving,
					yIsMoving, zIsMoving, totalIsMoving, size);

			EmbeddedLocation embeddedLocation = new EmbeddedLocation(lV, aV);
			eL.add(embeddedLocation);
		}
		return eL;
	}

	public static LinkedList<EmbeddedLocation> getLocationsForUploadFromDatabase() {

		int numberOfElements = AccelerometerValues.class.getDeclaredFields().length
				+ LocationValues.class.getDeclaredFields().length;
		String[] arrayOfFields = new String[numberOfElements];
		int i = 0;
		for (Field f : LocationValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			arrayOfFields[i] = f.getName();
			i++;
		}

		for (Field f : AccelerometerValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			arrayOfFields[i] = f.getName();
			i++;
		}

		Cursor cursor = myDatabase.query(locationTableName, arrayOfFields,
				"upload=?", new String[] { "FALSE" }, null, null, null);
		LinkedList<EmbeddedLocation> eL = new LinkedList<EmbeddedLocation>();

		while (cursor.moveToNext()) {

			/*
			 * LocationValues specific part
			 */
			long time_ = cursor.getLong(cursor.getColumnIndex("time_"));
			int user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
			double lat_ = cursor.getDouble(cursor.getColumnIndex("lat_"));
			double lon_ = cursor.getDouble(cursor.getColumnIndex("lon_"));
			double speed_ = cursor.getDouble(cursor.getColumnIndex("speed_"));
			double altitude_ = cursor.getDouble(cursor
					.getColumnIndex("altitude_"));
			double bearing_ = cursor.getDouble(cursor
					.getColumnIndex("bearing_"));
			double accuracy_ = cursor.getDouble(cursor
					.getColumnIndex("accuracy_"));
			int satellites_ = cursor.getInt(cursor
					.getColumnIndex("satellites_"));
			LocationValues lV = new LocationValues(user_id, lat_, lon_, speed_,
					altitude_, bearing_, accuracy_, satellites_, time_);

			/*
			 * Accelerometer Specific Parts
			 */
			float xMean, yMean, zMean, totalMean;
			float xStdDev, yStdDev, zStdDev, totalStdDev;
			float xMin, xMax, yMin, yMax, zMin, zMax, totalMin, totalMax;
			int xNumberOfPeaks, yNumberOfPeaks, zNumberOfPeaks, totalNumberOfPeaks;
			int totalNumberOfSteps;
			boolean xIsMoving = false, yIsMoving = false, zIsMoving = false, totalIsMoving = false;
			int size;

			xMean = cursor.getFloat(cursor.getColumnIndex("xMean"));
			yMean = cursor.getFloat(cursor.getColumnIndex("yMean"));
			zMean = cursor.getFloat(cursor.getColumnIndex("zMean"));
			totalMean = cursor.getFloat(cursor.getColumnIndex("totalMean"));

			xStdDev = cursor.getFloat(cursor.getColumnIndex("xStdDev"));
			yStdDev = cursor.getFloat(cursor.getColumnIndex("yStdDev"));
			zStdDev = cursor.getFloat(cursor.getColumnIndex("zStdDev"));
			totalStdDev = cursor.getFloat(cursor.getColumnIndex("totalStdDev"));

			xMin = cursor.getFloat(cursor.getColumnIndex("xMinimum"));
			yMin = cursor.getFloat(cursor.getColumnIndex("yMin"));
			zMin = cursor.getFloat(cursor.getColumnIndex("zMin"));
			totalMin = cursor.getFloat(cursor.getColumnIndex("totalMin"));

			xMax = cursor.getFloat(cursor.getColumnIndex("xMaximum"));
			yMax = cursor.getFloat(cursor.getColumnIndex("yMax"));
			zMax = cursor.getFloat(cursor.getColumnIndex("zMax"));
			totalMax = cursor.getFloat(cursor.getColumnIndex("totalMax"));

			xNumberOfPeaks = cursor.getInt(cursor
					.getColumnIndex("xNumberOfPeaks"));
			yNumberOfPeaks = cursor.getInt(cursor
					.getColumnIndex("yNumberOfPeaks"));
			zNumberOfPeaks = cursor.getInt(cursor
					.getColumnIndex("zNumberOfPeaks"));
			totalNumberOfPeaks = cursor.getInt(cursor
					.getColumnIndex("totalNumberOfPeaks"));

			totalNumberOfSteps = cursor.getInt(cursor
					.getColumnIndex("totalNumberOfSteps"));

			xIsMoving = cursor.getString(cursor.getColumnIndex("xIsMoving"))
					.equalsIgnoreCase("true");
			yIsMoving = cursor.getString(cursor.getColumnIndex("yIsMoving"))
					.equalsIgnoreCase("true");
			zIsMoving = cursor.getString(cursor.getColumnIndex("zIsMoving"))
					.equalsIgnoreCase("true");
			totalIsMoving = cursor.getString(
					cursor.getColumnIndex("totalIsMoving")).equalsIgnoreCase(
					"true");

			size = cursor.getInt(cursor.getColumnIndex("size"));

			AccelerometerValues aV = new AccelerometerValues(xMean, yMean,
					zMean, totalMean, xStdDev, yStdDev, zStdDev, totalStdDev,
					xMin, xMax, yMin, yMax, zMin, zMax, totalMin, totalMax,
					xNumberOfPeaks, yNumberOfPeaks, zNumberOfPeaks,
					totalNumberOfPeaks, totalNumberOfSteps, xIsMoving,
					yIsMoving, zIsMoving, totalIsMoving, size);

			EmbeddedLocation embeddedLocation = new EmbeddedLocation(lV, aV);
			eL.add(embeddedLocation);
		}
		return eL;
	}

	public String getJSONFromLocationsForUploadFromDatabase() {
		Gson json = new Gson();
		return json.toJson(getLocationsForUploadFromDatabase());
	}

	public void setUploadToTrue() {
		ContentValues values = new ContentValues();
		values.put("upload", "TRUE");
		myDatabase.update(locationTableName, values, null, null);
	}

	public LocationValues getLastLocation() {
		int numberOfElements = AccelerometerValues.class.getDeclaredFields().length
				+ LocationValues.class.getDeclaredFields().length;
		String[] arrayOfFields = new String[numberOfElements];
		int i = 0;
		for (Field f : LocationValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			arrayOfFields[i] = f.getName();
			i++;
		}

		Cursor cursor = myDatabase.query(locationTableName, arrayOfFields,
				null, null, null, null, "id", "1");
		LocationValues lastLocation = null;

		while (cursor.moveToNext()) {

			/*
			 * LocationValues specific part
			 */
			long time_ = cursor.getLong(cursor.getColumnIndex("time_"));
			int user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
			double lat_ = cursor.getDouble(cursor.getColumnIndex("lat_"));
			double lon_ = cursor.getDouble(cursor.getColumnIndex("lon_"));
			double speed_ = cursor.getDouble(cursor.getColumnIndex("speed_"));
			double altitude_ = cursor.getDouble(cursor
					.getColumnIndex("altitude_"));
			double bearing_ = cursor.getDouble(cursor
					.getColumnIndex("bearing_"));
			double accuracy_ = cursor.getDouble(cursor
					.getColumnIndex("accuracy_"));
			int satellites_ = cursor.getInt(cursor
					.getColumnIndex("satellites_"));
			lastLocation = new LocationValues(user_id, lat_, lon_, speed_,
					altitude_, bearing_, accuracy_, satellites_, time_);

		}
		return lastLocation;
	}
}
