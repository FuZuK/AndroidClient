package ua.killer.mobileapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ua.killer.mobileapp.application.Utils;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent arg1) {
		Utils.addServiceToAlarm(ctx);
	}

}
