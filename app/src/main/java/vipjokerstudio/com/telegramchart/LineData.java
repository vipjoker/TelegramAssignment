package vipjokerstudio.com.telegramchart;

import android.graphics.Color;

public class LineData {
    private int[] data;
    private String name;
    private int color;
    private String type;
    private boolean isActive = true;


    public LineData(int dataSize,String name,String color,String type){
        this.data = new int[dataSize];
        this.name = name;
        this.color = Color.parseColor(color);
        this.type = type;
    }
    public int[] getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
