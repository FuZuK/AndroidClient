package ua.killer.milliynet.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.os.AsyncTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ua.killer.milliynet.listeners.OnServerConnectListener;

public class ServerConnector {
	private Map<Object, Object> data = new HashMap<Object, Object>();
	private OnServerConnectListener listener;
	
	private Connecting connecting;
	
	public String url;
	
	public ServerConnector(Map<Object, Object> data, OnServerConnectListener _listener) {
		this.data = data;
		this.listener = _listener;
	}
	
	public void send(String _url) {
		url = _url;
		connecting = new Connecting();
		connecting.execute();
	}
	
	public void setOnServerConnectListener(OnServerConnectListener _listener) {
		this.listener = _listener;
	}
	
	private class Connecting extends AsyncTask<Void, Void, Void> {
		private String responseText;
		
		@Override
		protected void onPreExecute() {
			listener.onStartConnecting();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			OkHttpClient client = new OkHttpClient();
			FormBody.Builder body = new FormBody.Builder();

			for (Entry<Object, Object> entry : data.entrySet()) {
				body.add(entry.getKey().toString(), entry.getValue().toString());
			}

			Request req = new Request.Builder()
					.url(url)
					.post(body.build())
					.build();

			try {
				Response res = client.newCall(req).execute();
				responseText = res.body().string().trim();
			} catch (Exception e) {
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			listener.onFinishConnecting(responseText);
		}
	}

}
