package vipjokerstudio.com.telegramchart;

import android.app.Application;

import vipjokerstudio.com.telegramchart.util.Preferences;

public class TelegramChart extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Preferences.init(this);
    }
}
