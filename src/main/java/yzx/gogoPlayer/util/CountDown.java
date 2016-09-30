package yzx.gogoPlayer.util;


import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 计时器的Timer实现   by : YZX
 */
public abstract class CountDown {

    public abstract void onTick(int nowCount);


    private Handler handler = new Handler(Looper.getMainLooper());
    private Timer timer;
    private TimerTask task;

    private int currentCount;
    private int maxCount;
    private int duration;


    public CountDown(int maxCount, int duration) {
        this.maxCount = maxCount;
        this.duration = duration;
    }


    public CountDown start(int delay) {
        if (maxCount < 1)
            return this;
        if (task != null ||
                timer != null)
            return this;

        currentCount = 0;

        timer = new Timer();
        final Runnable run = new Runnable() {
            public void run() {
                onTick(++currentCount);
                if (currentCount >= maxCount)
                    CountDown.this.cancel(false);
            }
        };
        task = new TimerTask() {
            public void run() {
                handler.post(run);
            }
        };

        timer.schedule(task, delay, duration);

        return this;
    }


    /****************************************************************************************************************
     * 停止
     *
     * @param callFinish 是否调用最大count的onTick方法
     */
    public void cancel(boolean callFinish) {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (callFinish && currentCount < maxCount)
            handler.post(new Runnable() {
                public void run() {
                    onTick(maxCount);
                }
            });
    }


}