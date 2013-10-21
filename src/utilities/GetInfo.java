package utilities;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import components.DatabaseHandler.AdministrativeDatabase;

import config.Variables.Constants;

public class GetInfo {
	Context mContext;
	AdministrativeDatabase adminDb;

	/**
	 * Gets information specific to the project
	 * 
	 * @param ctx
	 */

	public GetInfo(Context ctx) {
		this.mContext = ctx;
		adminDb = new AdministrativeDatabase(Constants.databaseName,
				Constants.adminTable, mContext);

	}

	/**
	 * 
	 * @return true if the device is connected to the internet
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) this.mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}

		return false;
	}

	public static String convertTypeJavaToSql(String s) {
		String converted = "";

		if (s.equalsIgnoreCase("double"))
			converted = "double precision";
		if (s.equalsIgnoreCase("float"))
			converted = "real";
		if (s.equalsIgnoreCase("long"))
			converted = "bigint";
		if (s.equalsIgnoreCase("int"))
			converted = "integer";
		if (s.equalsIgnoreCase("boolean"))
			converted = "boolean";
		if (s.equalsIgnoreCase("string"))
			converted = "character varying(30)";
		if (s.equalsIgnoreCase("java.lang.string"))
			converted = "character varying(30)";

		// Log.d("Converted from", s + " to " + converted);

		return converted;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String generateSqlStatement(String nameOfTable,
			LinkedList<LinkedHashMap<String, String>> listOfMaps) {
		nameOfTable = nameOfTable.replace(" ", "_");
		String statement = "CREATE TABLE if not exists "
				+ nameOfTable
				+ " (id integer primary key autoincrement, upload boolean default FALSE, ";
		for (LinkedHashMap<String, String> map : listOfMaps) {
			Iterator iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>) iterator
						.next();
				statement = statement + pairs.getKey() + " " + pairs.getValue()
						+ " , ";
				iterator.remove(); // avoids a ConcurrentModificationException
			}
		}

		statement = statement.substring(0, statement.length() - 3);

		statement = statement + ")";

		return statement;
	}

	public boolean isServiceOn() {
		return adminDb.getStatus();
	}

	public void setServiceOn() {
		Log.d("SET SERVICE ON", "set service on");
		adminDb.setStatus(true);
	}

	public void setServiceOff() {
		Log.d("SET SERVICE OFF", "set service off");

		adminDb.setStatus(false);
	}

	public boolean attemptConnect(String string) {
		return false;
	}
}
