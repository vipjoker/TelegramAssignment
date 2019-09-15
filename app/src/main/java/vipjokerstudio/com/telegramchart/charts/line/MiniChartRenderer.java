package vipjokerstudio.com.telegramchart.charts.line;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.view.View;

import vipjokerstudio.com.telegramchart.LineData;
import vipjokerstudio.com.telegramchart.model.ChartData;
import vipjokerstudio.com.telegramchart.model.DrawData;
import vipjokerstudio.com.telegramchart.render.Renderer;
import vipjokerstudio.com.telegramchart.util.MathUtil;

public class MiniChartRenderer implements Renderer {


    private final int radius = 20;
    private float[] leftRadiuses = {radius, radius, 0, 0, 0, 0, radius, radius};
    private float[] rightRadiuses = {0, 0, radius, radius, radius, radius, 0, 0};
    private Path leftPath = new Path();
    private Path rightPath = new Path();
    private RectF smallRect = new RectF();
    private float smallRectHeight = 30;
    private float smallRectWidth = 5;

    private Paint cursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint notShowedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF leftRect = new RectF();

    private RectF rightRect = new RectF();
    private RectF areaRect = new RectF();
    private RectF selectedRect;
    private PointF lastPoint = new PointF();
    private ChartData data;
    private final int CURSOR_WIDTH = 40;
    private float height;
    private float width;
    private Path clipPath = new Path();
    private RectF clipRect = new RectF();

    private final DrawData scaleData;
    private int mainColor;
    private int secondaryColor;
    private int textColor;

    public MiniChartRenderer(DrawData scaleData,ChartData chartData) {
        this.scaleData = scaleData;
        this.data =chartData;
        graphPaint.setStyle(Paint.Style.STROKE);
        graphPaint.setColor(Color.GREEN);
        cursorPaint.setStyle(Paint.Style.FILL);
        cursorPaint.setColor(Color.GRAY);
        cursorPaint.setAlpha(50);


        notShowedPaint.setStyle(Paint.Style.FILL);


        notShowedPaint.setColor(Color.parseColor("#86A9C4"));
        notShowedPaint.setAlpha(50);
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setColor(Color.WHITE);
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


    private void recountAreaRect() {
        areaRect.set(leftRect.right, leftRect.top, rightRect.left, rightRect.bottom);
    }

    @Override
    public void update(long elapsedTimeMs) {

    }

    @Override
    public void initShadowPaint(View view) {

    }

    public void draw(Canvas canvas, long time) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.save();
        clipRect.set(0, 0, width, height);
        clipPath.addRoundRect(clipRect, radius, radius, Path.Direction.CCW);
        canvas.clipPath(clipPath);



        final long maxX = data.getMaxX(0);
        final long minX = data.getMinX(0);

        final long maxY = data.getMaxY();

        for (int i = 0; i < data.getyData().length; i++) {

            final LineData lineData = data.getyData()[i];
            if (lineData.isActive()) {
                drawChartLine(canvas, minX, maxX, maxY, lineData);
            }
        }

        leftPath.reset();
        rightPath.reset();
        leftPath.addRoundRect(leftRect, leftRadiuses, Path.Direction.CCW);
        rightPath.addRoundRect(rightRect, rightRadiuses, Path.Direction.CCW);

        canvas.drawRect(0, 0, leftRect.right, leftRect.bottom, notShowedPaint);
        canvas.drawRect(rightRect.left, 0, width, rightRect.bottom, notShowedPaint);
        canvas.drawPath(leftPath, cursorPaint);
        canvas.drawPath(rightPath, cursorPaint);

        cursorPaint.setAlpha(100);
        canvas.drawRect(leftRect.right, 0, rightRect.left, 3, cursorPaint);
        canvas.drawRect(leftRect.right, height - 3, rightRect.left, height, cursorPaint);
        cursorPaint.setAlpha(50);
        smallRect.set(leftRect.centerX() - smallRectWidth / 2, leftRect.centerY() - smallRectHeight / 2, leftRect.centerX() + smallRectWidth / 2, leftRect.centerY() + smallRectHeight / 2);
        canvas.drawRoundRect(smallRect, smallRectWidth / 2, smallRectWidth / 2, whitePaint);
        smallRect.set(rightRect.centerX() - smallRectWidth / 2, rightRect.centerY() - smallRectHeight / 2, rightRect.centerX() + smallRectWidth / 2, rightRect.centerY() + smallRectHeight / 2);
        canvas.drawRoundRect(smallRect, smallRectWidth / 2, smallRectWidth / 2, whitePaint);
        canvas.restore();
    }

    private void drawChartLine(Canvas canvas, long minX, long maxX, long maxY, LineData lineData) {
        graphPaint.setColor(lineData.getColor());
        if (data != null) {

            float startPointX = 0;
            float startPointY = 0;
            final int[] data = lineData.getData();
            for (int i = 0; i < this.data.size(); i++) {


                final long x = this.data.getX(i);
                final long y = data[i];
                final float graphX = MathUtil.map(x, minX, maxX, 0, width);
                final float graphY = MathUtil.map(y, 0, maxY, 0, height);


                if (i != 0) {
                    canvas.drawLine(startPointX, height - startPointY, graphX, height - graphY, graphPaint);
                }
                startPointX = graphX;
                startPointY = graphY;
            }
        }
    }

    public float getScaleMax() {
        return rightRect.right / width;
    }

    public float getScaleMin() {
        return leftRect.left / width;
    }


    private void constraintRect() {
        if (selectedRect == leftRect || selectedRect == areaRect) {
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
        if (selectedRect == rightRect || selectedRect == areaRect) {
            if (rightRect.right > width) {
                rightRect.right = width;
                rightRect.left = width - CURSOR_WIDTH;
                leftRect.right = rightRect.left - areaRect.width();
                leftRect.left = leftRect.right - CURSOR_WIDTH;

            }

            if (rightRect.left < leftRect.right) {
                rightRect.left = leftRect.right;
                rightRect.right = leftRect.right + CURSOR_WIDTH;
            }
        }
    }


    @Override
    public void onSizeChanged(float w, float h) {
        this.width = w;
        this.height = h;
        leftRect.set(w * scaleData.getMinScale(), 0, w * scaleData.getMinScale() + CURSOR_WIDTH, h);
        rightRect.set(w * scaleData.getMaxScale() - CURSOR_WIDTH, 0, w * scaleData.getMaxScale(), h);
        recountAreaRect();
    }

    @Override
    public void onTouchStart(float x, float y) {
        float dx = x - lastPoint.x;

        if (rightRect.contains(x, y)) {
            selectedRect = rightRect;

        } else if (leftRect.contains(x, y)) {
            selectedRect = leftRect;
        } else if (areaRect.contains(x, y)) {
            selectedRect = areaRect;

        } else {
            selectedRect = null;
        }
        lastPoint.set(x, y);

    }

    @Override
    public void onTouchMove(float x, float y) {
        float dx = x - lastPoint.x;
        if (selectedRect != null) {
            if (selectedRect == areaRect) {

                leftRect.offset(dx, 0);
                rightRect.offset(dx, 0);

            } else {
                selectedRect.offset(dx, 0);
            }

            constraintRect();
            recountAreaRect();


            scaleData.setMinScale(getScaleMin());
            scaleData.setMaxScale(getScaleMax());
        }
        lastPoint.set(x, y);

    }

    @Override
    public void onTouchEnd(float x, float y) {
        float dx = x - lastPoint.x;
        lastPoint.set(x, y);

    }
}
