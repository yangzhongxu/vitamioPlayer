package yzx.gogoPlayer.util;

import android.content.Context;
import android.os.Build;

/**
 * Created by yangzhongxu on 16/9/16
 */
public class ResUtil {

    private static Context context;
    public static void setContext(Context context){
        ResUtil.context = context;
    }

    public static int getColor(int color){
        if(Build.VERSION.SDK_INT <= 22) return context.getResources().getColor(color);
        else return context.getColor(color);
    }

}
