package vipjokerstudio.com.telegramchart.util;

public class Mathf {
    public static final float PI = (float) Math.PI;
    public static final float Epsilon = 0.000001f;

    public static float abs(float num) {
        return Math.abs(num);
    }

    public static float pow(float a, float b) {
        return (float) Math.pow(a, b);
    }

    public static float sqrt(float num) {
        return (float) Math.sqrt(num);
    }

    public static float asin(float num) {
        return (float) Math.asin(num);
    }

    public static float sin(float num) {
        return (float) Math.sin(num);
    }

    public static float cos(float angle) {
        return (float) Math.cos(angle);
    }


    static public float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static float clamp01(float value) {
        if (value < 0) return 0;
        if (value > 1) return 1;
        return value;
    }

    /**
     * Linearly interpolates between fromValue to toValue on progress position.
     */
    public static  float lerp(float fromValue, float toValue, float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }
    public static  long lerp(long fromValue, long toValue, float progress) {
        return fromValue + (long)((toValue - fromValue) * progress);
    }
}
