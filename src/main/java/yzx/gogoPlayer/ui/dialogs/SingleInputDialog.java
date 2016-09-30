package yzx.gogoPlayer.ui.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import yzx.gogoPlayer.R;
import yzx.gogoPlayer.util.ScreenUtil;
import yzx.gogoPlayer.util.InputMethodUtil;
import yzx.gogoPlayer.viewUtil.DialogUtil;

/**
 * Created by Administrator on 2016/9/18
 */
public class SingleInputDialog {

    public static abstract class OnUserClickListener{
        public abstract boolean onConfirm(String text);
        public boolean onCancel(String text){return true;};
    }


    public static void show(Context context,String title,String originTxt, String hint, final OnUserClickListener onClickListener , boolean showSoftInput){
        if(originTxt == null) originTxt = "";
        if(hint == null) hint = "";

        View contentView = View.inflate(context, R.layout.dialog_single_input, null);
        TextView titleTv = (TextView) contentView.findViewById(R.id.title);
        final EditText input = (EditText) contentView.findViewById(R.id.input);
        View confirmBtn = contentView.findViewById(R.id.confirm);
        View cancelBtn = contentView.findViewById(R.id.cancel);

        if(title == null) titleTv.setVisibility(View.GONE);
        else titleTv.setText(title);

        input.setText(originTxt);
        input.setHint(hint);
        input.setSelection(input.getText().length());

        final AlertDialog dialog = DialogUtil.showDialog(context, contentView, 0, 0, ScreenUtil.getScreenWidth()/10*9, -2, true, 0, Gravity.CENTER, false);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(onClickListener.onConfirm(input.getText().toString()))
                    dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(onClickListener.onCancel(input.getText().toString()))
                    dialog.dismiss();
            }
        });

        if(showSoftInput)
            InputMethodUtil.showInput(input);
    }

}
