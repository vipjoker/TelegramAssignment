package vipjokerstudio.com.telegramchart.charts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;

import vipjokerstudio.com.telegramchart.model.ChartData;
import vipjokerstudio.com.telegramchart.LineData;
import vipjokerstudio.com.telegramchart.model.DrawData;
import vipjokerstudio.com.telegramchart.render.Renderer;
import vipjokerstudio.com.telegramchart.util.DateUtil;
import vipjokerstudio.com.telegramchart.util.MathUtil;
import vipjokerstudio.com.telegramchart.util.UiUtil;

public class PercentageStackedAreaChart implements Renderer {

    private DrawData drawData;
    private final ChartData chartData;


    boolean isTouched;
    private float width;
    private float height;
    float lastX;
    private TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint graphPaintY0 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rectStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectWindow = new RectF();
    private RectF circleRect = new RectF();
    private float bottomOffset = UiUtil.dpToPx(30);
    private Path[] paths;
    private boolean isCircleMode = false;
    private final int textSize = 40;
    private final int windowRadius = 10;

    private Path arrowRightPath = new Path();
    private RectF arrowRect = new RectF(0,0,30,30);


    private int mainColor   ;
    private int secondaryColor;
    private int textColor;



    public PercentageStackedAreaChart(DrawData drawData, ChartData chartData) {
        this.drawData = drawData;
        this.chartData = chartData;
        paths = new Path[chartData.getyData().length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = new Path();
        }
        init();
    }

    @Override
    public void setMainColor(int color) {
        this.mainColor = color;
    }

    @Override
    public void setTextColor(int textColor) {
        this.textColor = textColor;
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
    public void initShadowPaint(View view) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, rectPaint);
    }

    @Override
    public void onTouchStart(float x, float y) {
        isTouched = true;
        lastX = x;
        drawData.setShowInfo(true);
    }

    @Override
    public void onTouchMove(float x, float y) {
        isTouched = true;
        lastX = x;
    }

    @Override
    public void onTouchEnd(float x, float y) {
//        isTouched = false;
    }


    private void init() {
        gridPaint.setColor(textColor);
        gridPaint.setAlpha(100);

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);

        graphPaintY0.setStyle(Paint.Style.FILL_AND_STROKE);
        graphPaintY0.setStrokeWidth(6);


        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(secondaryColor);

        int black = Color.argb(100,0,0,0);
        rectPaint.setShadowLayer(3,0,0,black);



        rectStroke.setStyle(Paint.Style.STROKE);
        rectStroke.setColor(Color.GRAY);



        // Set up a default TextPaint object
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(30);

        mTextPaint.setColor(textColor);

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




        drawChartY0(canvas, minX, maxX, maxY, chartData.getyData());

        drawGrid( canvas);

        drawDates(canvas, minX, maxX);




            if(drawData.isShowInfo()) {
                drawCursor(canvas);
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

        mTextPaint.setAlpha(150);
        for (int i = 0; i < chartData.size(); i += skipDatesCount) {
            final long x = chartData.getX(i);
            final float mappedX = getScaledX(x, minX, maxX);
            final String text = DateUtil.formatDate(x);


            canvas.drawText(text, mappedX, height - bottomOffset / 2, mTextPaint);

        }
        mTextPaint.setAlpha(255);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawChartY0(Canvas canvas, long minX, long maxX, long maxY, LineData[] lineDates) {
        if (chartData != null) {
            float startPointY = 0;
            for (Path path : paths) {
                path.reset();
                path.moveTo(0, height-bottomOffset);
            }
            for (int i = 0; i < chartData.size(); i++) {
                final long x = chartData.getX(i);
                final long total = drawData.getMaxPercentageTotal(i);
                final float graphX = getScaledX(x, minX, maxX);
                for (int j = 0; j < lineDates.length; j++) {
                    LineData lineData = lineDates[j];
                    if(!lineData.isActive())continue;
                    final Path path = paths[j];
                    final long y = lineData.getData()[i];


                    final float graphY = MathUtil.map(y, 0, total, 0, 1);


                    path.lineTo(graphX, height - (graphY + startPointY) * height - bottomOffset );
                    startPointY += graphY;

                }

                startPointY = 0;

            }

            for (int i = lineDates.length - 1; i >= 0; i--) {
                final LineData lineDate = lineDates[i];
                final Path path = paths[i];
                path.lineTo(width, height - bottomOffset);
                path.close();

                graphPaintY0.setColor(lineDate.getColor());
                canvas.drawPath(path, graphPaintY0);
            }


            float radius = Math.min(width, height);
            circleRect.set(0, 0, radius, radius);
            circleRect.offsetTo((width - circleRect.width()) / 2, 0);
            float startAngle = 0;
            if (isCircleMode) {
                final int linedatesLength = lineDates.length;
                float fraction = 1.0f / linedatesLength;

                for (int i = 1; i <= linedatesLength; i++) {

                    float result = MathUtil.map(fraction * i, 0, 1, 0, 359);
                    graphPaintY0.setColor(lineDates[i - 1].getColor());
                    canvas.drawArc(circleRect, startAngle, 360 * fraction, true, graphPaintY0);
                    startAngle += 360 * fraction;
                }
            }
        }
    }

    private float getScaledY(long maxY, long y) {
        return MathUtil.map(y, 0, maxY, bottomOffset, height);
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

    private void drawCursor(Canvas canvas) {

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
        int gridColor = gridPaint.getColor();
        gridPaint.setColor(textColor);
        canvas.drawLine(cursorX, 0, cursorX, height, gridPaint);

        gridPaint.setColor(gridColor);
        rectPaint.setColor(mainColor);
//        for (int i = 0;i <  chartData.getyData().length;i++) {
//            LineData lineData = chartData.getyData()[i];
//            final int color = lineData.getColor();
//
//            canvas.drawCircle(cursorX, cursorYpoints[i], 10, rectPaint);
//            graphPaintY0.setColor(color);
//            canvas.drawCircle(cursorX, cursorYpoints[i], 10, graphPaintY0);
//        }
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
        final long total = drawData.getMaxPercentageTotal(maxIndex);

        float startTextY = textSize + padding ;
        mTextPaint.setFakeBoldText(true);
        canvas.drawText(s, padding, startTextY, mTextPaint);
        mTextPaint.setFakeBoldText(false);
        for (LineData lineData : chartData.getyData()) {
            if(lineData.isActive()) {
                startTextY += textSize*lineHeight;

                final int datum = lineData.getData()[maxIndex];
                final String value = String.valueOf(datum);

                final String name = (int)(datum/(total * 1.0f) * 100)+  "% " +lineData.getName();
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

    private void drawGrid( Canvas canvas) {

        int max = 100;
        int size = 25;
        mTextPaint.setAlpha(50);

        mTextPaint.setColor(textColor);
        gridPaint.setColor(textColor);
        mTextPaint.setTextSize(30);
        mTextPaint.setAlpha(100);
        gridPaint.setAlpha(100);
        for (int i = 0; i <= max; i += size) {

            float mappedY = MathUtil.map(i,0,100,0,height - bottomOffset);
            mappedY = height - mappedY - bottomOffset;
            canvas.drawText(i + "", 10, mappedY - 5, mTextPaint);
            canvas.drawLine(/*getPaddingLeft()*/ 0, mappedY, canvas.getWidth() /*- getPaddingRight()*/, mappedY, gridPaint);
        }

        mTextPaint.setAlpha(255);
        gridPaint.setAlpha(255);
    }

}
