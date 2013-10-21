package utilities;

import components.Service.CollectingService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub	
		
		// CHECK IF THE SERVICE WAS RUNNING WHEN THE PHONE DIED 
		
		GetInfo gI = new GetInfo(context);
		if (gI.isServiceOn())
		{
		Intent startCollectionService = new Intent(CollectingService.class.getName());       
		if( "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) context.startService(startCollectionService);
		}
			
	}

}
