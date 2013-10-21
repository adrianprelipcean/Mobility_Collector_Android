package components.Location;

import java.util.LinkedList;

import utilities.GetInfo;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import components.DatabaseHandler.AdministrativeDatabase;
import components.DatabaseHandler.LocationAccelerationDatabase;
import components.DatabaseHandler.LocationSimpleDatabase;
import components.PowerSaving.PowerSavingAlarm;

import config.Variables.Constants;
import config.Variables.Variables;

public class EmbeddedLocationListener {

	private static SensorManager mSensorManager;
	private static SensorEventListener mSensorListener;

	private static LocationManager locationManager;
	private static LocationListener locationListener;

	private static EmbeddedLocation currentEmbeddedLocation;

	private static Context mContext;
	private static long timeFrequency;
	private static float distanceFrequency;

	private static float[] gravity;
	private static final float ALPHA = 0.8f;
	private static LinkedList<float[]> accelerometerValues;
	private boolean skipOneLocation = false;

	/*
	 * private static boolean isAccelerometerOn = false; private static boolean
	 * isRunning = false;
	 */

	PowerSavingAlarm powerAlarm;
	LocationAccelerationDatabase locationDatabase;
	LocationSimpleDatabase locationDatabaseSimple;
	AdministrativeDatabase adminDb;
	GetInfo info;
	int userId;

	public EmbeddedLocationListener(Context ctx, long timeFreq, float distFreq) {
		mContext = ctx;
		timeFrequency = timeFreq;
		distanceFrequency = distFreq;
		gravity = new float[3];

		locationDatabase = new LocationAccelerationDatabase(
				Constants.databaseName, Constants.locationTable, mContext);
		locationDatabaseSimple = new LocationSimpleDatabase(
				Constants.databaseName, Constants.simpleLocationTable, mContext);
		adminDb = new AdministrativeDatabase(Constants.databaseName,
				Constants.adminTable, mContext);

		if (Variables.powerSaving) {
			powerAlarm = new PowerSavingAlarm();
			powerAlarm.SetAlarm(mContext, true);
		}
	}

	public static long getTimeFreq() {
		return timeFrequency;
	}

	public static float getDistanceFreq() {
		return distanceFrequency;
	}

	/**
	 * This calls for the normal launch of the location listener
	 */
	public void startListening() {

		Log.d("GPS PROVIDER", "ENABLED");

		// isRunning = true;

		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);

		locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location location) {

				if (!skipOneLocation)

				{
					// check that the user id is in its correct instance

					if (userId == 0)
						userId = adminDb.getUserId();

					/*
					 * this also resets the accelerometer values
					 */

					if (Variables.powerSaving)
						powerAlarm.CancelAlarm(mContext, true);

					if (Variables.isAccelerometerEmbedded) {
						stopAccelerometer();

						/*
						 * instance of the ongoing accelerometer values
						 */

						if (getAccelerometerValues().size() != 0)

						/*
						 * the location and embedded accelerometer reading
						 */

						{

							if (isAccurate(location)) {
								currentEmbeddedLocation = new EmbeddedLocation(
										location, new AccelerometerValues(
												getAccelerometerValues()),
										userId);

								locationDatabase
								 .insertLocationIntoDb(currentEmbeddedLocation);
							}

							resetAccelerometerValues();
							// feed listener

							/*
							 * start new instance of the accelerometer
							 */

						}
						startAccelerometer();
					} else {
						if (isAccurate(location))
							locationDatabaseSimple
									.insertLocationIntoDb(new LocationValues(
											location, userId));
					}

					if (Variables.equiDistance)
						tryToAdaptSpeedUsingList(location);

					if (Variables.powerSaving) {
						powerAlarm = new PowerSavingAlarm();
						powerAlarm.SetAlarm(mContext, true);
					}
				} else
					skipOneLocation = false;
			}

			private boolean isAccurate(Location location) {
				if (Variables.isAccuracyFilterEnabled) {
					if (location.getAccuracy() <= Variables.accuracyFilterValue)
						return true;
					else
						return false;
				}
				return true;
			}
		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				timeFrequency, distanceFrequency, locationListener);

	/* for (EmbeddedLocation lv:	locationDatabase.getAllLocationsFromDatabase())
	 {
		Location l = new Location("");
		l.setTime(lv.currentLocation.time_);
		l.setLatitude(lv.currentLocation.lat_);
		l.setLongitude(lv.currentLocation.lon_);
		locationListener.onLocationChanged(l);
	 }*/
	}

	private void resetAccelerometerValues() {
		accelerometerValues = new LinkedList<float[]>();
	}

	public void stopListening() {
		try {
			locationManager.removeUpdates(locationListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
		stopAccelerometer();

		Log.d("GPS PROVIDER", "DISABLED");

	}

	public void stopAlarm() {
		if (Variables.powerSaving) {
			try {
				powerAlarm.CancelAlarm(mContext, true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				powerAlarm.CancelAlarm(mContext);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void startAccelerometer() {

		// isAccelerometerOn = true;
		emptyAccelerometerValuesGetNewList();
		mSensorListener = new SensorEventListener() {
			int counter = 0;

			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {

			}

			@Override
			public void onSensorChanged(SensorEvent event) {

				float[] values = event.values.clone();
				counter++;

				values = lowPass(values[0], values[1], values[2]);
				if (counter > 10)
					accelerometerValues.add(values);
			}
		};

		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				getNeededDelay());

	}

	private static int getNeededDelay() {
		//not needed - gets service killed
		/*if (timeFrequency == 1000)
			return SensorManager.SENSOR_DELAY_FASTEST;
		else if (timeFrequency <= 10000)
			return SensorManager.SENSOR_DELAY_GAME;
		else if (timeFrequency <= 20000)
			return SensorManager.SENSOR_DELAY_UI;*/
		return SensorManager.SENSOR_DELAY_NORMAL;
	}

	private static void stopAccelerometer() {

		try {
			mSensorManager.unregisterListener(mSensorListener);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static LinkedList<float[]> getAccelerometerValues() {
		/*
		 * Singleton accelerometer values
		 */

		if (accelerometerValues == null)
			accelerometerValues = new LinkedList<float[]>();
		return accelerometerValues;
	}

	/**
	 * Reset the list's content
	 */
	private static LinkedList<float[]> emptyAccelerometerValuesGetNewList() {

		accelerometerValues = getAccelerometerValues();

		accelerometerValues = new LinkedList<float[]>();

		gravity = new float[3];

		return accelerometerValues;
	}

	/**
	 * Smooth the accelerometer values using a low pass filter
	 */
	private static float[] lowPass(float x, float y, float z) {
		float[] filteredValues = new float[3];

		gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
		gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
		gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

		filteredValues[0] = x - gravity[0];
		filteredValues[1] = y - gravity[1];
		filteredValues[2] = z - gravity[2];

		return filteredValues;
	}

	@SuppressWarnings("static-access")
	private void tryToAdaptSpeedUsingList(Location location) {

		EquidistanceTracking.getInstance().addLocationToList(location);

		final long newFrequency = Math.round(EquidistanceTracking.getInstance()
				.checkForLocationAdjustment());
		if (newFrequency != -1) {
			
			if (newFrequency > 10000) {

				locationManager.removeUpdates(locationListener);

				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, newFrequency,
						Variables.samplingMinDistance, locationListener);
			} else
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, newFrequency,
						Variables.samplingMinDistance, locationListener);
			skipOneLocation = true;
		}
	}
}
