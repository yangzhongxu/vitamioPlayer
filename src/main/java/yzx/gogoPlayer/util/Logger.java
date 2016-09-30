package yzx.gogoPlayer.util;

import android.util.Log;

import yzx.gogoPlayer.BuildConfig;

/**
 * Created by yangzhongxu on 16/9/17
 */
public class Logger {

    public static final String TAG="------>>";

    public static void logE(String text){
        if(BuildConfig.DEBUG)
            Log.e(TAG,text);
    }

}
