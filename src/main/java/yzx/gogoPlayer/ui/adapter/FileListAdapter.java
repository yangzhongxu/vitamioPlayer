package yzx.gogoPlayer.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yzx.gogoPlayer.R;
import yzx.gogoPlayer.common.OnAdapterItemClickListener;
import yzx.gogoPlayer.ui.model.VideoFindModel;
import yzx.gogoPlayer.util.ResUtil;
import yzx.gogoPlayer.viewUtil.BaseCommonAdapter;
import yzx.gogoPlayer.viewUtil.ViewHolder;

/**
 * Created by yangzhongxu on 16/9/24
 */

public class FileListAdapter extends BaseCommonAdapter<File> {

    public FileListAdapter(Context context, File targetDir){
        super(context, arrayToList(targetDir.listFiles()), R.layout.item_file_list);
        nowDir = targetDir;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        onChanged.run();
    }


    @Override
    public void convert(ViewHolder holder, File item) {
        ImageView icon = holder.getView(R.id.icon);
        if(item.isFile()) icon.setImageResource(R.mipmap.ic_file);
        else icon.setImageResource(R.mipmap.ic_folder);

        TextView nameTv = holder.getView(R.id.name);
        nameTv.setText(item.getName());
        if(VideoFindModel.isSupportVideoFile(item)) nameTv.setTextColor(ResUtil.getColor(R.color.colorAccent));
        else nameTv.setTextColor(Color.rgb(0x66,0x66,0x66));

        holder.getConvertView().setTag(R.id.tag_1,item);
        holder.getConvertView().setOnClickListener(onItemCLickListener);
    }


    public OnAdapterItemClickListener<File> onFileClickListener;
    public Runnable onEmptyDirClick;
    public Runnable onUnReadAbleDirClick;
    public Runnable onChanged;

    private File nowDir;


    private View.OnClickListener onItemCLickListener = view -> {
        File file = (File) view.getTag(R.id.tag_1);
        if(file.isFile()) onFileClickListener.onClick(file);
        else{
            nowDir = file;
            refreshByNowDir();
        }
    };

    private void refreshByNowDir(){
        if(!nowDir.canRead()){
            onUnReadAbleDirClick.run();
            nowDir = nowDir.getParentFile();
            return ;
        }
        File[] files = nowDir.listFiles();
        if(files==null || files.length<1){
            onEmptyDirClick.run();
            nowDir = nowDir.getParentFile();
        }else{
            getList().clear();
            getList().addAll(arrayToList(files));
            notifyDataSetChanged();
        }
    }


    public File getNowDir() {
        return nowDir;
    }

    public boolean canBack(){
        return nowDir.getParentFile() != null && nowDir.getParentFile().canRead();
    }

    public void back(){
        nowDir = nowDir.getParentFile();
        refreshByNowDir();
    }


    private static <T> List<T> arrayToList(T[] ts){
        ArrayList<T> result = new ArrayList<>(ts.length);
        result.addAll(Arrays.asList(ts));
        return result;
    }

}
