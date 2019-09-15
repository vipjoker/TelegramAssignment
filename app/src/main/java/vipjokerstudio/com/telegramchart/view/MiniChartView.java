package vipjokerstudio.com.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import vipjokerstudio.com.telegramchart.model.ChartData;
import vipjokerstudio.com.telegramchart.LineData;
import vipjokerstudio.com.telegramchart.util.LongFunction;
import vipjokerstudio.com.telegramchart.util.MathUtil;


public class MiniChartView extends View {
    private Paint cursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint nonActivePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint notShowedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF leftRect = new RectF();
    private RectF rightRect = new RectF();
    private RectF areaRect = new RectF();
    private RectF selectedRect;
    private PointF lastPoint = new PointF();
    private RangeListener listener;
    private ChartData data;
    private final int CURSOR_WIDTH = 20;
    private float min = 0;
    private float max  = 0.3f;
    public MiniChartView(Context context) {
        super(context);
        init(null, 0);
    }

    public MiniChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MiniChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    private void init(AttributeSet attrs, int defStyle) {

        graphPaint.setStyle(Paint.Style.STROKE);
        graphPaint.setColor(Color.GREEN);
        cursorPaint.setStyle(Paint.Style.FILL);
        cursorPaint.setColor(Color.BLACK);
        cursorPaint.setAlpha(30);

        nonActivePaint.setStyle(Paint.Style.FILL);
        nonActivePaint.setColor(Color.BLACK);
        nonActivePaint.setAlpha(0);

        notShowedPaint.setStyle(Paint.Style.FILL);
        notShowedPaint.setColor(Color.BLACK);
        notShowedPaint.setAlpha(10);
    }


    public void setData(ChartData data) {
        this.data = data;
        invalidate();
    }

    public void setRangeListener(RangeListener rangeListener) {
        this.listener = rangeListener;
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        leftRect.set(w * min, 0, w * min + CURSOR_WIDTH, h);
        rightRect.set( w * max - CURSOR_WIDTH, 0, w * max, h);
        recountAreaRect();

    }

    public void setInitScales(float min,float max){
        this.min = min;
        this.max = max;
    }

    private void recountAreaRect(){
        areaRect.set(leftRect.right,leftRect.top,rightRect.left,rightRect.bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final long maxX = data.getMaxX(0);
        final long minX = data.getMinX(0);

        final long maxY = data.getMaxY();

        for (int i = 0; i <  data.getyData().length ; i++) {
            final int chartPos = i;
            final LineData lineData = data.getyData()[i];
            if(lineData.isActive()) {
                drawChartLine(canvas, minX, maxX, maxY, pos -> data.getY(chartPos, pos), data.getyData()[chartPos].getColor());
            }
        }

        canvas.drawRect(leftRect, cursorPaint);
        canvas.drawRect(rightRect, cursorPaint);
        canvas.drawRect(leftRect.right,0,rightRect.left,5,cursorPaint);
        canvas.drawRect(0,0,leftRect.left,leftRect.bottom,notShowedPaint);
        canvas.drawRect(rightRect.right,0,getWidth(),rightRect.bottom,notShowedPaint);

//        canvas.drawRect(areaRect,nonActivePaint);


    }
    private void drawChartLine(Canvas canvas, long minX, long maxX, long maxY, LongFunction yFunc, int color) {
        graphPaint.setColor(color);
        final int width = getWidth();
        final int height = getHeight();
        if (data != null) {

            float startPointX = 0;
            float startPointY = 0;
            for (int i = 0; i < data.size(); i++) {


                final long x = data.getX(i);
                final long y = yFunc.get(i);
                final float graphX = MathUtil.map(x, minX, maxX, 0, width);
                final float graphY = MathUtil.map(y, 0, maxY, 0, height);


                if(i != 0) {
                    canvas.drawLine(startPointX, height - startPointY, graphX, height - graphY, graphPaint);
                }
                startPointX = graphX;
                startPointY = graphY;
            }
        }
    }

    @Override

    public boolean onTouchEvent(MotionEvent event) {


        float dx = event.getX() - lastPoint.x;
        //  float dy = lastPoint.y - event.getY();
        switch (event.getAction()) {


            case MotionEvent.ACTION_DOWN:
                if (rightRect.contains(event.getX(), event.getY())) {
                    selectedRect = rightRect;

                } else if (leftRect.contains(event.getX(), event.getY())) {
                    selectedRect = leftRect;
                }else if(areaRect.contains(event.getX(),event.getY())) {
                    selectedRect = areaRect;

                }else {
                    selectedRect = null;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (selectedRect != null) {
                    if(selectedRect == areaRect){

                            leftRect.offset(dx, 0);
                            rightRect.offset(dx, 0);

                    }else {
                        selectedRect.offset(dx, 0);
                    }

                    constraintRect();
                    recountAreaRect();

                    if (listener != null) {
                        listener.onRangeChanged(getScaleMin(), getScaleMax());
                    }
                }
                break;

        }
        lastPoint.set(event.getX(), event.getY());
        invalidate();
        return true;
    }

    public float getScaleMax() {
        return rightRect.right / getWidth();
    }

    public float getScaleMin() {
        return leftRect.left / getWidth();
    }


    private void constraintRect() {
            if(selectedRect == leftRect || selectedRect == areaRect) {
                if (leftRect.left < 0) {
                    leftRect.left = 0;
                    leftRect.right = CURSOR_WIDTH;


                    rightRect.left = leftRect.right + areaRect.width();
                    rightRect.right = rightRect.left + CURSOR_WIDTH;
                }

                if (leftRect.right > rightRect.left) {
                    leftRect.right = rightRect.left;
                    leftRect.left = rightRect.left - CURSOR_WIDTH;
                }
            }
            if(selectedRect == rightRect || selectedRect == areaRect){
                if(rightRect.right > getWidth()){
                    rightRect.right = getWidth();
                    rightRect.left = getWidth() - CURSOR_WIDTH;
                    leftRect.right = rightRect.left - areaRect.width();
                    leftRect.left =  leftRect.right - CURSOR_WIDTH;

                }

                if(rightRect.left < leftRect.right){
                    rightRect.left = leftRect.right;
                    rightRect.right = leftRect.right + CURSOR_WIDTH;
                }
            }
    }

    public interface RangeListener {
        void onRangeChanged(float min, float max);
    }

}
