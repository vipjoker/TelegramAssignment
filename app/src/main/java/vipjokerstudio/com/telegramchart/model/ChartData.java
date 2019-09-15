package vipjokerstudio.com.telegramchart.model;


import vipjokerstudio.com.telegramchart.LineData;
import vipjokerstudio.com.telegramchart.util.ListUtil;

public class ChartData {

    private String name;
    private final long[] xData;
    private long[] total;

    private LineData[] yData;
    private boolean isStacked;
    private boolean yScaled;

    public ChartData(long[] x, LineData[] yData) {
        this.xData = x;
        this.yData = yData;

    }


    public int size() {
        return xData.length;
    }

    public long getX(int index) {
        return xData[index];
    }

    public int getY(int chartIndex, int index) {
        return yData[chartIndex].getData()[index];
    }

    public int getMaxY(int start, int end) {
        int max = 0;// ListUtil.findMax(yData[0].getData(),start,end);
        for (LineData yDatum : yData) {
            if (yDatum.isActive()) {
                int localMax = ListUtil.findMax(yDatum.getData(), start, end);
                if (localMax > max) {
                    max = localMax;
                }
            }
        }

        return max;
    }

    public int getMaxY(int index, int start, int end) {
        return ListUtil.findMax(yData[index].getData(), start, end);
    }

    public int getMaxY() {

        int max = 0;// ListUtil.findMax(yData[0].getData());
        for (LineData yDatum : yData) {
            if (yDatum.isActive()) {
                int localMax = ListUtil.findMax(yDatum.getData());
                if (localMax > max) {
                    max = localMax;
                }
            }
        }

        return max;
    }

    public int getMaxY(int index) {
        return ListUtil.findMax(yData[index].getData());
    }

    public int getIndexByFraction(float fraction) {

        return Math.round((size() - 1) * fraction);
    }


    public LineData[] getyData() {
        return yData;
    }

    public void setyData(LineData[] yData) {
        this.yData = yData;
    }

    public long getMaxX(int offset) {
        return xData[xData.length - 1];
    }

    public long getMinX(int offset) {
        return xData[0];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public long getMaxY(float minScale, float maxScale) {
        final int startIndex = getIndexByFraction(minScale);
        final int endIndex = getIndexByFraction(maxScale);
        return getMaxY(startIndex, endIndex);
    }

    public long getMaxY(int index, float minScale, float maxScale) {
        final int startIndex = getIndexByFraction(minScale);
        final int endIndex = getIndexByFraction(maxScale);
        return getMaxY(index, startIndex, endIndex);
    }

    public long getMinY(float minScale, float maxScale) {
        final int startIndex = getIndexByFraction(minScale);
        final int endIndex = getIndexByFraction(maxScale);
        return getMinY(startIndex, endIndex);
    }

    private long getMinY(int min, int max) {
        return 0;
    }


    public boolean isStacked() {
        return isStacked;
    }

    public void setStacked(boolean stacked) {
        isStacked = stacked;
    }

    public boolean isyScaled() {
        return yScaled;
    }

    public void setyScaled(boolean yScaled) {
        this.yScaled = yScaled;
    }


    public long getMaxTotal(float min,float max){
        int minIndex = getIndexByFraction(min);
        int maxIndex = getIndexByFraction(max);
        return ListUtil.findMax(total,minIndex,maxIndex);
    }
    public long getMaxTotal(){
        return ListUtil.findMax(total);
    }

    public void updateTotal() {
        if (total == null) {
            total = new long[size()];
        }


        for (int i = 0; i < size(); i++) {
            long t = 0;
            for (LineData lineDatum : yData) {
                if(lineDatum.isActive()) {
                    t += lineDatum.getData()[i];
                }
            }
            total[i] = t;
        }
    }

    public long getTotal(int index){
        return total[index];
    }

    public  int getActiveCount(){
        int count = 0;
        for (LineData lineData : getyData()) {
            if(lineData.isActive())count++;
        }
        return count;
    }
}
