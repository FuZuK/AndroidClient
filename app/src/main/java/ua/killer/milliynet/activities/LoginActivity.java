package ua.killer.milliynet.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ua.killer.milliynet.R;
import ua.killer.milliynet.application.Utils;
import ua.killer.milliynet.client.UserCheck;
import ua.killer.milliynet.configs.Settings;
import ua.killer.milliynet.listeners.OnLoginListener;

public class LoginActivity extends Activity implements OnClickListener, OnLoginListener {
	private Button buttonLogin;
	private EditText editTextNick, editTextPassword;
	private String loginNick, loginPassword;
	private ProgressDialog progressDialog;
	private SharedPreferences sPrefs;
	private Context mContext;
	
	private UserCheck mUserCheck;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity_layout);
		
		mContext = this;
		sPrefs = Utils.getDefaultSPrefs(mContext);
		
		if (!sPrefs.contains("first_start"))
			firstStart();
		
		if (!TextUtils.isEmpty(sPrefs.getString("user_token", "")))
			startSettingsActivity();
		
		initInterface();
		
		mUserCheck = (UserCheck) getLastNonConfigurationInstance();
	    if (mUserCheck != null) {
	    	mUserCheck.setOnLoginListener(this);
	    	if (mUserCheck.isLoading())
	    		createProgressDialog();
	    }
	}

	private void initInterface() {
		buttonLogin = (Button) findViewById(R.id.login);
		editTextNick = (EditText) findViewById(R.id.nick);
		editTextPassword = (EditText) findViewById(R.id.password);
		
		editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		    	switch (actionId) {
		    	case EditorInfo.IME_ACTION_DONE:
		        	checkData();
		            return true;
		    	}
		        return false;
		    }
		});
		
		buttonLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.login:
			checkData();
			break;
		}
	}

	private void checkData() {
		loginNick = editTextNick.getText().toString();
		loginPassword = editTextPassword.getText().toString();
		
		if (TextUtils.isEmpty(loginNick) || TextUtils.isEmpty(loginPassword)) {
			Toast.makeText(this, getResources().getString(R.string.error_empty_data), Toast.LENGTH_SHORT).show();
			return;
		}

		if (!Utils.hasConnection(mContext)) {
			Toast.makeText(mContext, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
			return;
		}

		mUserCheck = Utils.checkUser(loginNick, loginPassword, this);
	}
	
	@Override
	public void onCheckStart(String nick, String password) {
		createProgressDialog();
	}

	@Override
	public void onCheckFinish(String nick, String password, JSONObject result) {
		cancelProgressDialog();
		try {
			if (result.getInt("status") == -1) {
				Toast.makeText(mContext, getResources().getString(R.string.error_wrong_nick_or_password), Toast.LENGTH_SHORT).show();
				return;
			}
		} catch (Exception e) {
			Toast.makeText(mContext, getResources().getString(R.string.error_some_error) + ": " + e.toString(), Toast.LENGTH_SHORT).show();
		}
		try {
			loginSuccess(result.getJSONObject("data").getJSONObject("user"));
		} catch (Exception e) {
			Toast.makeText(mContext, getResources().getString(R.string.error_some_error), Toast.LENGTH_SHORT).show();
		}
	}

	private void createProgressDialog() {
		if (progressDialog != null)
			progressDialog = null;
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(getResources().getString(R.string.dialog_loading) + "...");
		progressDialog.show();
	}
	
	private void cancelProgressDialog() {
		progressDialog.cancel();
	}
	
	private void loginSuccess(JSONObject user) {
		try {
			sPrefs.edit()
			.putString("user_nick", user.getString("nick"))
			.putString("user_token", user.getString("token"))
			.putString("user_avatar", user.getString("avatar"))
			.putInt("user_id", user.getInt("id"))
			.apply();

			Toast.makeText(mContext, getResources().getString(R.string.message_login_success), Toast.LENGTH_SHORT).show();
			Utils.addServiceToAlarm(mContext);
			startSettingsActivity();
		} catch (JSONException e) {
			Toast.makeText(mContext, getResources().getString(R.string.error_some_error), Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private void firstStart() {
		sPrefs.edit().putBoolean("first_start", true)
		.putBoolean("mail", true)
		.putBoolean("discussions", true)
		.putBoolean("notification", true)
		.putBoolean("tape", true)
		.putBoolean("friends", true)
		.putBoolean("guests", true)
		.putBoolean("sound", true)
		.putBoolean("vibration", true)
		.putString("refresh", "5")
		.putString("offline_time", "30")
		.apply();
	}

	private void startSettingsActivity() {
		startActivity(new Intent(this, SettingsActivity.class));
		finish();
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		  super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		 return mUserCheck;
	}

}