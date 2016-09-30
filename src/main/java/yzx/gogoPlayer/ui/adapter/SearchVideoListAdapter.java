package yzx.gogoPlayer.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import yzx.gogoPlayer.R;
import yzx.gogoPlayer.common.OnAdapterItemClickListener;
import yzx.gogoPlayer.common.OnAdapterItemLongClickListener;
import yzx.gogoPlayer.util.ImageUtil;
import yzx.gogoPlayer.viewUtil.BaseCommonAdapter;
import yzx.gogoPlayer.viewUtil.ViewHolder;

/**
 * Created by yangzhongxu on 16/9/17
 */
public class SearchVideoListAdapter extends BaseCommonAdapter<File> implements View.OnClickListener,View.OnLongClickListener{

    public SearchVideoListAdapter(Context context, List<File> mData){
        super(context,mData, R.layout.item_search_video);
    }

    @Override
    public void convert(ViewHolder holder, File item) {

        TextView nameTv = holder.getView(R.id.name);
        nameTv.setText(item.getName());

        ImageView image = holder.getView(R.id.image);
        ImageUtil.loadVideoThumbnail(getContextAsActivity(),item,image);

        View itemView = holder.getView(R.id.item);
        itemView.setTag(holder.getPosition());

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }


    public OnAdapterItemClickListener<File> onItemClickListener;
    public OnAdapterItemLongClickListener<File> onItemLongClickListener;


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.item){
            if(onItemClickListener != null)
                onItemClickListener.onClick(getList().get((int) view.getTag()));
        }else{
            ;
        }
    }


    @Override
    public boolean onLongClick(View view) {
        if(view.getId() == R.id.item){
            if(onItemLongClickListener != null)
                onItemLongClickListener.onLongClick(getList().get((int) view.getTag()));
        }else{
            ;
        }
        return true;
    }

}
