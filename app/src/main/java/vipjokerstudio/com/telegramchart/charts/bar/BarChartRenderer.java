package vipjokerstudio.com.telegramchart.charts.bar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;

import vipjokerstudio.com.telegramchart.model.ChartData;
import vipjokerstudio.com.telegramchart.LineData;
import vipjokerstudio.com.telegramchart.model.DrawData;
import vipjokerstudio.com.telegramchart.render.Renderer;
import vipjokerstudio.com.telegramchart.util.DateUtil;
import vipjokerstudio.com.telegramchart.util.FormatUtil;
import vipjokerstudio.com.telegramchart.util.LongFunction;
import vipjokerstudio.com.telegramchart.util.MathUtil;
import vipjokerstudio.com.telegramchart.util.UiUtil;

public class BarChartRenderer implements Renderer {

    boolean isTouched;
    private float width;
    private float height;
    float lastX;
    private int selectedColor = Color.parseColor("#2E84EB");

    private TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint graphPaintY0 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rectStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectWindow = new RectF();
    private RectF barRect = new RectF();
    private float bottomOffset = UiUtil.dpToPx(30);
    private final PointF lastPoint = new PointF();
    private Path arrowRightPath = new Path();
    private RectF arrowRect = new RectF(0,0,30,30);
    private final int textSize = 40;
    private final int windowRadius = 10;

    private ChartData chartData;

    private DrawData drawData;
    private int mainColor;
    private int secondaryColor;
    private int textColor;

    public BarChartRenderer(DrawData drawData, ChartData chartData) {
        this.drawData = drawData;
        this.chartData = chartData;
        init();
    }

    @Override
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public void setMainColor(int color) {
        this.mainColor = color;
    }

    @Override
    public void setSecondaryColor(int color) {
        this.secondaryColor = color;
    }

    @Override
    public void onSizeChanged(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void onTouchStart(float x, float y) {
        isTouched = true;
        lastX = x;
        lastPoint.set(x,y);
        drawData.setShowInfo(true);
    }

    @Override
    public void onTouchMove(float x, float y) {
        isTouched = true;
        lastX = x;
        lastPoint.set(x,y);
    }

    @Override
    public void onTouchEnd(float x, float y) {
//        isTouched = false;
        lastPoint.set(x,y);
    }

    @Override
    public void initShadowPaint(View view) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE,rectPaint);
    }

    private void init() {
        gridPaint.setColor(Color.GRAY);

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);

        graphPaintY0.setStyle(Paint.Style.FILL_AND_STROKE);
        graphPaintY0.setStrokeWidth(1);


        rectStroke.setStyle(Paint.Style.STROKE);
        rectStroke.setColor(Color.GRAY);



        rectPaint.setColor(Color.WHITE);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(secondaryColor);

        int black = Color.argb(100,0,0,0);
        rectPaint.setShadowLayer(3,0,0,black);

        // Set up a default TextPa



        // Set up a default TextPaint object
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(30);

        mTextPaint.setColor(Color.parseColor("#cccccc"));

    }

    @Override
    public void update(long elapsedTimeMs) {

        final long maxX = chartData.getMaxX(0);
        final long minX = chartData.getMinX(0);
        final long maxY = chartData.getMaxY(drawData.getMinScale(), drawData.getMaxScale());
        drawData.setMaxX(maxX);
        drawData.setMinX(minX);
        drawData.setMaxY(maxY);

        for (int i = 0; i < chartData.size(); i++) {
            final long x = chartData.getX(i);
            final float scaledX = getScaledX(x, minX, maxX);
            if (scaledX > lastX) {
                drawData.setCursorX(scaledX);
                drawData.setCursorIndex(i);
                break;
            }
        }


        for (int i = 0; i < chartData.getyData().length; i++) {
            LineData lineData = chartData.getyData()[i];

            drawData.buffer[i] = height - getScaledY(drawData.getMaxY(), lineData.getData()[drawData.getCursorIndex()]);
        }
        drawData.setCursorYpoints(drawData.buffer);


    }

    public void draw(Canvas canvas, long time) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        final long maxX = drawData.getMaxX();
        final long minX = drawData.getMinX();
        final long maxY = drawData.getMaxY();




        for (int i = 0; i < chartData.getyData().length; i++) {
            final LineData lineData = chartData.getyData()[i];

            if (lineData.isActive()) {

                drawChartY0(canvas, minX, maxX, maxY, lineData);
            }

        }
        drawGrid(maxY, canvas);


        drawDates(canvas, minX, maxX);

        if (drawData.isShowInfo()) {

            drawCursor(canvas, lastX, minX, maxX);
        }

    }

    private void drawDates(Canvas canvas, long minX, long maxX) {
        int minimalOffset = 15;
        int skipDatesCount = 0;

        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAlpha(150);
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


            canvas.drawText(text, mappedX, height - bottomOffset / 2, mTextPaint);

        }
        mTextPaint.setAlpha(255);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }


    private void drawChartY0(Canvas canvas, long minX, long maxX, long maxY, LineData lineData) {
        if (chartData != null) {

            float startPointX = 0;
            float startPointY = 0;
            final int[] data = lineData.getData();

            for (int i = 0; i < chartData.size(); i++) {


                final long x = chartData.getX(i);
                final long y = data[i];

                final float graphX = getScaledX(x, minX, maxX);
                final float graphY = getScaledY(maxY, y);


                if (i != 0) {


                    barRect.set(startPointX, height - bottomOffset, graphX, height - graphY);


                    if(drawData.isShowInfo() &&  barRect.left < lastPoint.x && barRect.right > lastPoint.x){
                        graphPaintY0.setColor(selectedColor);
                    }else{
                        graphPaintY0.setColor(lineData.getColor());
                    }


                    canvas.drawRect(barRect, graphPaintY0);
                }
                startPointX = graphX;
                startPointY = graphY;
            }
        }
    }

    private float getScaledY(long maxY, long y) {
        return MathUtil.map(y, 0, maxY, bottomOffset, height - bottomOffset);
    }

    private float getScaledX(long current, long min, long max) {
        float scalledWidth = width * (1 / (drawData.getMaxScale() - drawData.getMinScale()));
        final float graphX = MathUtil.map(current, min, max, 0, scalledWidth) - (scalledWidth * drawData.getMinScale());
        return graphX;
    }

    private float getScaledXforDates(long current, long min, long max) {
        float scalledWidth = width * (1 / (drawData.getMaxScale() - drawData.getMinScale()));
        final float graphX = MathUtil.map(current, min, max, 0, scalledWidth);
        return graphX;
    }






    private void drawCursor(Canvas canvas, float lastX, long minX, long maxX) {

        int maxIndex = 0;
        float padding = 20;
        float lineHeight = 1.5f;
        int lines = 1;

        final float rectWidth = width * (2f / 6f);
        for (int i = 0; i < chartData.size(); i++) {
            final long x = chartData.getX(i);
            final float scaledX = getScaledX(x, drawData.getMinX(), drawData.getMaxX());
            if(scaledX >lastX){
                maxIndex = i;
                break;
            }
        }
        final long date = chartData.getX(maxIndex);

        final String s = DateUtil.formatDateWithDayOfWeek(date);

        final float height = canvas.getHeight() - bottomOffset;

        final float cursorX = drawData.getCursorX();
        final float[] cursorYpoints = drawData.getCursorYpoints();



        rectPaint.setColor(secondaryColor);


        canvas.save();
        if(cursorX + rectWidth > width){
            canvas.translate(cursorX - (rectWidth + 20), 0);
        }else{

            canvas.translate(20 + cursorX, 0);
        }


        for(LineData lineData :chartData.getyData()){
            if(lineData.isActive()){
                lines++;
            }
        }



        rectWindow.set(0,0, rectWidth,(lines * textSize *lineHeight + padding));
        rectWindow.offset(0,10);


        canvas.drawRoundRect(rectWindow, windowRadius, windowRadius, rectPaint);



        arrowRightPath.reset();
        arrowRightPath.moveTo(rectWindow.right - arrowRect.width() -padding,padding + padding/2);
        arrowRightPath.rLineTo(arrowRect.width()/2,arrowRect.width()/2);
        arrowRightPath.rLineTo(-arrowRect.width()/2,arrowRect.width()/2);
        gridPaint.setStrokeWidth(5);
        canvas.drawPath(arrowRightPath,gridPaint);
        gridPaint.setStrokeWidth(1);

        mTextPaint.setColor(textColor);

        float startTextY = textSize + padding ;
        mTextPaint.setFakeBoldText(true);
        canvas.drawText(s, padding, startTextY, mTextPaint);
        mTextPaint.setFakeBoldText(false);
        for (LineData lineData : chartData.getyData()) {
            if(lineData.isActive()) {
                startTextY += textSize*lineHeight;
                final String name = lineData.getName();

                final String value = String.valueOf(lineData.getData()[maxIndex]);

                float valueWidth = mTextPaint.measureText(value);
                mTextPaint.setFakeBoldText(true);
                mTextPaint.setColor(lineData.getColor());
                canvas.drawText(value, rectWindow.right - valueWidth - padding, startTextY, mTextPaint);
                mTextPaint.setFakeBoldText(false);
                mTextPaint.setColor(textColor);
                canvas.drawText(name, padding, startTextY, mTextPaint);

            }
        }

        canvas.restore();

    }

    private void drawGrid(long max, Canvas canvas) {
        if (max == 0) return;
        if(chartData.getActiveCount() == 0)return;
        final float height = canvas.getHeight();//- getPaddingBottom() - getPaddingTop();
        int rank = (int) Math.log10(max);

        int highestRankNum = (int) Math.pow(10, rank - 1);
        float fifthOfMax = max / 5.0f;
        float fraction;
        if (highestRankNum == 0) {
            fraction = 0;
        } else {
            fraction = fifthOfMax % highestRankNum;
        }


        long size = (long) (fifthOfMax - fraction);
        mTextPaint.setColor(textColor);
        mTextPaint.setAlpha(150);
        mTextPaint.setTextSize(30);
        graphPaintY0.setAlpha(150);
        if(size == 0 ){
            size = 25;
            max = 100;
        }
        for (int i = 0; i <= max; i += size) {

            float mappedY = getScaledY(max, i);
            mappedY = height - mappedY;
            canvas.drawText(FormatUtil.formatNumber(i) , 10, mappedY - 5, mTextPaint);
            canvas.drawLine(/*getPaddingLeft()*/ 0, mappedY, canvas.getWidth() /*- getPaddingRight()*/, mappedY, gridPaint);
        }
        mTextPaint.setAlpha(255);
        graphPaintY0.setAlpha(255);
    }

}
