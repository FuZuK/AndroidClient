package ua.killer.mobileapp;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import ua.killer.mobileapp.application.Constants;
import ua.killer.mobileapp.application.Utils;
import ua.killer.mobileapp.cache.BitmapHttpLoaderCache;
import ua.killer.mobileapp.components.Photo;
import ua.killer.mobileapp.configs.Configs;
import ua.killer.mobileapp.listeners.BitmapHttpLoaderListener;
import ua.killer.mobileapp.listeners.NotificationsCompleteListener;

public class MobileAppNotifications {
	private NotificationsCompleteListener listener;
	private Configs configs;
	private Context mContext;
	private Resources res;

	public static final int NOTIF_MAIL = 100,
			NOTIF_DISCUSSIONS = 2,
			NOTIF_NOTIFICATION = 3,
			NOTIF_TAPE = 4,
			NOTIF_FRIENDS = 5,
			NOTIF_GUESTS = 6;
	
	public MobileAppNotifications(Context _mContext) {
		this.mContext = _mContext;
		configs = new Configs(mContext);
		res = mContext.getResources();
	}
	
	public void setNotificationsCompleteListener(NotificationsCompleteListener _listener) {
		this.listener = _listener;
	}

	public void handleJSON(JSONObject json) {
		try {
			JSONObject counters = json.getJSONObject("counters");
			JSONObject contents = json.getJSONObject("contents");

			Editor configsEditor = configs.getEditor();

			// messages
			int countMail = counters.getInt("mail");

			if (countMail > 0 && configs.showMail) {
				JSONArray mailContent = contents.getJSONArray("mail");

				for (int i = 0; i < mailContent.length(); i++) {
					final JSONObject message = mailContent.getJSONObject(i);
					final JSONObject messageUser = message.getJSONObject("user");
					JSONObject messageUserAvatar = messageUser.getJSONObject("avatar");
					final int messageUserID = messageUser.getInt("id");
					final int messageID = message.getInt("id");

					if (messageID <= configs.lastMessageID) {
						continue;
					}

					Photo avatar = Photo.parse(messageUserAvatar);
					avatar.setSize(128);
					BitmapHttpLoaderCache bmLoader = new BitmapHttpLoaderCache(avatar.getHash(), avatar.getUrl(), mContext, new BitmapHttpLoaderListener() {
						@Override
						public void onLoadStart() {}

						@Override
						public void onLoadFinish(Bitmap bitmap) {
							PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(String.format(Constants.URL_MAIL_KONT, messageUserID))), 0);

							try {
								String barTitle = "", notifTitle = "", notifText = "";
								barTitle = res.getString(R.string.message_from) + " " + messageUser.getString("nick");
								notifTitle = messageUser.getString("nick");
								notifText = message.getString("message");
								sendNotif(R.drawable.mail, bitmap, barTitle, notifTitle, notifText, contentIntent, NOTIF_MAIL + messageUser.getInt("id"));
								configs.lastMessageID = Math.max(configs.lastMessageID, messageID);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});

					try {
						bmLoader.load();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			configsEditor.putInt("lastMessageID", configs.lastMessageID);

			// discussions
			int countDiscussions = counters.getInt("discussions");

			if (countDiscussions > configs.lastCountDiscussions && configs.showDiscussions) {
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_DISCUSSIONS)), 0);
				String barTitle = countDiscussions + " " + Utils.strDeclension(countDiscussions, res.getString(R.string.discussions_1_new), res.getString(R.string.discussions_2_new), res.getString(R.string.discussions_5_new));
				String notifTitle = res.getString(R.string.discussions);

				sendNotif(R.drawable.discussions, barTitle, notifTitle, barTitle, contentIntent, NOTIF_DISCUSSIONS);
			}

			configsEditor.putInt("lastCountDiscussions", countDiscussions);

			// notifications
			int countNotification = counters.getInt("notification");

			if (countNotification > configs.lastCountNotification && configs.showNotification) {
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_NOTIFICATION)), 0);
				String barTitle = countNotification + " " + Utils.strDeclension(countNotification, res.getString(R.string.notifications_1_new), res.getString(R.string.notifications_2_new), res.getString(R.string.notifications_5_new));
				String notifTitle = res.getString(R.string.notifications);

				sendNotif(R.drawable.notification, barTitle, notifTitle, barTitle, contentIntent, NOTIF_NOTIFICATION);
			}

			configsEditor.putInt("lastCountNotification", countNotification);

			// tape
			int countTape = counters.getInt("tape");

			if (countTape > configs.lastCountTape && configs.showTape) {
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_TAPE)), 0);
				String barTitle = countTape + " " + Utils.strDeclension(countTape, res.getString(R.string.tape_1_new), res.getString(R.string.tape_2_new), res.getString(R.string.tape_5_new)) + " " + res.getString(R.string.in_tape);
				String notifTitle = res.getString(R.string.tape);

				sendNotif(R.drawable.tape, barTitle, notifTitle, barTitle, contentIntent, NOTIF_TAPE);
			}

			configsEditor.putInt("lastCountTape", countTape);

			// friends
			int countFriends = counters.getInt("friends");

			if (countFriends > 0 && configs.showFriends) {
				JSONArray friendsContent = contents.getJSONArray("friends");

				for (int i = 0; i < friendsContent.length(); i++) {
					JSONObject friend = friendsContent.getJSONObject(i);
					final JSONObject friendUser = friend.getJSONObject("user");
					JSONObject friendUserAvatar = friendUser.getJSONObject("avatar");
					final int friendID = friend.getInt("id");

					if (friendID <= configs.lastNewFriendID) {
						continue;
					}

					Photo avatar = Photo.parse(friendUserAvatar);
					avatar.setSize(128);

					BitmapHttpLoaderCache bmLoader = new BitmapHttpLoaderCache(avatar.getHash(), avatar.getUrl(), mContext, new BitmapHttpLoaderListener() {
						@Override
						public void onLoadStart() {}

						@Override
						public void onLoadFinish(Bitmap bitmap) {
							try {
								PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_NEW_FRIENDS)), 0);
								String barTitle = "", notifTitle = "", notifText = "";
								barTitle = friendUser.getString("nick") + " " + res.getString(R.string.user_want_to_be_friend);
								notifTitle = friendUser.getString("nick");
								notifText = res.getString(R.string.want_to_be_your_friend);

								sendNotif(R.drawable.friends, bitmap, barTitle, notifTitle, notifText, contentIntent, NOTIF_FRIENDS + friendUser.getInt("id"));
								configs.lastNewFriendID = Math.max(configs.lastNewFriendID, friendID);
							} catch (JSONException e) {
								e.printStackTrace();							}
						}
					});

					try {
						bmLoader.load();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			configsEditor.putInt("lastNewFriendID", configs.lastNewFriendID);

			// guests
			int countGuests = counters.getInt("guests");

			if (countGuests > 0 && configs.showGuests) {
				JSONArray guestsContent = contents.getJSONArray("guests");

				for (int i = 0; i < guestsContent.length(); i++) {
					JSONObject guest = guestsContent.getJSONObject(i);
					final JSONObject guestUser = guest.getJSONObject("user");
					JSONObject guestUserAvatar = guestUser.getJSONObject("avatar");
					final int guestID = guest.getInt("id");

					if (guestID <= configs.lastNewGuestID) {
						continue;
					}

					Photo avatar = Photo.parse(guestUserAvatar);
					avatar.setSize(128);

					BitmapHttpLoaderCache bmLoader = new BitmapHttpLoaderCache(avatar.getHash(), avatar.getUrl(), mContext, new BitmapHttpLoaderListener() {
						@Override
						public void onLoadStart() {}

						@Override
						public void onLoadFinish(Bitmap bitmap) {
							try {
								PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_NEW_GUESTS)), 0);
								String barTitle = "", notifTitle = "", notifText = "";
								barTitle = guestUser.getString("nick") + " " + res.getString(R.string.user_visited_your_page);
								notifTitle = guestUser.getString("nick");
								notifText = res.getString(R.string.visited_your_page);

								sendNotif(R.drawable.guests, bitmap, barTitle, notifTitle, notifText, contentIntent, NOTIF_GUESTS);
								configs.lastNewGuestID = Math.max(configs.lastNewGuestID, guestID);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					});

					try {
						bmLoader.load();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			configsEditor.putInt("lastNewGuestID", configs.lastNewGuestID);

			configsEditor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (listener != null) {
			listener.onComplete();
		}
	}
	
	private void sendNotif(int icon, String barText, String title, String text, PendingIntent pIntent, int notifId) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
				.setContentText(text).setContentTitle(title)
				.setTicker(barText)
				.setSmallIcon(icon)
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pIntent)
				.setAutoCancel(true);

		if (configs.playSound) {
			builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		}

		if (configs.playVibration) {
			builder.setVibrate(new long[]{0, 1000});
		}

		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(notifId, builder.build());
	}

	private void sendNotif(int icon, Bitmap largeIcon, String barText, String title, String text, PendingIntent pIntent, int notifId) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
				.setContentText(text)
				.setContentTitle(title)
				.setTicker(barText)
				.setLargeIcon(largeIcon)
				.setSmallIcon(icon)
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pIntent)
				.setAutoCancel(true);

		if (configs.playSound) {
			builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		}

		if (configs.playVibration) {
			builder.setVibrate(new long[]{0, 1000});
		}

		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(notifId, builder.build());
	}
}
