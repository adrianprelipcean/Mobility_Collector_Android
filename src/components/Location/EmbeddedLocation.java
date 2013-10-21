package components.Location;

import android.location.Location;
import android.util.Log;

public class EmbeddedLocation {
	LocationValues currentLocation;
	AccelerometerValues currentAcc;

	public EmbeddedLocation(Location l, int userID) {
		this.currentLocation = new LocationValues(l, userID);
	}

	public EmbeddedLocation(Location l, AccelerometerValues a, int userID) {
		Log.d("user id", userID + " ");
		this.currentLocation = new LocationValues(l, userID);
		this.currentAcc = a;
	}

	public EmbeddedLocation(LocationValues lV, AccelerometerValues a) {
		this.currentLocation = lV;
		this.currentAcc = a;
	}

	public LocationValues getCurrentLocation() {
		return currentLocation;
	}

	public AccelerometerValues getCurrentAcc() {
		return currentAcc;
	}

}
