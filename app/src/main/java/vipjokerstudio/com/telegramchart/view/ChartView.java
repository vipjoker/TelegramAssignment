package vipjokerstudio.com.telegramchart.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import vipjokerstudio.com.telegramchart.model.ChartData;
import vipjokerstudio.com.telegramchart.LineData;
import vipjokerstudio.com.telegramchart.util.DateUtil;
import vipjokerstudio.com.telegramchart.util.LongFunction;
import vipjokerstudio.com.telegramchart.util.MathUtil;

/**
 * TODO: document your custom view class.
 */
public class ChartView extends View implements Animator.AnimatorListener {
    boolean isTouched = false;
    float lastX;
    private TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint graphPaintY0 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rectStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectWindow = new RectF();

    private float bottomOffset = 100;
    private ChartData chartData;
    private float startScale;
    private float endScale = 1;
    private long animValue;
    private boolean isAnimated;
    private ValueAnimator valueAnimator;


    public ChartView(Context context) {
        super(context);
        init(null, 0);
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        gridPaint.setColor(Color.GRAY);

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);

        graphPaintY0.setStyle(Paint.Style.STROKE);
        graphPaintY0.setStrokeWidth(6);




        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(Color.WHITE);
        rectStroke.setStyle(Paint.Style.STROKE);
        rectStroke.setColor(Color.GRAY);



        // Set up a default TextPaint object
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(30);

        mTextPaint.setColor(Color.parseColor("#cccccc"));

    }

    public void setChartData(ChartData chartData) {
        this.chartData = chartData;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        final long maxX = chartData.getMaxX(0);
        final long minX = chartData.getMinX(0);
        final long maxY;
        if (isAnimated) {
            maxY = animValue;
        } else {
            maxY = getMaxY();
        }


        drawGrid(maxY, canvas);
        for (int i = 0; i < chartData.getyData().length; i++) {
            final LineData lineData = chartData.getyData()[i];
            if(lineData.isActive()) {
                final int chartPos = i;
                drawChartY0(canvas, minX, maxX, maxY, pos -> chartData.getY(chartPos, pos), chartData.getyData()[chartPos].getColor());
            }
        }


        drawDates(canvas, minX, maxX);

        if (isTouched) {
            drawCursor(canvas, lastX,minX,maxX);
        }
    }

    private void drawDates(Canvas canvas, long minX, long maxX) {
        int minimalOffset = 15;
        int skipDatesCount = 0;
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        for (; skipDatesCount < chartData.size(); skipDatesCount++) {
            final long x = chartData.getX(skipDatesCount);
            final float mappedX = getScaledXforDates(x, minX, maxX);
            final String text = DateUtil.formatDate(x);
            final float measureText = mTextPaint.measureText(text);


            if (measureText + minimalOffset < mappedX) {

                break;
            }
        }


        for (int i = 0; i < chartData.size(); i += skipDatesCount) {
            final long x = chartData.getX(i);
            final float mappedX = getScaledX(x, minX, maxX);
            final String text = DateUtil.formatDate(x);


            canvas.drawText(text, mappedX, getHeight() - bottomOffset / 2, mTextPaint);

        }

        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    private long getMaxY() {
        final int startIndex = chartData.getIndexByFraction(startScale);
        final int endIndex = chartData.getIndexByFraction(endScale);
        return chartData.getMaxY(startIndex, endIndex);
    }

    private void drawChartY0(Canvas canvas, long minX, long maxX, long maxY, LongFunction yFunc, int color) {
        graphPaintY0.setColor(color);
        final int width = getWidth();
        final int height = getHeight();
        if (chartData != null) {

            float startPointX = 0;
            float startPointY = 0;


            for (int i = 0; i < chartData.size(); i++) {


                final long x = chartData.getX(i);
                final long y = yFunc.get(i);

                final float graphX = getScaledX(x, minX, maxX);
                final float graphY = getScaledY(maxY, y);


                if (i != 0) {
                    canvas.drawLine(startPointX, height - startPointY, graphX, height - graphY, graphPaintY0);
                }
                startPointX = graphX;
                startPointY = graphY;
            }
        }
    }

    private float getScaledY(long maxY, long y) {
        return MathUtil.map(y, 0, maxY, bottomOffset, getHeight());
    }

    private float getScaledX(long current, long min, long max) {
        float scalledWidth = getWidth() * (1 / (endScale - startScale));
        final float graphX = MathUtil.map(current, min, max, 0, scalledWidth) - (scalledWidth * startScale);
        return graphX;
    }

    private float getScaledXforDates(long current, long min, long max) {
        float scalledWidth = getWidth() * (1 / (endScale - startScale));
        final float graphX = MathUtil.map(current, min, max, 0, scalledWidth);
        return graphX;
    }

    private void animate(int oldValue, int newValue) {
        if (isAnimated) {
            valueAnimator.cancel();
        }

        valueAnimator = ValueAnimator.ofInt(oldValue, newValue);
        valueAnimator.addUpdateListener(this::onFrameUpdated);
        valueAnimator.addListener(this);
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    private void onFrameUpdated(ValueAnimator valueAnimator) {
        animValue = (int) valueAnimator.getAnimatedValue();
        invalidate();
    }

    public void setChartScale(float start, float end) {
        this.startScale = start;
        this.endScale = end;
        isTouched  = false;
        if (animValue == 0) {
            animValue = getMaxY();
        }
        animate((int) animValue, (int) getMaxY());
    }

    public void updateChart(){

        if (animValue == 0) {
            animValue = getMaxY();
        }
        animate((int) animValue, (int) getMaxY());
    }



    private void drawCursor(Canvas canvas, float lastX,long minX,long maxX) {

        int minIndex= 0;
        int maxIndex= 0;
        float xCoord = 0;
        for (int i = 0; i < chartData.size(); i++) {
            final long x = chartData.getX(i);
            final float scaledX = getScaledX(x, minX, maxX);
            if(scaledX >lastX){
                maxIndex = i;
                xCoord = scaledX;
                break;
            }
        }


        final long date = chartData.getX(maxIndex);

        final String s = DateUtil.formatDateWithDayOfWeek(date);


        final float height = canvas.getHeight() - bottomOffset;






        float w = getWidth()/4;
        float h = getHeight()/5;
        canvas.drawLine(xCoord, getPaddingTop(), xCoord, height, gridPaint);
        rectWindow.set(-50,0,w,h);
        for (LineData lineData : chartData.getyData()) {
            final float yCoord = getHeight() -  getScaledY(getMaxY(),lineData.getData()[maxIndex]);
            final int color = lineData.getColor();

            canvas.drawCircle(xCoord,yCoord,10,rectPaint);
            graphPaintY0.setColor(color);
            canvas.drawCircle(xCoord,yCoord,10,graphPaintY0);
        }


        canvas.save();
        canvas.translate(50+ xCoord,0);

        float startTextX = 10;
        for (LineData lineData : chartData.getyData()) {
            final String name = lineData.getName();
            mTextPaint.setColor(lineData.getColor());

            final String value = String.valueOf(lineData.getData()[maxIndex]);



            mTextPaint.setTextSize(50);

            float valueWidth = mTextPaint.measureText(value);
            mTextPaint.setTextSize(40);

            float nameWidth = mTextPaint.measureText(name);



            startTextX += Math.max(valueWidth,nameWidth);
            startTextX+=20;
        }
        rectWindow.set(-20,0,startTextX + 20,h);
        canvas.drawRoundRect(rectWindow,5,5,rectPaint);

        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(s,rectWindow.centerX(),rectWindow.height()/4,mTextPaint);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        startTextX = 10;

        for (LineData lineData : chartData.getyData()) {
            final String name = lineData.getName();
            mTextPaint.setColor(lineData.getColor());

            final String value = String.valueOf(lineData.getData()[maxIndex]);
            mTextPaint.setTextSize(50);
            canvas.drawText(value,startTextX,h/2 + 10,mTextPaint);
            float valueWidth = mTextPaint.measureText(value);
            mTextPaint.setTextSize(40);
            canvas.drawText(name,startTextX, h/2 + 70,mTextPaint);
            float nameWidth = mTextPaint.measureText(name);

            startTextX += Math.max(valueWidth,nameWidth );
            startTextX+=20;
        }

        canvas.drawRoundRect(rectWindow,5,5,rectStroke);
        canvas.restore();

    }

    private void drawGrid(long max, Canvas canvas) {

        final float height = canvas.getHeight() - getPaddingBottom() - getPaddingTop();
         int rank = (int)Math.log10(max);

         int highestRankNum =(int) Math.pow(10,rank - 1);
         long fifthOfMax = max/5;
         long fraction;
        if(highestRankNum == 0){
            fraction = 0;
        }else{
            fraction = fifthOfMax%highestRankNum;
        }



        long size = fifthOfMax - fraction;
        mTextPaint.setColor(Color.GRAY);

        mTextPaint.setTextSize(30);
        for (int i = 0; i <= max; i += size) {

            float mappedY = getScaledY(max,  i);
            mappedY = height - mappedY;
            canvas.drawText(i+ "", 10, mappedY - 5, mTextPaint);
            canvas.drawLine(getPaddingLeft(), mappedY, canvas.getWidth() - getPaddingRight(), mappedY, gridPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isTouched = true;
        lastX = event.getX();
        invalidate();
        return true;

    }


    @Override
    public void onAnimationStart(Animator animation) {
        isAnimated = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        isAnimated = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        isAnimated = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        isAnimated = true;
    }

}
