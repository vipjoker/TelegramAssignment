package vipjokerstudio.com.telegramchart.util;

public class MathUtil {
    public static float map(float value, float currentMin, float currentMax, float targetMin, float targetMax) {

        return targetMin + (targetMax - targetMin) * ((value - currentMin) / (currentMax - currentMin));
    }

    public static float lerp (float value1,  float value2,float alpha) {
        return ((value2 - value1) * alpha) + value1;
    };



}
