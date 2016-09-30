package yzx.gogoPlayer.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Administrator on 2016/9/18.
 */
public class SimpleConfirmDialog {

    public static abstract class OnUserClickListener{
        public abstract void onConfirm();
        public void onCancel(){};
        public void onDismiss(){};
    }


    public static void show(Context context, CharSequence title, CharSequence message, CharSequence confirmText, CharSequence cancelText, final OnUserClickListener cb){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(title != null) builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(confirmText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                cb.onConfirm();
            }
        });
        builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                cb.onCancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
               cb.onDismiss();
            }
        });
        dialog.show();
    }

    public static void show(Context context,CharSequence title,CharSequence message,final OnUserClickListener cb){
        show(context,title,message,"确定","取消",cb);
    }

}
