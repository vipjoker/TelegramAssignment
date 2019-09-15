package vipjokerstudio.com.telegramchart.render;

import android.graphics.Canvas;
import android.util.Log;
import android.view.TextureView;




public class DrawingThread extends Thread {

    private Renderer renderer;
    private TextureView textureView;
    private boolean isRunning = false;
    private long previousTime;
    private final int fps = 60;
    public static final String TAG = DrawingThread.class.getName();
    public DrawingThread(Renderer renderer, TextureView textureView) {
        setName("Thread-TextureView");
        this.renderer = renderer;
        this.textureView = textureView;
        previousTime = System.currentTimeMillis();
    }

    public void setRunning(boolean run) {
        isRunning = run;
    }

    @Override
    public void run() {
        Canvas canvas;

        while (isRunning) {
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMs = currentTimeMillis - previousTime;
            long sleepTimeMs = (long) (1000f / fps - elapsedTimeMs);

            canvas = null;
            try {

                canvas = textureView.lockCanvas();

                if (canvas == null) {
                    Thread.sleep(1);

                    continue;

                } else if (sleepTimeMs > 0) {

                    Thread.sleep(sleepTimeMs);

                }
                renderer.update(elapsedTimeMs);
                renderer.draw(canvas, elapsedTimeMs);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    textureView.unlockCanvasAndPost(canvas);
                    previousTime = System.currentTimeMillis();
                }
            }
        }
    }


}