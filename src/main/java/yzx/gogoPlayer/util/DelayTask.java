package yzx.gogoPlayer.util;

import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yzx on 2016/9/19
 */
public class DelayTask {

    public DelayTask(){}
    public DelayTask(Runnable task){this.mTask = task;}


    private Runnable mTask;

    public void setTask(Runnable task){
        this.mTask = task;
    }


    private Timer timer;
    private TimerTask timerTask;

    public void readyRun(int delayMillion,boolean coverLast){
        if(timer == null){
            timer = new Timer();
            timer.schedule(timerTask = new TimerTask() {
                public void run() {
                    new Handler(Looper.getMainLooper()).post(mTask);
                }
            },delayMillion);
        }else{
            if(coverLast){
                stopReady();
                readyRun(delayMillion,false);
            }
        }
    }

    public void stopReady(){
        if(timerTask != null) timerTask.cancel();
        if(timer != null) timer.cancel();
        timerTask = null;
        timer=null;
    }

    public void runImmediately(){
        mTask.run();
    }

}
