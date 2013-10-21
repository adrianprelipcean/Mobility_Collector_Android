package config.Variables;

public class Variables {
    
    public static int samplingMinDistance = 50;
	public static int samplingMinTime = 25;

	public static int isAutoUpload = 1;
	public static double frequencyInHours = 1;

	public static boolean powerSaving = true;
	public static boolean equiDistance = true;

	public static boolean areAnnotationsAllowed = true;
	public static boolean isAccelerometerEmbedded = true;
	public static String annotationsStrings = "walking!__!swimming!__!running";

	public static long acceleromterFrequency = 30000;
	public static long accelerometerSleepTime = 300000;
	
	public static boolean isAccuracyFilterEnabled = false;
	public static float accuracyFilterValue = 0;
	public static boolean periodAnnotations = true;

	public static final String urlConnection = "http://utcb.ro"+"/Mobility_Collector_Servlet";

	public static final String aboutSectionString = "first!_!row!__!test!_!test!_!test!_!!__!new!_!test!_!new!_!test!_!new!_!test!_!!__!last!_!row";

    
}
