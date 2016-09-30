package yzx.gogoPlayer.viewUtil;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;


/**
 * ViewHolder封装  by : YZX
 */
public class ViewHolder {

    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    private ViewHolder(Context context, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = View.inflate(context, layoutId, null);
        mConvertView.setTag(this);
    }

    static ViewHolder get(Context context, View convertView, int layoutId, int position) {
        if (convertView == null)
            return new ViewHolder(context, layoutId, position);
        ViewHolder h = (ViewHolder) convertView.getTag();
        h.mPosition = position;
        return h;
    }

    public View getConvertView() {
        return mConvertView;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    public int getPosition() {
        return mPosition;
    }

}