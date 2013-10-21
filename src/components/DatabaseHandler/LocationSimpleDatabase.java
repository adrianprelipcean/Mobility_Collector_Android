package components.DatabaseHandler;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import components.Location.LocationValues;

public class LocationSimpleDatabase {
	String name;
	static SQLiteDatabase myDatabase;
	Context mContext;
	static String locationTableName;
	LinkedList<LinkedHashMap<String, String>> listOfMaps;

	public LocationSimpleDatabase(String databaseName,
			String locationTableName_, Context ctx) {
		this.name = databaseName;
		locationTableName = locationTableName_;
		this.mContext = ctx;
		myDatabase = mContext.openOrCreateDatabase(name, Context.MODE_PRIVATE,
				null);
		listOfMaps = new LinkedList<LinkedHashMap<String, String>>();
		listOfMaps.add(LocationValues.getAllElements());
		myDatabase.execSQL(utilities.GetInfo.generateSqlStatement(
				locationTableName, listOfMaps));

	}

	public boolean insertLocationIntoDb(LocationValues eL) {
		ContentValues newValues = new ContentValues();

		for (Field f : LocationValues.class.getDeclaredFields()) {
			f.setAccessible(true);

			String name = f.getName();
			String value;
			try {
				value = f.get(eL).toString();
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

	public static LinkedList<LocationValues> getAllLocationsFromDatabase() {

		int numberOfElements = LocationValues.class.getDeclaredFields().length;
		String[] arrayOfFields = new String[numberOfElements];
		int i = 0;
		for (Field f : LocationValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			arrayOfFields[i] = f.getName();
			i++;
		}

		Cursor cursor = myDatabase.query(locationTableName, arrayOfFields,
				null, null, null, null, null);

		LinkedList<LocationValues> locationList = new LinkedList<LocationValues>();

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

			locationList.add(lV);
		}
		return locationList;
	}

	public static LinkedList<LocationValues> getLocationsForUploadFromDatabase() {

		int numberOfElements = LocationValues.class.getDeclaredFields().length;
		String[] arrayOfFields = new String[numberOfElements];
		int i = 0;
		for (Field f : LocationValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			arrayOfFields[i] = f.getName();
			i++;
		}

		Cursor cursor = myDatabase.query(locationTableName, arrayOfFields,
				"upload=?", new String[] { "FALSE" }, null, null, null);
		LinkedList<LocationValues> locationList = new LinkedList<LocationValues>();

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

			locationList.add(lV);
		}
		return locationList;
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

}
