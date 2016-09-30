package yzx.gogoPlayer.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by yzx on 2016/9/18
 */
public class InputMethodUtil {

    /**
     * 弹出输入法
     */
    public static void showInput(final EditText view) {
        if (view == null)
            return;
        view.postDelayed(new Runnable() {
            public void run() {
                InputMethodManager is = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                is.showSoftInput(view, 0);
                view.setSelection(view.getText().length());
            }
        }, 100);
    }

}
