package vipjokerstudio.com.telegramchart.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;


public class RenderTextureView extends TextureView implements TextureView.SurfaceTextureListener {


    private Renderer renderer;
    private DrawingThread drawingThread;

    public RenderTextureView(Context context) {
        super(context);
        init();
    }


    public RenderTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RenderTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSurfaceTextureListener(this);

    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        drawingThread = new DrawingThread(renderer, this);
        drawingThread.setRunning(true);
        drawingThread.start();

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        renderer.onSizeChanged(width, height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        renderer.onSizeChanged(w, h);
//        Director.getInstance().setScreenSize(w,h);
//        if(!isInited){
//            isInited = true;
//
//            Director.getInstance().runWithScene(new StartScene());
//        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        boolean retry = true;

        drawingThread.setRunning(false);

        while (retry) {
            try {
                drawingThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                renderer.onTouchStart(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                renderer.onTouchMove(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                renderer.onTouchEnd(event.getX(),event.getY());
                break;

        }
        return true;
    }
}
