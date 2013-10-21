package components.Location;

import java.util.LinkedList;

import config.Variables.Variables;

import android.location.Location;
import android.util.Log;

/*
 * CLASS USED FOR EQUIDISTANT TRACKING 
 */

public class EquidistanceTracking {

	static double Threshold_Frequency = 0.1;

	static LinkedList<Location> locationList;
	static double currentFrequency;
	static int requiredSize;

	private static EquidistanceTracking instance = null;

	protected EquidistanceTracking() {
		// Exists only to defeat instantiation.
		locationList = new LinkedList<Location>();
		currentFrequency = (double) Variables.samplingMinTime / 1000;
		Log.d("Debug tag initial frequency", Variables.samplingMinTime + "");
	}

	public static EquidistanceTracking getInstance() {
		if (instance == null) {
			instance = new EquidistanceTracking();
		}
		return instance;
	}

	/*
	 * adds an extra location to the list
	 */
	public static boolean addLocationToList(Location l) {

		/*
		 * remove the tail if the size is greater than expected
		 */

		if (locationList.size() >= getRequiredSize()) {
			for (int i = 0; i <= (locationList.size() - getRequiredSize()) + 1; i++)
				locationList.remove(i);
		}

		// Log.d("Debug tag", "current" + locationList.size()
		// +" needed"+getRequiredSize());

		return locationList.add(l);
	}

	private static int getRequiredSize() {
		// TODO Auto-generated method stub
		if (currentFrequency <= 5)
			requiredSize = 12;
		else if (currentFrequency <= 10)
			requiredSize = 6;
		else
			requiredSize = 3;

		// Log.d("Debug tag required size", "required size: " + requiredSize +
		// " currentFrequency " + currentFrequency);

		return requiredSize;
	}

	private static double getPredictedFrequency() {
		double predictedFrequency;
		double incrementalFrequencyMinimum = 0;
		double incrementalFrequencyCurrent = 0;
		Location fromLocation = null;
		Location toLocation = null;

		for (Location l : locationList) {
			if (fromLocation != null && toLocation != null) {

				/*
				 * Consequent valid sets
				 */
				fromLocation = toLocation;
				toLocation = l;

				if (fromLocation.distanceTo(toLocation) > 0) {
					incrementalFrequencyCurrent = 50
							* ((toLocation.getTime() - fromLocation.getTime()) / 1000)
							/ (fromLocation.distanceTo(toLocation));

					if ((incrementalFrequencyMinimum > incrementalFrequencyCurrent)
							|| (incrementalFrequencyMinimum == 0))
						incrementalFrequencyMinimum = incrementalFrequencyCurrent;
				}
			} else {
				if (fromLocation == null)
					fromLocation = l;
				else {

					/*
					 * First valid set
					 */
					toLocation = l;
					incrementalFrequencyMinimum = 50
							* ((toLocation.getTime() - fromLocation.getTime()) / 1000)
							/ (fromLocation.distanceTo(toLocation));
				}
			}
		}

		if (incrementalFrequencyMinimum >= (double) Variables.samplingMinTime / 1000)
			predictedFrequency = (double) Variables.samplingMinTime / 1000;
		else if (incrementalFrequencyMinimum <= 5)
			predictedFrequency = 1;
		else
			predictedFrequency = incrementalFrequencyMinimum;

		return predictedFrequency;
	}

	public static double checkForLocationAdjustment() {
		if (locationList.size() == getRequiredSize()) {

			double predictedFrequency = getPredictedFrequency();

			if (predictedFrequency != currentFrequency) {

				if (Math.abs(predictedFrequency - currentFrequency) >= Threshold_Frequency
						* predictedFrequency) {

					/*
					 * new frequency is needed to maintain equidistant tracking
					 */

					Log.d("Debug tag changed frequency", predictedFrequency
							+ " from " + currentFrequency);

					setCurrentFrequency(predictedFrequency);
					return predictedFrequency * 1000;
				}
			}
		}

		return -1;
	}

	private static void setCurrentFrequency(double currentFrequency) {
		Log.d("Debug tag set frequency ", currentFrequency + "");
		locationList.removeFirst();
		EquidistanceTracking.currentFrequency = currentFrequency;
	}
}
