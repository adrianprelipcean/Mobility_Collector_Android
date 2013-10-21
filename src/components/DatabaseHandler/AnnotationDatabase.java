package components.DatabaseHandler;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import components.Location.AnnotationsValues;

public class AnnotationDatabase {
	String name;
	String annotationTableName;
	static SQLiteDatabase myDatabase;
	Context mContext;

	public AnnotationDatabase(String databaseName, String annotationTableName,
			Context ctx) {
		this.mContext = ctx;
		this.name = databaseName;
		this.annotationTableName = annotationTableName;
		myDatabase = mContext.openOrCreateDatabase(name, Context.MODE_PRIVATE,
				null);
		LinkedList<LinkedHashMap<String, String>> listOfMaps = new LinkedList<LinkedHashMap<String, String>>();
		listOfMaps.add(AnnotationsValues.getAllElements());
		myDatabase.execSQL(utilities.GetInfo.generateSqlStatement(
				annotationTableName, listOfMaps));
	}

	public boolean insertAnnotationIntoDatabase(AnnotationsValues aV) {
		ContentValues newValues = new ContentValues();

		for (Field f : AnnotationsValues.class.getDeclaredFields()) {
			f.setAccessible(true);

			String name = f.getName();
			String value;
			try {
				value = f.get(aV).toString();
				newValues.put(name, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			}

		}

		myDatabase.insert(annotationTableName, null, newValues);
		newValues.clear();

		return false;
	}

	public LinkedList<AnnotationsValues> getAllAnnotationsFromDatabase() {

		int numberOfElements = AnnotationsValues.class.getDeclaredFields().length;
		String[] arrayOfFields = new String[numberOfElements];
		int i = 0;
		for (Field f : AnnotationsValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			arrayOfFields[i] = f.getName();
			i++;
		}

		Cursor cursor = myDatabase.query(annotationTableName, arrayOfFields,
				null, null, null, null, null);

		LinkedList<AnnotationsValues> annotationList = new LinkedList<AnnotationsValues>();

		while (cursor.moveToNext()) {

			/*
			 * LocationValues specific part
			 */
			int userid = cursor.getInt(cursor.getColumnIndex("userid"));
			long annotationStartTime = cursor.getLong(cursor
					.getColumnIndex("annotationStartTime"));
			long annotationStopTime = cursor.getLong(cursor
					.getColumnIndex("annotationStopTime"));
			String annotationValues = cursor.getString(cursor
					.getColumnIndex("annotationValues"));
			AnnotationsValues aVal = new AnnotationsValues(userid,
					annotationStartTime, annotationStopTime, annotationValues);

			annotationList.add(aVal);
		}
		return annotationList;
	}

	public LinkedList<AnnotationsValues> getAllAnnotationsForUpload() {

		int numberOfElements = AnnotationsValues.class.getDeclaredFields().length;
		String[] arrayOfFields = new String[numberOfElements];
		int i = 0;
		for (Field f : AnnotationsValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			arrayOfFields[i] = f.getName();
			i++;
		}

		Cursor cursor = myDatabase.query(annotationTableName, arrayOfFields,
				"upload=?", new String[] { "FALSE" }, null, null, null);

		LinkedList<AnnotationsValues> annotationList = new LinkedList<AnnotationsValues>();

		while (cursor.moveToNext()) {

			/*
			 * LocationValues specific part
			 */
			int userid = cursor.getInt(cursor.getColumnIndex("userid"));
			long annotationStartTime = cursor.getLong(cursor
					.getColumnIndex("annotationStartTime"));
			long annotationStopTime = cursor.getLong(cursor
					.getColumnIndex("annotationStopTime"));
			String annotationValues = cursor.getString(cursor
					.getColumnIndex("annotationValues"));
			AnnotationsValues aVal = new AnnotationsValues(userid,
					annotationStartTime, annotationStopTime, annotationValues);

			annotationList.add(aVal);
		}
		return annotationList;
	}

	public String getJSONFromAllForUpload() {
		Gson json = new Gson();
		return json.toJson(getAllAnnotationsForUpload());
	}

	public void setUploadToTrue() {
		ContentValues values = new ContentValues();
		values.put("upload", "TRUE");
		myDatabase.update(annotationTableName, values, null, null);
	}

	public AnnotationsValues getLastInsertedAnnotation() {

		int numberOfElements = AnnotationsValues.class.getDeclaredFields().length;
		String[] arrayOfFields = new String[numberOfElements];
		int i = 0;
		for (Field f : AnnotationsValues.class.getDeclaredFields()) {
			f.setAccessible(true);
			arrayOfFields[i] = f.getName();
			i++;
		}

		Cursor cursor = myDatabase.query(annotationTableName, arrayOfFields,
				null, null, null, null, "id desc", "1");
		AnnotationsValues lastAnnotation = null;

		while (cursor.moveToNext()) {

			/*
			 * LocationValues specific part
			 */
			int userid = cursor.getInt(cursor.getColumnIndex("userid"));
			long annotationStartTime = cursor.getLong(cursor
					.getColumnIndex("annotationStartTime"));
			long annotationStopTime = cursor.getLong(cursor
					.getColumnIndex("annotationStopTime"));
			String annotationValues = cursor.getString(cursor
					.getColumnIndex("annotationValues"));
			lastAnnotation = new AnnotationsValues(userid, annotationStartTime,
					annotationStopTime, annotationValues);
		}

		return lastAnnotation;
	}

}
