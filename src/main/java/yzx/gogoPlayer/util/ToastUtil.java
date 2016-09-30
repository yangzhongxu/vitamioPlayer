package yzx.gogoPlayer.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by yangzhongxu on 16/9/17
 */
public class ToastUtil {

    private static Context context;
    private static Toast toast;
    public static void setContext(Context context) {
        ToastUtil.context = context;
    }

    public static void toast(String str, boolean isLong){
        if(toast != null) toast.cancel();
        toast = Toast.makeText(context,str,isLong ? Toast.LENGTH_LONG:Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toast(String str){
        toast(str,false);
    }

    public static void toast(int str){
        toast(context.getString(str));
    }

    public static void toastEnqueue(String str,boolean isLong){
        Toast.makeText(context, str, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

}
