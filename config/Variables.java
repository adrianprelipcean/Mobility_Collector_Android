package config.Variables;

public class Variables {
    
    public static int samplingMinDistance = @VARIABLES.samplingMinDistance@;
	public static int samplingMinTime = @VARIABLES.samplingMinTime@;

	public static int isAutoUpload = @VARIABLES.isAutoUpload@;
	public static double frequencyInHours = @VARIABLES.frequencyInHours@;

	public static boolean powerSaving = @VARIABLES.powerSaving@;
	public static boolean equiDistance = @VARIABLES.equidistance@;

	public static boolean areAnnotationsAllowed = @VARIABLES.areAnnotationsAllowed@;
	public static boolean isAccelerometerEmbedded = @VARIABLES.isAccelerometerEmbedded@;
	public static String annotationsStrings = "@VARIABLES.annotationsStrings@";

	public static long acceleromterFrequency = @VARIABLES.accelerometerFrequency@;
	public static long accelerometerSleepTime = @VARIABLES.accelerometerSleepTime@;
	
	public static boolean isAccuracyFilterEnabled = @VARIABLES.isAccuracyFilterEnabled@;
	public static float accuracyFilterValue = @VARIABLES.accuracyFilterValue@;
	public static boolean periodAnnotations = @VARIABLES.periodAnnotations@;

	public static final String urlConnection = "@VARIABLES.urlConnection@"+"/Mobility_Collector_Servlet";

	public static final String aboutSectionString = "@VARIABLES.aboutSectionString@";

    
}
