package ua.killer.mobileapp.services;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import ua.killer.mobileapp.MobileAppNotifications;
import ua.killer.mobileapp.application.Constants;
import ua.killer.mobileapp.application.Utils;
import ua.killer.mobileapp.client.ServerConnector;
import ua.killer.mobileapp.configs.Configs;
import ua.killer.mobileapp.listeners.NotificationsCompleteListener;
import ua.killer.mobileapp.listeners.OnServerConnectListener;

public class MobileAppService extends Service implements OnServerConnectListener {
	private Context mContext;
	private Configs configs;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		mContext = this;
		configs = new Configs(mContext);

		if (
				!Utils.hasConnection(mContext)
				|| configs.refresh == 0
				|| !configs.isAuth()
				|| (
						!configs.showMail
							&& !configs.showDiscussions
							&& !configs.showNotification
							&& !configs.showTape
							&& !configs.showFriends
							&& !configs.showGuests
				)
				|| (
						configs.onlyWiFi
							&& !Utils.WiFiIsConnected(this)
				)
		) {
			if (configs.isAuth()) {
				Utils.addServiceToAlarm(mContext);
			}

			return START_STICKY;
		}

		refreshData();
		
		return START_STICKY;
	}
	
	private void refreshData() {
		Map<Object, Object> data = new HashMap<Object, Object>();

		data.put("token", configs.userToken);
		data.put("id", configs.userID);
		data.put("offline_time", configs.offlineTime);

		ServerConnector connector = new ServerConnector(data, this);

		try {
			connector.send(Constants.SERVER_DATA_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStartConnecting() {
	}

	@Override
	public void onFinishConnecting(String responseText) {
		if (responseText == null || TextUtils.isEmpty(responseText)) {
			Utils.addServiceToAlarm(mContext);

			return;
		}

		try {
			JSONObject json = new JSONObject(responseText);
			if (json.getInt("status") == -1) {
				Utils.logout(mContext);

				return;
			}

			if (!configs.isAuth()) {
				return;
			}

			MobileAppNotifications appNotifs = new MobileAppNotifications(mContext);

			appNotifs.setNotificationsCompleteListener(ncl);
			appNotifs.handleJSON(json.getJSONObject("data"));
		} catch (JSONException e) {
			e.printStackTrace();
			Utils.addServiceToAlarm(mContext);
		}
	}

	private NotificationsCompleteListener ncl = new NotificationsCompleteListener() {
		@Override
		public void onComplete() {
			Utils.addServiceToAlarm(mContext);
		}
	};
}
