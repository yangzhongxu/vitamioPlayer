package yzx.gogoPlayer.util;

import android.app.Activity;
import android.view.View;

/**
 * Created by yzx on 2016/9/20
 */
public class ViewUtil {

    public static void setClick(Activity activity, View.OnClickListener listener,int... ids){
        for (int id : ids) {
            View view = activity.findViewById(id);
            if(view != null) view.setOnClickListener(listener);
        }
    }

}
