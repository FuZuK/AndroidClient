package ua.killer.milliynet.cache;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ua.killer.milliynet.BitmapHttpLoader;
import ua.killer.milliynet.listeners.BitmapHttpLoaderListener;

public class BitmapHttpLoaderCache extends BitmapHttpLoader implements BitmapHttpLoaderListener {
	private BitmapHttpLoaderListener listener;
	private String action;
	private Cache cache;
	
	public BitmapHttpLoaderCache(String _action, String _url, Context _mContext) {
		super(_url, _mContext);
		action = _action;
		_init();
	}
	
	public BitmapHttpLoaderCache(String _action, String _url, Context _mContext, BitmapHttpLoaderListener _listener) {
		super(_url, _mContext);
		action = _action;
		this.listener = _listener;
		_init();
	}
	
	private void _init() {
		this.setOnLoadListener(this);
		cache = new Cache(action, mContext);
	}

	@Override
	public void onLoadStart() {
		listener.onLoadStart();
	}

	@Override
	public void onLoadFinish(Bitmap bitmap) throws IOException {
		cache.write(bitmap);
		listener.onLoadFinish(bitmap);
	}
	
	@Override
	public void load() throws IOException {
		if (cache.exists())
			listener.onLoadFinish(BitmapFactory.decodeStream(cache.get()));
		else {
			super.load();
		}
	}

}
