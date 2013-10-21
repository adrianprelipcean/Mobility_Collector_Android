package components.Service;

import java.util.Timer;
import java.util.TimerTask;

import utilities.GetInfo;
import utilities.UploadAsync;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import components.Location.EmbeddedLocationListener;

import config.Variables.Variables;
import design.Classes.ServiceHandling;

public class CollectingService extends Service {

	public static EmbeddedLocationListener mListener;
	public GetInfo gI;
	private Timer timer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		stopListening();
		mListener.stopAlarm();
		timer.cancel();
		gI.setServiceOff();

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		gI = new GetInfo(this);

		gI.setServiceOn();

		mListener = new EmbeddedLocationListener(this,
				Variables.samplingMinTime, Variables.samplingMinDistance);

		startListening();

		autoUploading(this);

		Intent interactionActivity = new Intent(this, ServiceHandling.class);

		PendingIntent pIntent = PendingIntent.getActivity(this, 0,
				interactionActivity, 0);

		PendingIntent myservicefinal = PendingIntent.getActivity(this, 0,
				interactionActivity, 0);

		// TODO Change the icon to a project specific icon
		Notification noti = new NotificationCompat.Builder(this)
				.setContentTitle("You are currently collecting data")
				.setContentText("Mobility Collector")
				.setSmallIcon(se.kth.mobilitycollector.R.drawable.ic_mobility)
				.addAction(se.kth.mobilitycollector.R.drawable.ic_mobility,
						"Handle the collection", myservicefinal)
				.setContentIntent(pIntent).build();
		@SuppressWarnings("unused")
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		startForeground(1, noti);

		return START_STICKY;
	}

	public static void startListening() {
		mListener.startListening();
	}

	public static void stopListening() {
		mListener.stopListening();
	}

	public void autoUploading(final Context ctx) {
		new UploadAsync(ctx).execute();

		timer = new Timer();
		if (Variables.isAutoUpload == 1) {
			timer.schedule(new TimerTask() {
				@Override
				public void run()

				{
					if (gI.isOnline())
						try {
							new UploadAsync(ctx).execute();
						} catch (Exception e) {
						}
				}

			}, 5 * 60 * 1000, (long) (60 * 60 * 1000 * Variables.frequencyInHours));
		}

		else {
			timer.cancel();
		}
	}

}
