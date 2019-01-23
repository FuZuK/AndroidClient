package ua.killer.milliynet;

import ru.elifantiev.android.roboerrorreporter.RoboErrorReporter;
import ua.killer.milliynet.configs.Settings;

import android.app.Application;
import android.util.Log;

import java.io.IOException;

public class MyApplication extends Application {

	@Override
    public void onCreate() {
		RoboErrorReporter.bindReporter(this);
		Settings.load(this.getApplicationContext());

		super.onCreate();
    }
}
