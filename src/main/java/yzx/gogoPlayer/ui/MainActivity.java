package yzx.gogoPlayer.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ListView;

import java.io.File;

import yzx.gogoPlayer.R;
import yzx.gogoPlayer.localData.PlayHistoryLocalData;
import yzx.gogoPlayer.ui.adapter.PlayHistoryAdapter;
import yzx.gogoPlayer.util.ToastUtil;

public class MainActivity extends BaseActivity {

    private BroadcastReceiver playHistoryBroadcastReceiver;

    private ListView listview;
    private PlayHistoryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHistoryList();
        doEvent();
    }


    private void initHistoryList(){
        listview = (ListView)findViewById(R.id.listview);
        listview.setAdapter(adapter = new PlayHistoryAdapter(this));
        notifyHistoryView();
        adapter.onNotifyCallback = this::notifyHistoryView;
        adapter.onAdapterItemClickListener = file -> {
            if(file.isFile() && file.exists()) VideoPlayActivity.start(MainActivity.this,file);
            else ToastUtil.toast(R.string.history_video_cannot_find);
        };
    }


    private void doEvent(){
        /* 搜索按钮 */
        findViewById(R.id.btn_search).setOnClickListener(view -> {
            SearchVideoActivity.start(MainActivity.this);
        });
        findViewById(R.id.btn_select).setOnClickListener(view->{
            SelectVideoActivity.start(MainActivity.this);
        });
        /* 接收视频被播放的本地广播,处理播放历史 */
        LocalBroadcastManager.getInstance(this).registerReceiver(playHistoryBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                File file = (File) intent.getSerializableExtra("data"); if(file ==null) return;
                PlayHistoryLocalData.get().notifyNewItem(file.getAbsolutePath());
                adapter.notifyDataSetChanged();
            }
        },new IntentFilter(VideoPlayActivity.ACTION_VIDEO_PLAYED));
    }


    private void notifyHistoryView(){
        if(adapter == null) return ;
        if(adapter.isEmpty()){
            listview.setVisibility(View.INVISIBLE);
            findViewById(R.id.emptyHistoryView).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.emptyHistoryView).setVisibility(View.INVISIBLE);
            listview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playHistoryBroadcastReceiver);
        super.onDestroy();
    }

}
