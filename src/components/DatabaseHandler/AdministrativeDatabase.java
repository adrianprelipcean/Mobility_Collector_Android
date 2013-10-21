package components.DatabaseHandler;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import config.Variables.Constants;
import config.Variables.Variables;

public class AdministrativeDatabase {
	String name;
	String tableName;
	Context mContext;
	static SQLiteDatabase myDatabase;

	public AdministrativeDatabase(String dbName, String tableName_, Context ctx) {
		// TODO Auto-generated constructor stub
		name = dbName;
		tableName = tableName_;
		this.mContext = ctx;
		myDatabase = mContext.openOrCreateDatabase(name, Context.MODE_PRIVATE,
				null);

		String statement = "CREATE TABLE if not exists " + tableName + " ("
				+ config.Variables.Constants.serviceColumnName
				+ " boolean default FALSE, "
				+ config.Variables.Constants.userIdColumnName + " integer, "
				+ config.Variables.Constants.urlColumnName + " text " + ")";

		myDatabase.execSQL(statement);
	}

	public boolean getStatus() {
		boolean isOnline = false;

		Cursor cursor1 = myDatabase.query(tableName, null, null, null, null,
				null, null);
		if (cursor1.getCount() > 0)
			while (cursor1.moveToNext()) {
				isOnline = cursor1.getString(
						cursor1.getColumnIndex(Constants.serviceColumnName))
						.equalsIgnoreCase("true");
			}

		cursor1.close();

		return isOnline;
	}

	public void setStatus(boolean status) {

		Cursor cursor1 = myDatabase.query(tableName, null, null, null, null,
				null, null);
		try {
			if (cursor1.getCount() > 0)
				myDatabase.execSQL("Update " + tableName + " Set "
						+ Constants.serviceColumnName + " = '" + status + "'");
			else
				myDatabase.execSQL("Insert Into " + tableName + "("
						+ Constants.serviceColumnName + ","
						+ Constants.userIdColumnName + ","
						+ Constants.urlColumnName + ") Values( '" + status
						+ "'," + getUserId() + ",'" + getURL() + "')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}

	}

	public int getUserId() {
		int userID = 0;
		Cursor cursor1 = myDatabase.query(tableName, null, null, null, null,
				null, null);
		if (cursor1.getCount() > 0)
			while (cursor1.moveToNext()) {
				userID = cursor1.getInt(cursor1
						.getColumnIndex(Constants.userIdColumnName));
			}
		else
		cursor1.close();
		return userID;
	}

	public void updateUserId(int userId) {
		Cursor cursor1 = myDatabase.query(tableName, null, null, null, null,
				null, null);
		try {
			if (cursor1.getCount() > 0)
				myDatabase.execSQL("Update " + tableName + " Set "
						+ Constants.userIdColumnName + " = " + userId);
			else
				myDatabase.execSQL("Insert Into " + tableName + "("
						+ Constants.serviceColumnName + ","
						+ Constants.userIdColumnName + ","
						+ Constants.urlColumnName + ") Values('false',"
						+ userId + ",'" + getURL() + "')");
		} catch (SQLException e) {
			Log.e("ERROR IS ", e.toString());
		}
	}

	public String getURL() {
		String url = Variables.urlConnection;
		Cursor cursor1 = myDatabase.query(tableName, null, null, null, null,
				null, null);
		if (cursor1.getCount() > 0)
			while (cursor1.moveToNext()) {
				url = cursor1.getString(cursor1
						.getColumnIndex(Constants.urlColumnName));
			}
		cursor1.close();
		return url;
	}

	public void setURL(String url) {
		Cursor cursor1 = myDatabase.query(tableName, null, null, null, null,
				null, null);
		try {
			if (cursor1.getCount() > 0)
				myDatabase.execSQL("Update " + tableName + " Set "
						+ Constants.urlColumnName + " = " + url);
			else
				myDatabase.execSQL("Insert Into " + tableName + "("
						+ Constants.serviceColumnName + ","
						+ Constants.userIdColumnName + ","
						+ Constants.urlColumnName + ") Values('false',"
						+ getUserId() + "," + url + ")");
		} catch (SQLException e) {
		}
	}
}
