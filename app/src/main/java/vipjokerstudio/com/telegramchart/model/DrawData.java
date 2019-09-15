package vipjokerstudio.com.telegramchart.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import vipjokerstudio.com.telegramchart.LineData;
import vipjokerstudio.com.telegramchart.util.AnimationTask;
import vipjokerstudio.com.telegramchart.util.Interpolator;
import vipjokerstudio.com.telegramchart.util.ListUtil;
import vipjokerstudio.com.telegramchart.util.MathUtil;
import vipjokerstudio.com.telegramchart.util.Mathf;

public class DrawData {

    private final CopyOnWriteArrayList<AnimationTask> animationTasks = new CopyOnWriteArrayList<>();

    private float minScale;
    private float maxScale;
    private long maxX;
    private long minX;
    private long maxY;
    private long targetMaxY;
    private boolean showInfo;
    AnimationTask maxYAnimation = new AnimationTask(Interpolator.LINEAR, 500);
    AnimationTask cursorAnimation = new AnimationTask(Interpolator.LINEAR, 200);
    AnimationTask percentageTotal = new AnimationTask(Interpolator.LINEAR,500);
    AnimationTask totalYAnimation = new AnimationTask(Interpolator.LINEAR,500);
    AnimationTask totalYAnimationMini = new AnimationTask(Interpolator.LINEAR,500);
    private final List<LineData> lineData = new ArrayList<>();
    private float cursorX;
    private int cursorIndex;

    private float targetCursorX;

    private final float[] cursorYpoints;
    private final float[] targerCursorYpoints;
    public final float[] buffer;
    public final long[] scaledMaxY;
    public final long[] targetScaledMaxY;

    public final long[] miniChartMaxY;
    public long maxTotalMiniChart;
    public long targetMaxTotalMiniChart;


    private long maxTotal;
    private long targetMaxTotal;
    public long total;
    public long targetTotal;

    private long[] maxPercentageTotal;
    private long[] maxPercentageTotalTarget;

    public DrawData(float minScale, float maxScale, int size) {
        this.minScale = minScale;
        this.maxScale = maxScale;
        cursorYpoints = new float[size];
        targerCursorYpoints = new float[size];

        buffer = new float[size];
        scaledMaxY = new long[size];
        targetScaledMaxY = new long[size];
        miniChartMaxY = new long[size];

    }

    public float getMinScale() {
        return minScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {

        this.maxScale = maxScale;
//        if(this.maxScale != maxScale){
//            final float startValue = this.maxScale;
//            animationTasks.add(new AnimationTask(Interpolator.EASEINBACK,1000,v->{
//                this.maxScale = Mathf.lerp(startValue,maxScale,v);
//            })) ;
//        }
    }

    public void update() {
//        final Iterator<AnimationTask> iterator = animationTasks.iterator();
//        while (iterator.hasNext()) {
//            final AnimationTask next = iterator.next();
//            next.update();
//            if (next.isFinished()) {
//                iterator.remove();
//            }
//        }
    }

    public List<LineData> getLineData() {
        return lineData;
    }


    public void setMaxX(long maxX) {
        this.maxX = maxX;
    }

    public void setMinX(long minX) {
        this.minX = minX;
    }

    public long getMaxX() {
        return maxX;
    }

    public long getMinX() {
        return minX;
    }


    public void setMiniChartMaxY(int index, long value) {
        miniChartMaxY[index] = value;
    }

    public long getMiniChartMaxY(int index) {
        return miniChartMaxY[index];
    }

    public void setMaxY(long maxY) {


        if (targetMaxY != maxY) {
            maxYAnimation.start();
        }
        this.targetMaxY = maxY;
    }

    public long getMaxY() {
        final float update = maxYAnimation.update();
        this.maxY = Mathf.lerp(this.maxY, this.targetMaxY, update);
        return maxY;
    }


    public void setMaxY(int index, long maxY) {


        if (targetScaledMaxY[index] != maxY) {
            maxYAnimation.start();
        }
        this.targetScaledMaxY[index] = maxY;
    }

    public long getMaxY(int index) {
        final float update = maxYAnimation.update();
        this.scaledMaxY[index] = Mathf.lerp(this.scaledMaxY[index], this.targetScaledMaxY[index], update);
        return this.scaledMaxY[index];
    }

    public void setCursorX(float cursorX) {
        if (cursorX != this.targetCursorX) {
            cursorAnimation.start();
        }
        this.targetCursorX = cursorX;
    }

    public float getCursorX() {
        this.cursorX = Mathf.lerp(this.cursorX, targetCursorX, cursorAnimation.update());
        return this.cursorX;
    }

    public void setCursorIndex(int cursorIndex) {
        this.cursorIndex = cursorIndex;
    }

    public int getCursorIndex() {
        return cursorIndex;
    }

    public void setCursorYpoints(float[] points) {
//
//        if(!ListUtil.areContentsSame(targerCursorYpoints,points)){
//            cursorAnimation.start();
//        }

        ListUtil.copy(targerCursorYpoints, points);
    }

    public float[] getCursorYpoints() {
        for (int i = 0; i < targerCursorYpoints.length; i++) {
            cursorYpoints[i] = Mathf.lerp(cursorYpoints[i], targerCursorYpoints[i], cursorAnimation.update());
        }
        return cursorYpoints;
    }

    public void setMaxTotal(long totalMax) {
        if (targetTotal != totalMax) {
            totalYAnimation.start();
        }
        this.targetTotal = totalMax;
    }

    public long getMaxTotal() {
        final float update = totalYAnimation.update();
        this.total = Mathf.lerp(total, targetTotal, update);
        return this.total;
    }

    public long getMaxTotalMiniChart(){
        final float update = totalYAnimationMini.update();
        maxTotalMiniChart = Mathf.lerp(maxTotalMiniChart,targetMaxTotalMiniChart,update);
        return maxTotalMiniChart;
    }

    public void setMaxTotalMiniChart(long totalMiniChart){

        if(targetMaxTotalMiniChart != totalMiniChart){
            totalYAnimationMini.start();
        }
        this.targetMaxTotalMiniChart = totalMiniChart;
    }

    public void setMaxTotalPercentage(long[] max){
        if(maxPercentageTotalTarget != null) {
            final boolean contentsSame = ListUtil.areContentsSame(max, maxPercentageTotalTarget);
            if(!contentsSame){
                percentageTotal.start();
            }
        }else{
            percentageTotal.start();
        }


        if(maxPercentageTotal == null){
            maxPercentageTotal = new long[max.length];
        }
        if(maxPercentageTotalTarget == null){
            maxPercentageTotalTarget = new long[max.length];
        }

        ListUtil.copy(this.maxPercentageTotalTarget,max);
    }

    public long getMaxPercentageTotal( int index){
        final float update = percentageTotal.update();
        this.maxPercentageTotal[index] = Mathf.lerp(maxPercentageTotal[index], maxPercentageTotalTarget[index], update);
        return this.maxPercentageTotal[index];
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }
}

