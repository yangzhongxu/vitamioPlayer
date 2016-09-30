package yzx.gogoPlayer.util;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.view.WindowManager;

/**
 * Created by yzx on 2016/9/18
 */
public class ScreenUtil {

    private static Context context;
    public static void setContext(Context context) {
        ScreenUtil.context = context;
    }

    public static int dp2px(int dp){
        return (int) (context.getResources().getDisplayMetrics().density * dp +0.5);
    }

    public static int getScreenWidth(){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /** [1 - 255] */
    public static float getScreenBrightness(Context context) {
        try {
            return  android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    //====================================================


    public static void changeActivityBrightness(Activity activity,/* 0-1 */float p){
        WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
        attr.screenBrightness = p;
        activity.getWindow().setAttributes(attr);
    }

}
