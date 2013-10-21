package utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import components.DatabaseHandler.CentralDatabase;

import config.Variables.Constants;
import config.Variables.Variables;

public class UploadAsync extends AsyncTask<Void, Void, Boolean> {

	URL url = null;
	Context mContext;
	CentralDatabase centralDB;

	public UploadAsync(Context ctx) {
		mContext = ctx;
		centralDB = new CentralDatabase(mContext);
	}

	protected Boolean doInBackground(Void... params) {
		URL url = null;
		String upload = centralDB.getUploadStatement();

		if (!upload
				.equalsIgnoreCase("method=upload&embeddedLocations_=[]&simpleLocations_=&annotations_=[]"))

			try {
				url = new URL((Variables.urlConnection + Constants.servletName));
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();

				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				urlConn.setRequestMethod("POST");
				urlConn.setUseCaches(false);
				urlConn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				urlConn.setRequestProperty("Charest", "utf-8");
				// to connect to the server side
				urlConn.connect();

				DataOutputStream dop = new DataOutputStream(
						urlConn.getOutputStream());

				dop.writeBytes(upload);
				dop.flush();
				dop.close();
				DataInputStream dis = new DataInputStream(
						urlConn.getInputStream());
				String locPassage = dis.readUTF().toString();
				dis.close();

				if ("OK".equalsIgnoreCase(locPassage)) {
					return true;
				} else {
					return false;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return false;

	}

	protected void onPostExecute(Boolean result) {
		if (result) {
			Toast.makeText(mContext, "Update was successful", Toast.LENGTH_LONG)
					.show();
			centralDB.setUploadToTrue();
		}
		super.onPostExecute(result);
	}

}
