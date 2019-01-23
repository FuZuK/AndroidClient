package ua.killer.mobileapp.configs;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ua.killer.mobileapp.R;

public class Settings {
    private static Properties properties;

    public static void load(Context context) {
        Resources resources = context.getResources();

        InputStream rawResource = resources.openRawResource(R.raw.config);
        properties = new Properties();

        try {
            properties.load(rawResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getConfigValue(String name) {
        return properties != null
                ? properties.getProperty(name)
                : null;
    }
}
