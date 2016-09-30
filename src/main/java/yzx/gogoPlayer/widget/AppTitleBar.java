package yzx.gogoPlayer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import yzx.gogoPlayer.R;
import yzx.gogoPlayer.util.ResUtil;

/**
 * Created by yangzhongxu on 16/9/16
 */
public class AppTitleBar extends Toolbar{

    public AppTitleBar(Context context, AttributeSet attr){
        super(context,attr);
        init(attr);
    }


    private void init(AttributeSet attr){
        setId(R.id.app_title_bar);
        setTitleTextColor(Color.WHITE);

        TypedArray ta = getContext().obtainStyledAttributes(attr, R.styleable.AppTitleBar);

        /* show back btn */
        boolean showBack = ta.getBoolean(R.styleable.AppTitleBar_show_back_icon, true);
        if(showBack) setNavigationIcon(R.mipmap.arrow_white_back);
        /* set default bgcolor */
        boolean setDefaultBgColor = ta.getBoolean(R.styleable.AppTitleBar_set_default_bgcolor, true);
        if(setDefaultBgColor) setBackgroundColor(ResUtil.getColor(R.color.colorPrimary));

        ta.recycle();
    }

}
