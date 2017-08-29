package ua.killer.milliynet;

import ru.elifantiev.android.roboerrorreporter.RoboErrorReporter;
import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

	@Override
    public void onCreate() {
		RoboErrorReporter.bindReporter(this);

		super.onCreate();
    }
}
