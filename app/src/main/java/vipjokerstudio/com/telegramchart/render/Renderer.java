package vipjokerstudio.com.telegramchart.render;

import android.graphics.Canvas;
import android.view.View;

public interface Renderer {

    void update(long elapsedTimeMs);

    void draw(Canvas canvas, long time);

    void onSizeChanged(float width, float height);

    void onTouchStart(float x, float y);

    void onTouchMove(float x, float y);

    void onTouchEnd(float x, float y);

    void setMainColor(int color);

    void setSecondaryColor(int color);

    void setTextColor(int textColor);

    void initShadowPaint(View view);
}
