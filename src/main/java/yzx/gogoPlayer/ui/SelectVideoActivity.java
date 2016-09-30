package yzx.gogoPlayer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import yzx.gogoPlayer.R;
import yzx.gogoPlayer.ui.adapter.FileListAdapter;
import yzx.gogoPlayer.ui.model.VideoFindModel;
import yzx.gogoPlayer.util.ToastUtil;

/**
 * Created by yangzhongxu on 16/9/24
 */

public class SelectVideoActivity extends BaseActivity {

    public static void start(Context from){
        Intent intent = new Intent(from, SelectVideoActivity.class);
        from.startActivity(intent);
    }


    private FileListAdapter adapter;
    private TextView pathTv;
    private HorizontalScrollView hscrollView;
    private ListView listview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video);

        pathTv = (TextView) findViewById(R.id.pathTv);
        hscrollView = (HorizontalScrollView) findViewById(R.id.hscrollview);

        adapter = new FileListAdapter(this,Environment.getExternalStorageDirectory());
        adapter.onEmptyDirClick = () -> ToastUtil.toast(R.string.dir_is_empty);
        adapter.onUnReadAbleDirClick = () -> ToastUtil.toast(R.string.dir_is_un_readable);
        adapter.onChanged = () -> {
            pathTv.setText(adapter.getNowDir().getAbsolutePath());
            hscrollView.fullScroll(HorizontalScrollView.FOCUS_DOWN);
            listview.setSelection(0);
        };
        adapter.onFileClickListener = file -> {
            if(!VideoFindModel.isSupportVideoFile(file))
                return ;
            VideoPlayActivity.start(SelectVideoActivity.this,file);
        };

        listview = (ListView)findViewById(R.id.listview);
        listview.setAdapter(adapter);
        adapter.onChanged.run();
    }


    @Override
    public void onBackPressed() {
        if(adapter.canBack()) adapter.back();
        else super.onBackPressed();
    }

}
