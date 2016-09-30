package yzx.gogoPlayer.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
import yzx.gogoPlayer.R;
import yzx.gogoPlayer.entity.LocalVariable;
import yzx.gogoPlayer.util.ArrayUtil;
import yzx.gogoPlayer.util.CountDown;
import yzx.gogoPlayer.util.DelayTask;
import yzx.gogoPlayer.util.ScreenUtil;
import yzx.gogoPlayer.util.TimeUtil;
import yzx.gogoPlayer.util.ToastUtil;
import yzx.gogoPlayer.util.ViewUtil;
import yzx.gogoPlayer.viewUtil.DialogUtil;

/**
 * Created by yzx on 2016/9/18
 */
public class VideoPlayActivity extends BaseActivity{

    public static final String ACTION_VIDEO_PLAYED="video.played.action";
    public static void start(Context from, File video){
        Intent intent = new Intent(from, VideoPlayActivity.class).putExtra("data", video);
        from.startActivity(intent);
    }



    private File fromVideoFile;
    private VideoView vv;
    private MediaPlayer mp;
    private View layer;

    private TextView eventNoticeTv;
    private TextView speedDescTv;
    private TextView qualityDescTv;

    private String[] speedDescArray = {"0.5","0.8","1.0","1.3","1.5","1.8","2.0"};
    private String[] qualityDescArray ;
    private String nowQuality;
    private float playSpeed  =1.0f;
    private boolean prepared = false;
    private CountDown countDown;
    private final DelayTask delayTask = new DelayTask();
    private final int readyHideLayerTime = 7000;

    private TextView timeTv;
    private ImageView controlBtn;
    private ImageView centerControlBtn;
    private SeekBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);

        qualityDescArray = getResources().getStringArray(R.array.video_qualities);
        nowQuality= qualityDescArray[qualityDescArray.length - 1];

        fromVideoFile = (File)getIntent().getSerializableExtra("data");
        if(fromVideoFile == null){ finish(); return; }
        vv=(VideoView) findViewById(R.id.videoview);
        eventNoticeTv = (TextView) findViewById(R.id.eventNoticeTv);
        layer = findViewById(R.id.layer);

        initParams();
        initPlayControl();
        initPlayEvent();
        initTouchEvent();

        /* 发个本地广播,告诉app有视频被播放了,谁爱接谁接 */
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_VIDEO_PLAYED).putExtra("data",fromVideoFile));
    }

    private void initParams(){
        Vitamio.isInitialized(this);
        ((TextView)findViewById(R.id.fileNameTv)).setText(fromVideoFile.getName());
        /* 播放速度相关view */
        speedDescTv = (TextView) findViewById(R.id.speedTv);
        speedDescTv.setText("x"+speedDescArray[2]);
        findViewById(R.id.speedLayout).setOnClickListener(view -> {
            showSpeedSelectDialog();
        });
        /* 播放质量相关View */
        qualityDescTv = (TextView) findViewById(R.id.qualityTv);
        qualityDescTv.setText(qualityDescArray[qualityDescArray.length-1]);
        findViewById(R.id.qualityLayout).setOnClickListener(view -> {
            showQualitySelectDialog();
        });
        /* 左上角返回按钮 */
        findViewById(R.id.backIv).setOnClickListener(view->{
            finish();
        });
    }

    private void initPlayControl(){
        timeTv = (TextView)findViewById(R.id.time);
        controlBtn = (ImageView)findViewById(R.id.playControl);
        centerControlBtn = (ImageView)findViewById(R.id.playControlCenter);
        progressBar = (SeekBar) findViewById(R.id.progress);
        controlBtn.setOnClickListener(view -> {
            togglePlayStatus();
            delayTask.readyRun(readyHideLayerTime,true);
        });
        centerControlBtn.setOnClickListener(view -> {
            togglePlayStatus();
            delayTask.readyRun(readyHideLayerTime,true);
        });
        /* 每秒执行2次 */
        final LocalVariable<Boolean> hasSetMax = new LocalVariable<>(false);
        final LocalVariable<String> totalTimeString = new LocalVariable<>(null);
        countDown = new CountDown(Integer.MAX_VALUE , 500) {
            public void onTick(int nowCount) {
                //设置seekbar进度
                if(!hasSetMax.value) { progressBar.setMax((int)vv.getDuration()); hasSetMax.value = true; }
                progressBar.setProgress((int)vv.getCurrentPosition());
                //设置时间显示
                if(totalTimeString.value == null){ totalTimeString.value = TimeUtil.formatTo_mmss(vv.getDuration()); }
                timeTv.setText(TimeUtil.formatTo_mmss(vv.getCurrentPosition())+" / " + totalTimeString.value);
            }
        };
        /* SeekBar 拖动事件 */
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) showEventNoticeTv(TimeUtil.formatTo_mmss(seekBar.getProgress()));
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                delayTask.stopReady();
                ((ViewGroup)centerControlBtn.getParent()).setVisibility(View.INVISIBLE);
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                delayTask.readyRun(readyHideLayerTime,true);
                ((ViewGroup)centerControlBtn.getParent()).setVisibility(View.VISIBLE);
                hideEventNoticeTv();
                if(!prepared){  seekBar.setProgress(0); return; }
                vv.seekTo(seekBar.getProgress()); vv.start();
                notifyControlBtn();
            }
        });
        /* layer消失的任务 */
        delayTask.setTask(()->{
            if(layer.getVisibility() == View.VISIBLE)
                toggleLayer();
        });
        /* 快进/快退 */
        ViewUtil.setClick(this,view ->{
                    if(!prepared) return ;
                    int changeTimeMillion = Integer.parseInt(view.getTag().toString());
                    long toSeek = vv.getCurrentPosition()+changeTimeMillion;
                    if(toSeek <0 ) toSeek = 0;
                    else if(toSeek > vv.getDuration()) toSeek = vv.getDuration();
                    vv.seekTo(toSeek); vv.start();
                    notifyControlBtn();
                    delayTask.readyRun(readyHideLayerTime,true);
        },R.id.tv_forward_10,R.id.tv_forward_20,R.id.tv_forward_30,
           R.id.tv_back_10,R.id.tv_back_20,R.id.tv_back_30);
    }

    private void initPlayEvent(){
        vv.setVideoPath(fromVideoFile.getAbsolutePath());
        vv.setKeepScreenOn(true);
        vv.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
        vv.requestLayout();
        /* 准备完毕 */
        vv.setOnPreparedListener(localMp->{
            vv.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE,0);
            VideoPlayActivity.this.mp = localMp;
            mp.setPlaybackSpeed(playSpeed);
            prepared = true;
            countDown.start(0);
        });
        /* 播放完毕 */
        vv.setOnCompletionListener(localMP->{
            finish();
        });
        /* 出错 */
        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                ToastUtil.toast(R.string.play_error);
                finish();
                return true;
            }
        });
        /* 缓冲开始,结束,下载速度变化等.. */
        vv.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                //...
                return true;
            }
        });
        vv.start();
    }


    private void initTouchEvent(){
        final MyGestureListener gl = new MyGestureListener();
        final GestureDetector gd = new GestureDetector(this, gl);
        gd.setIsLongpressEnabled(false);
        findViewById(R.id.touchLayer).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gd.onTouchEvent(motionEvent);
                if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    gl.onUp(motionEvent);
                return true;
            }
        });
    }


    /* class : 手势识别 */
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        private final AudioManager am = (AudioManager)VideoPlayActivity.this.getSystemService(Context.AUDIO_SERVICE);
        private final float maxVolum = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        private float nowVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        private final StringBuilder sb = new StringBuilder();
        private float nowBrigntness = ScreenUtil.getScreenBrightness(VideoPlayActivity.this) / 255f;
        private boolean downInBrigntnessArea = false;
        private boolean downInVollumArea = false;
        private final float sw = ScreenUtil.getScreenWidth();
        private final float sh = ScreenUtil.getScreenHeight();
        /* 一个像素代表的亮度 */
        private final float perPxToBrightnessPercent = 1f / (sh/2f);
        /* 一个像素代表的音量 */
        private final float perPxToVolumPercent = maxVolum/(sh/2f);
        /* 第一次触发onScroll标记,用来判断scroll是横向还是竖向 */
        private boolean firstDoOnScroll = true;
        /* scroll的方向 1 : 竖着 , 0 : 横着 */
        private int scrollOrigination = -1;

        public boolean onDown(MotionEvent e) {
            /* 复原onScroll用的变量 */
            firstDoOnScroll = true; scrollOrigination = -1;
            /* 判断down的区域(是否可以改变音量or亮度) */
            float dx = e.getRawX();
            if(dx < sw/3) { downInVollumArea = !(downInBrigntnessArea = true); }
            else if(dx > sw/3*2){ downInBrigntnessArea= !(downInVollumArea  = true); }
            else{ downInBrigntnessArea = downInVollumArea = false; }
            /* 复原当前音量值(防止用户按手机的音量键导致的音量不一致) */
            nowVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            return true;
        }
        /* 双击暂停/播放 */
        public boolean onDoubleTap(MotionEvent e) {
            togglePlayStatus();
            return true;
        }
        /* 单击显示/消失弹层 */
        public boolean onSingleTapConfirmed(MotionEvent e) {
            toggleLayer(); return true;
        }
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            /* 判断是scroll的方向 : 横or竖 */
            if(firstDoOnScroll){
                firstDoOnScroll = false;
                if(Math.abs(distanceX) > Math.abs(distanceY)) scrollOrigination = 0;
                else scrollOrigination = 1;
            }
            /* 修改亮度 */
            if(scrollOrigination == 1 && downInBrigntnessArea){
                float change = perPxToBrightnessPercent *distanceY;
                nowBrigntness += change;
                if(nowBrigntness > 1) nowBrigntness = 1;
                else if(nowBrigntness < 0) nowBrigntness = 0;
                ScreenUtil.changeActivityBrightness(VideoPlayActivity.this,nowBrigntness);
                showEventNoticeTv(sb.delete(0,sb.length()).append(getString(R.string.liangdu)).append(" : ").append((int)(nowBrigntness*100)).append("%").toString());
            }
            /* 修改音量 */
            else if(scrollOrigination == 1 && downInVollumArea){
                int oldVolume = (int) nowVolume;
                float change = distanceY * perPxToVolumPercent;
                nowVolume += change;
                if(nowVolume  <0 ) nowVolume = 0;
                else if(nowVolume > maxVolum) nowVolume  = maxVolum;
                if(oldVolume != (int)(nowVolume))
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)nowVolume , 0);
                showEventNoticeTv(sb.delete(0,sb.length()).append(getString(R.string.yinliang)).append(" : ").append((int)(nowVolume*100f/maxVolum)).append("%").toString());
            }
            /* 调节进度 */
            else if(scrollOrigination == 0){

            }
            return true;
        }
        public void onUp(MotionEvent ev){
            firstDoOnScroll = true;
            scrollOrigination = -1;
            hideEventNoticeTv();
        }
    }


    private void showEventNoticeTv(String text){
        eventNoticeTv.setVisibility(View.VISIBLE);
        eventNoticeTv.setText(text);
    }

    private void hideEventNoticeTv(){
        eventNoticeTv.setVisibility(View.INVISIBLE);
    }

    private void togglePlayStatus(){
        if(!prepared) return ;
        if(vv.isPlaying()){ vv.pause();}
        else { vv.start();}
        /* change play image */
        notifyControlBtn();
    }

    private void notifyControlBtn(){
        boolean nowPlay = vv.isPlaying();
        if(nowPlay){
            controlBtn.setImageResource(R.mipmap.play_video_pause);
            centerControlBtn.setImageResource(R.mipmap.play_video_pause);
        } else{
            controlBtn.setImageResource(R.mipmap.play_video_start);
            centerControlBtn.setImageResource(R.mipmap.play_video_start);
        }
    }


    private void toggleLayer(){
        if(layer.getVisibility() == View.VISIBLE) layer.setVisibility(View.INVISIBLE);
        else layer.setVisibility(View.VISIBLE);
        /* 延迟消失 */
        if(layer.getVisibility() == View.VISIBLE){ delayTask.readyRun(readyHideLayerTime,true); }
        /* 去掉任务 */
        else{ delayTask.stopReady(); }
    }


    /* 选择play速度 */
    private void showSpeedSelectDialog(){
        int selectedColor = Color.parseColor("#aaff0703");
        View cv = View.inflate(this, R.layout.dialog_video_speed_select, null);
        final AlertDialog dialog = DialogUtil.showDialog(this, cv, 0, 0, -2, -2, true, 0, Gravity.CENTER, true);
        dialog.setCanceledOnTouchOutside(true);
        for (String s : speedDescArray) {
            TextView tv = (TextView) cv.findViewWithTag(s);
            if(Float.parseFloat(s) ==  playSpeed)
                tv.setBackgroundColor(selectedColor);
            else
                tv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        playSpeed = Float.parseFloat(v.getTag().toString());
                        if(mp != null) mp.setPlaybackSpeed(playSpeed);
                        speedDescTv.setText("x"+v.getTag().toString());
                        dialog.dismiss();
                    }
                });
        }
    }


    /* 选择播放quality */
    private void showQualitySelectDialog(){
        int selectedColor = Color.parseColor("#aaff0703");
        View cv = View.inflate(this, R.layout.dialog_video_quality_select, null);
        final AlertDialog dialog = DialogUtil.showDialog(this, cv, 0, 0, -2, -2, true, 0, Gravity.CENTER, true);
        dialog.setCanceledOnTouchOutside(true);
        for (String q : qualityDescArray) {
            TextView tv = (TextView) cv.findViewWithTag(q);
            if(nowQuality.equals(q))
                tv.setBackgroundColor(selectedColor);
            else
                tv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        nowQuality = v.getTag().toString();
                        qualityDescTv.setText(nowQuality);
                        int index = ArrayUtil.firstIndexOf(qualityDescArray, nowQuality);
                        if(index == 0) vv.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
                        else if(index == 1) vv.setVideoQuality(MediaPlayer.VIDEOQUALITY_MEDIUM);
                        else if(index == 2) vv.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
                        dialog.dismiss();
                    }
                });
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        vv.pause();
    }

    @Override
    protected void onDestroy() {
        vv.stopPlayback();
        countDown.cancel(false);
        super.onDestroy();
    }


    private boolean readyFinish = false;
    @Override
    public void onBackPressed() {
        if(readyFinish) { super.onBackPressed(); }
        else{
            ToastUtil.toast(R.string.press_again_quite);
            readyFinish = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    readyFinish = false;
                }
            },2000);
        }
    }

}
