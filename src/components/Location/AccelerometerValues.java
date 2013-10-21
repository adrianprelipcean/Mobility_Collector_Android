package components.Location;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import android.util.Log;

public class AccelerometerValues {

	float xMean, yMean, zMean, totalMean;
	float xStdDev, yStdDev, zStdDev, totalStdDev;
	float xMinimum, xMaximum, yMin, yMax, zMin, zMax, totalMin, totalMax;
	int xNumberOfPeaks, yNumberOfPeaks, zNumberOfPeaks, totalNumberOfPeaks;
	int totalNumberOfSteps;
	boolean xIsMoving = false, yIsMoving = false, zIsMoving = false,
			totalIsMoving = false;
	int size;

	public AccelerometerValues(LinkedList<float[]> accVal) {
		feedAccelerometerValues(accVal);
	}
	
	public AccelerometerValues(float xMean, float yMean, float zMean,
			float totalMean, float xStdDev, float yStdDev, float zStdDev,
			float totalStdDev, float xMin, float xMax, float yMin, float yMax,
			float zMin, float zMax, float totalMin, float totalMax,
			int xNumberOfPeaks, int yNumberOfPeaks, int zNumberOfPeaks,
			int totalNumberOfPeaks, int totalNumberOfSteps, boolean xIsMoving,
			boolean yIsMoving, boolean zIsMoving, boolean totalIsMoving,
			int size) {
		super();
		this.xMean = xMean;
		this.yMean = yMean;
		this.zMean = zMean;
		this.totalMean = totalMean;
		this.xStdDev = xStdDev;
		this.yStdDev = yStdDev;
		this.zStdDev = zStdDev;
		this.totalStdDev = totalStdDev;
		this.xMinimum = xMin;
		this.xMaximum = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
		this.totalMin = totalMin;
		this.totalMax = totalMax;
		this.xNumberOfPeaks = xNumberOfPeaks;
		this.yNumberOfPeaks = yNumberOfPeaks;
		this.zNumberOfPeaks = zNumberOfPeaks;
		this.totalNumberOfPeaks = totalNumberOfPeaks;
		this.totalNumberOfSteps = totalNumberOfSteps;
		this.xIsMoving = xIsMoving;
		this.yIsMoving = yIsMoving;
		this.zIsMoving = zIsMoving;
		this.totalIsMoving = totalIsMoving;
		this.size = size;
	}

	public AccelerometerValues feedAccelerometerValues(
			LinkedList<float[]> accVal) {
		return triggerComputationsAndGenerateObject(accVal);
	}

	private AccelerometerValues triggerComputationsAndGenerateObject(
			LinkedList<float[]> accVals) {
		LinkedList<Float> xValues = new LinkedList<Float>();
		LinkedList<Float> yValues = new LinkedList<Float>();
		LinkedList<Float> zValues = new LinkedList<Float>();
		LinkedList<Float> totalValues = new LinkedList<Float>();

		for (float[] vals : accVals)

		{
			xValues.add(vals[0]);
			yValues.add(vals[1]);
			zValues.add(vals[2]);
			totalValues.add((float) Math.pow(vals[0] * vals[0] + vals[1]
					* vals[1] + vals[2] * vals[2], (double) 0.5));
		}

		setxMean(getMean(xValues));
		setyMean(getMean(yValues));
		setzMean(getMean(zValues));
		setTotalMean(getMean(totalValues));

		setxMin(getMin(xValues));
		setyMin(getMin(yValues));
		setzMin(getMin(zValues));
		setTotalMin(getMin(totalValues));

		setxMax(getMax(xValues));
		setyMax(getMax(yValues));
		setzMax(getMax(zValues));
		setTotalMax(getMax(totalValues));

		setxStdDev(getStdDev(xValues, getxMean()));
		setyStdDev(getStdDev(yValues, getyMean()));
		setzStdDev(getStdDev(zValues, getzMean()));
		setTotalStdDev(getStdDev(totalValues, getTotalMean()));

		setxIsMoving(getMovement(xValues, getxMean()));
		setyIsMoving(getMovement(yValues, getyMean()));
		setzIsMoving(getMovement(zValues, getzMean()));
		setTotalIsMoving(getMovement(totalValues, getTotalMean()));

		setxNumberOfPeaks(getNumberOfPeaks(xValues, getxMean(), getxStdDev()));
		setyNumberOfPeaks(getNumberOfPeaks(yValues, getyMean(), getyStdDev()));
		setzNumberOfPeaks(getNumberOfPeaks(zValues, getzMean(), getzStdDev()));
		setTotalNumberOfPeaks(getNumberOfPeaks(totalValues, getTotalMean(),
				getTotalStdDev()));

		setTotalNumberOfSteps(getNumberOfSteps(totalValues, getTotalMean(),
				getTotalStdDev()));

		setSize(totalValues.size());

		Log.d("accelerometer object detected", getTotalMax() + " "
				+ getTotalMin() + " " + getTotalMean() + " "
				+ getTotalNumberOfPeaks() + " " + getTotalStdDev() + " "
				+ isTotalIsMoving());

		return null;
	}

	public void setTotalNumberOfSteps(int totalNumberOfSteps) {
		this.totalNumberOfSteps = totalNumberOfSteps;
	}

	private void setxMin(float xMin) {
		this.xMinimum = xMin;
	}

	private void setxMax(float xMax) {
		this.xMaximum = xMax;
	}

	private void setyMin(float yMin) {
		this.yMin = yMin;
	}

	private void setyMax(float yMax) {
		this.yMax = yMax;
	}

	private void setzMin(float zMin) {
		this.zMin = zMin;
	}

	private void setzMax(float zMax) {
		this.zMax = zMax;
	}

	private void setTotalMin(float totalMin) {
		this.totalMin = totalMin;
	}

	private void setTotalMax(float totalMax) {
		this.totalMax = totalMax;
	}

	/**
	 * Gets the relevant number of picks in the current iteration of the
	 * recorded accelerometer values
	 * 
	 * @param xValues
	 *            accelerometer values in a linked list
	 * @param xMean
	 *            the mean of the accelerometer values
	 * @param xStdDev
	 *            the standard devation of the accelerometer values
	 * @return the number of relevant peaks
	 */
	private int getNumberOfPeaks(LinkedList<Float> xValues, float xMean,
			float xStdDev) {

		float comparisonValue = xMean + (float) (Math.sqrt((double) xStdDev));

		boolean prevValue = false;
		boolean currentValue = false;
		int index = 0;
		int numberOfPeaks = 0;

		for (float f : xValues) {
			if (index < 1) {
				if (index == 0)
					prevValue = f > comparisonValue;
			} else {
				currentValue = f > comparisonValue;
				if (currentValue && !prevValue)
					numberOfPeaks++;
				prevValue = currentValue;
			}
			index++;
		}
		return numberOfPeaks;
	}

	/*
	 * 
	 * IGNORE THIS IN THE MOBILE GHENT CONFERENCE
	 */
	private int getNumberOfSteps(LinkedList<Float> xValues, float xMean,
			float xStdDev) {
		float comparisonValue = xMean + (float) (Math.sqrt((double) xStdDev));

		boolean prevValue = false;
		boolean currentValue = false;
		int index = 0;
		int numberOfSteps = 0;

		if (comparisonValue > 3) {
			for (float f : xValues) {
				if (index < 1) {
					if (index == 0)
						prevValue = f > comparisonValue;
				} else {
					currentValue = f > comparisonValue;
					if (currentValue && !prevValue)
						numberOfSteps++;
					prevValue = currentValue;
				}
				index++;
			}
			/**
			 * Maybe this is implied
			 */
			/*
			 * for (LinkedList<Float> clst : listOfClusters){ if
			 * (getMax(clst)>comparisonValue) numberOfSteps++; }
			 */
		}
		return numberOfSteps;
	}

	/**
	 * 
	 * @param xValues
	 *            accelerometer values linked list
	 * @param mean
	 *            previously computed mean of the accelerometer values
	 * @return true if the user is moving
	 */
	private boolean getMovement(LinkedList<Float> xValues, float mean) {

		/*
		 * Test data: 0.08 for standing still 0.5-1 for having phone inside the
		 * pocket while tapping 1-2 hand-derived movement while grabbing the
		 * phone >2.5 moving
		 */

		if (mean > 2.5)
			return true;

		return false;
	}

	private float getMax(LinkedList<Float> xValues) {
		float xMax;
		xMax = xValues.getFirst();
		for (float f : xValues)
			if (xMax < f)
				xMax = f;
		return xMax;
	}

	private float getMin(LinkedList<Float> xValues) {
		float xMin;
		xMin = xValues.getFirst();
		for (float f : xValues)
			if (xMin > f)
				xMin = f;
		return xMin;
	}

	private float getStdDev(LinkedList<Float> xValues, float mean) {
		float squareSum = 0;
		int numberOfValues = 0;
		for (float f : xValues) {
			squareSum = (squareSum + (float) Math.pow((f - mean), 2));
			numberOfValues++;
		}
		if (numberOfValues == 0)
			return 0;
		return (float) Math.sqrt(squareSum / numberOfValues);
	}

	private float getMean(LinkedList<Float> xValues) {

		int nr = 0;
		float sum = 0;

		for (float f : xValues) {
			sum = sum + f;
			nr++;
		}

		if (nr == 0) {
			return 0;
		}

		return (float) sum / nr;
	}

	private void setxMean(float xMean) {
		this.xMean = xMean;
	}

	private void setyMean(float yMean) {
		this.yMean = yMean;
	}

	private void setzMean(float zMean) {
		this.zMean = zMean;
	}

	private void setTotalMean(float totalMean) {
		this.totalMean = totalMean;
	}

	private void setxStdDev(float xStdDev) {
		this.xStdDev = xStdDev;
	}

	private void setyStdDev(float yStdDev) {
		this.yStdDev = yStdDev;
	}

	private void setzStdDev(float zStdDev) {
		this.zStdDev = zStdDev;
	}

	private void setTotalStdDev(float totalStdDev) {
		this.totalStdDev = totalStdDev;
	}

	private void setxNumberOfPeaks(int xNumberOfPeaks) {
		this.xNumberOfPeaks = xNumberOfPeaks;
	}

	private void setyNumberOfPeaks(int yNumberOfPeaks) {
		this.yNumberOfPeaks = yNumberOfPeaks;
	}

	private void setzNumberOfPeaks(int zNumberOfPeaks) {
		this.zNumberOfPeaks = zNumberOfPeaks;
	}

	private void setTotalNumberOfPeaks(int totalNumberOfPeaks) {
		this.totalNumberOfPeaks = totalNumberOfPeaks;
	}

	private void setxIsMoving(boolean xIsMoving) {
		this.xIsMoving = xIsMoving;
	}

	private void setyIsMoving(boolean yIsMoving) {
		this.yIsMoving = yIsMoving;
	}

	private void setzIsMoving(boolean zIsMoving) {
		this.zIsMoving = zIsMoving;
	}

	private void setTotalIsMoving(boolean totalIsMoving) {
		this.totalIsMoving = totalIsMoving;
	}

	private void setSize(int size) {
		this.size = size;
	}

	public float getxMean() {
		return xMean;
	}

	public float getyMean() {
		return yMean;
	}

	public float getzMean() {
		return zMean;
	}

	public float getTotalMean() {
		return totalMean;
	}

	public float getxStdDev() {
		return xStdDev;
	}

	public float getyStdDev() {
		return yStdDev;
	}

	public float getzStdDev() {
		return zStdDev;
	}

	public float getTotalStdDev() {
		return totalStdDev;
	}

	public int getxNumberOfPeaks() {
		return xNumberOfPeaks;
	}

	public int getyNumberOfPeaks() {
		return yNumberOfPeaks;
	}

	public int getzNumberOfPeaks() {
		return zNumberOfPeaks;
	}

	public int getTotalNumberOfPeaks() {
		return totalNumberOfPeaks;
	}

	public boolean isxIsMoving() {
		return xIsMoving;
	}

	public boolean isyIsMoving() {
		return yIsMoving;
	}

	public boolean iszIsMoving() {
		return zIsMoving;
	}

	public boolean isTotalIsMoving() {
		return totalIsMoving;
	}

	public int getSize() {
		return size;
	}

	public float getxMin() {
		return xMinimum;
	}

	public float getxMax() {
		return xMaximum;
	}

	public float getyMin() {
		return yMin;
	}

	public float getyMax() {
		return yMax;
	}

	public float getzMin() {
		return zMin;
	}

	public float getzMax() {
		return zMax;
	}

	public float getTotalMin() {
		return totalMin;
	}

	public float getTotalMax() {
		return totalMax;
	}

	public static LinkedHashMap<String, String> getAllElements() {
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		for (Field f : AccelerometerValues.class.getDeclaredFields()) {
		/*	Log.d("accelerometer fields", f.getName() + " "
					+ f.getType().getName());*/
			hashMap.put(f.getName(), utilities.GetInfo.convertTypeJavaToSql(f
					.getType().getName()));
			/*Log.d("inserted accelerometer fields",
					f.getName()
							+ " "
							+ utilities.GetInfo.convertTypeJavaToSql(f
									.getType().getName()));*/
		}
		return hashMap;
	}
}
