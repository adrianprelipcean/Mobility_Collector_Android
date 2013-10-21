package components.Location;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

public class AnnotationsValues {
	int userid;
	long annotationStartTime;
	long annotationStopTime;
	String annotationValues;

	public AnnotationsValues(int uid, long start, long stop, String val) {
		this.userid = uid;
		this.annotationStartTime = start;
		this.annotationStopTime = stop;
		this.annotationValues = val;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public long getAnnotationStartTime() {
		return annotationStartTime;
	}

	public void setAnnotationStartTime(long annotationStartTime) {
		this.annotationStartTime = annotationStartTime;
	}

	public long getAnnotationStopTime() {
		return annotationStopTime;
	}

	public void setAnnotationStopTime(long annotationStopTime) {
		this.annotationStopTime = annotationStopTime;
	}

	public String getAnnotationValues() {
		return annotationValues;
	}

	public void setAnnotationValues(String annotationValues) {
		this.annotationValues = annotationValues;
	}

	public static LinkedHashMap<String, String> getAllElements() {
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();

		for (Field f : AnnotationsValues.class.getDeclaredFields()) {
			hashMap.put(f.getName(), utilities.GetInfo.convertTypeJavaToSql(f
					.getType().getName()));
		}
		return hashMap;
	}
}
