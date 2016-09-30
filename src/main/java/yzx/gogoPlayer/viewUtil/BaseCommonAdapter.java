package yzx.gogoPlayer.viewUtil;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * 基础adapter简化封装   by : YZX
 */
public abstract class BaseCommonAdapter<T> extends android.widget.BaseAdapter {

    private Context context;
    private List<T> list;
    private final int myLayoutId;


    public BaseCommonAdapter(Context context, List<T> mData, int itemLayoutId) {
        this.context = context;
        this.list = (mData == null) ? new ArrayList<T>(0) : mData;
        this.myLayoutId = itemLayoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.get(context, convertView, myLayoutId, position);
        convert(viewHolder, getItem(position));
        return viewHolder.getConvertView();
    }

    public abstract void convert(ViewHolder holder, T item);


    //================================================


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //================================================


    public List<T> getList() {
        return list;
    }

    public Context getContext() {
        return context;
    }

    public Activity getContextAsActivity() {
        return (Activity) context;
    }

}