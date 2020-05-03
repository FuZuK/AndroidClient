package ua.killer.mobileapp.application;

import ua.killer.mobileapp.configs.Settings;

public class Constants {
	public static String SERVER_HOST = Settings.getConfigValue("server_host");
	public static String SERVER_AUTH_URL = SERVER_HOST + "/api/mobile_app/auth.php";
	public static String SERVER_DATA_URL = SERVER_HOST + "/api/mobile_app/data.php";
	
	public static final String URL_NEW_MAIL = SERVER_HOST + "/new_mail.php";
	public static final String URL_MAIL_KONT = SERVER_HOST + "/mail.php?id=%d";
	public static final String URL_DISCUSSIONS = SERVER_HOST + "/user/discussions";
	public static final String URL_NOTIFICATION = SERVER_HOST + "/user/notification";
	public static final String URL_TAPE = SERVER_HOST + "/user/tape";
	public static final String URL_NEW_FRIENDS = SERVER_HOST + "/user/frends/new.php";
	public static final String URL_NEW_GUESTS = SERVER_HOST + "/user/myguest/";
}
