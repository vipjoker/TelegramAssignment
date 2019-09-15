package vipjokerstudio.com.telegramchart.util;

import android.content.res.Resources;

public class UiUtil {
    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
