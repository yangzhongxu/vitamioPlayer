package yzx.gogoPlayer;

import android.app.Application;
import android.content.Context;

import yzx.gogoPlayer.util.ResUtil;
import yzx.gogoPlayer.util.ScreenUtil;
import yzx.gogoPlayer.util.ToastUtil;

/**
 * Created by yzx on 2016/9/14
 */
public class AppApplication extends Application{

    private static AppApplication application;
    public static AppApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        check(this);

        ResUtil.setContext(this);
        ScreenUtil.setContext(this);
        ToastUtil.setContext(this);
    }


    //=============================


    /* 加载so库 ;  JNI检测签名信息 */
    static { System.loadLibrary("gogo");}
    public static native void check(Context context);
    public static native void checkChecked();


    /* 测试CMake */
    //static{ System.loadLibrary("cppMain"); }
    //public static native int add(int a,int b);

}
