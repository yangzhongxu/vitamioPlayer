package yzx.gogoPlayer.viewUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

import yzx.gogoPlayer.R;

/**
 * Created by yzx on 2016/9/18
 */
public class DialogUtil {

    /****************************************************************************************************************
     * 显示dialog
     *
     * @param act        activity
     * @param view       contentView
     * @param x          相对Gravity坐标x
     * @param y          相对Gravity坐标y
     * @param width      宽
     * @param height     高
     * @param cancelAble 是否可以cancel
     * @param anim       动画style , 没有填0
     * @param gravity    位置
     * @return 已经show的dialog对象
     */
    public static AlertDialog showDialog(Context act, View view, int x, int y, int width, int height,
                                         boolean cancelAble, int anim, int gravity, boolean bgTrans) {
        AlertDialog dialog;
        if (bgTrans)
            dialog = new AlertDialog.Builder(act, R.style.Dialog_Trans).create();
        else
            dialog = new AlertDialog.Builder(act).create();
        dialog.setCancelable(cancelAble);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.x = x;
        lp.y = y;
        lp.gravity = gravity;
        dialog.getWindow().setWindowAnimations(anim);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        return dialog;
    }

}
