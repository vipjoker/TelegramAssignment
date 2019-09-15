package vipjokerstudio.com.telegramchart.util;


public class AnimationTask {
    private final Interpolator interpolator;
    private final float duration;
    private long targetTime;


    public AnimationTask(Interpolator interpolator, float duration) {
        this.interpolator = interpolator;
        this.duration = duration;

    }

    public void start(){
        targetTime = System.currentTimeMillis() + (long)duration;
    }


    public float update(){
        final long time = System.currentTimeMillis();
        if(targetTime > time){

            final float res = interpolator.update(0, 1, 1 - (targetTime - time) / duration);
            return res;
        }else{
           return 1;
        }

    }
    public boolean isFinished(){
        return targetTime < System.currentTimeMillis();
    }


}
