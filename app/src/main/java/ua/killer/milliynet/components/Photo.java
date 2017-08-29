package ua.killer.milliynet.components;

import org.json.JSONException;
import org.json.JSONObject;

import ua.killer.milliynet.application.Constants;

public class Photo {
	private boolean exists = false;
	private int id;
	private String extension;
	private int size = 48;
	
	public Photo(JSONObject json) throws JSONException {
		exists = json.getBoolean("exists");
		
		if (exists()) {
			id = json.getInt("id");
			extension = json.getString("extension");
		}
	}
	
	public boolean exists() {
		return exists;
	}
	
	public int getId() {
		return id;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public String getHash() {
		return "PHOTO." + (exists() ? (getSize() + "." + id) : 0);
	}
	
	private int getSize() {
		return size;
	}
	
	public String getUrl() {
		String link = exists() ? "/foto/foto" + getSize() + "/" + getId() + "." + getExtension() : "/style/icons/avatar.png";
		return Constants.SERVER_HOST + link;
	}

	public void setSize(int _size) {
		size = _size;
	}
	
	public static Photo parse(JSONObject json) throws JSONException {
		return new Photo(json);
	}
}
