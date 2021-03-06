package ua.killer.mobileapp.application;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import ua.killer.mobileapp.client.UserCheck;
import ua.killer.mobileapp.listeners.OnLoginListener;
import ua.killer.mobileapp.services.MobileAppService;

public class Utils {

	public static SharedPreferences getDefaultSPrefs(Context mContext) {
		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public static UserCheck checkUser(final String loginNick, final String loginPassword, final OnLoginListener listener) {
		UserCheck mUserCheck = new UserCheck(loginNick, loginPassword, listener);
		mUserCheck.connect();
		return mUserCheck;
	}

	public static boolean hasConnection(Context mContext) {
		ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo WiFi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return (WiFi != null && WiFi.isConnected() || mobile != null && mobile.isConnected());
	}

	public static boolean WiFiIsConnected(Context mContext) {
		ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo WiFi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return WiFi.isConnected();
	}

	public static void logout(Context mContext) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		sp.edit().clear().apply();
		Utils.removeServiceFromAlarm(mContext);
		NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll();
	}

	public static void addServiceToAlarm(Context mContext) {
		SharedPreferences sp = getDefaultSPrefs(mContext);
		int interval = Integer.valueOf(sp.getString("refresh", "5"));
		if (interval == 0)
			return;
		AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pIntent = PendingIntent.getService(mContext, 1, new Intent(mContext, MobileAppService.class), 0);
		long nextBindTime = System.currentTimeMillis() + (interval * 1000);
		mAlarmManager.set(AlarmManager.RTC, nextBindTime, pIntent);
	}

	public static void removeServiceFromAlarm(Context mContext) {
		AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pIntent = PendingIntent.getService(mContext, 1, new Intent(mContext, MobileAppService.class), 0);
		mAlarmManager.cancel(pIntent);
	}
	
	public static String strDeclension(int num, String... expressions) {
		if (expressions.length < 3)
			expressions[2] = expressions[1];
		int result = 0;
		double count = num % 100;
		if (count >= 5 && count <= 20)result = 2;
		else count = count % 10;
		if (count == 1)result = 0;
		else if (count >= 2 && count <= 4)result = 1;
		else result = 2;
		return expressions[result];
	}

}