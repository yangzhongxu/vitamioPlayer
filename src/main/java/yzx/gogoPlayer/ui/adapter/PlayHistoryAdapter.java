package yzx.gogoPlayer.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import yzx.gogoPlayer.R;
import yzx.gogoPlayer.common.OnAdapterItemClickListener;
import yzx.gogoPlayer.localData.PlayHistoryLocalData;
import yzx.gogoPlayer.util.ImageUtil;
import yzx.gogoPlayer.viewUtil.BaseCommonAdapter;
import yzx.gogoPlayer.viewUtil.ViewHolder;

/**
 * Created by yzx on 2016/9/20
 */
public class PlayHistoryAdapter extends BaseCommonAdapter<String>{

    public void convert(ViewHolder holder, String item) {
        File file = new File(item);

        ImageView image = holder.getView(R.id.image);
        ImageUtil.loadVideoThumbnail(getContextAsActivity(),file,image);

        TextView nameTv = holder.getView(R.id.name);
        nameTv.setText(file.getName());

        holder.getView(R.id.item).setTag(file);
        holder.getView(R.id.item).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(onAdapterItemClickListener != null)
                    onAdapterItemClickListener.onClick((File) v.getTag());
            }
        });

        holder.getView(R.id.delete).setTag(file);
        holder.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PlayHistoryLocalData.get().delete(((File)(v.getTag())).getAbsolutePath());
                notifyDataSetChanged();
            }
        });
    }


    public Runnable onNotifyCallback;
    public OnAdapterItemClickListener<File> onAdapterItemClickListener;

    @Override
    public void notifyDataSetChanged() {
        List<String> all = PlayHistoryLocalData.get().getAll();
        getList().clear();
        if(all != null) getList().addAll(all);
        super.notifyDataSetChanged();
        if(onNotifyCallback != null)
            onNotifyCallback.run();
    }

    public PlayHistoryAdapter(Context context) {
        super(context, PlayHistoryLocalData.get().getAll(), R.layout.item_video_play_history);
    }

}
