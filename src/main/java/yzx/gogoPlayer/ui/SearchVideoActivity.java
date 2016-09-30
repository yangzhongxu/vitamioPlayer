package yzx.gogoPlayer.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import yzx.gogoPlayer.R;
import yzx.gogoPlayer.ui.adapter.SearchVideoListAdapter;
import yzx.gogoPlayer.ui.dialogs.SimpleConfirmDialog;
import yzx.gogoPlayer.ui.dialogs.SingleInputDialog;
import yzx.gogoPlayer.ui.model.VideoFindModel;
import yzx.gogoPlayer.util.FileFinder;
import yzx.gogoPlayer.util.ToastUtil;

/**
 * Created by yzx on 2016/9/14
 */
public class SearchVideoActivity extends BaseActivity{

    public static void start(Context from){
        from.startActivity(new Intent(from,SearchVideoActivity.class));
    }


    //==========================


    private VideoFindModel vfmodel;
    private boolean hasStopSearch = false;
    private String originTitleText;
    private String searchKeyString = "";

    private SearchVideoListAdapter adapter;
    private List<File> allDataList;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video);

        /* init : ListView/Adapter */
        ListView listView = (ListView) findViewById(R.id.listview);
        adapter = new SearchVideoListAdapter(this,null);
        listView.setAdapter(adapter);
        adapter.onItemClickListener = this::jumpToPlay;
        adapter.onItemLongClickListener = this::showFileLongClickMenu;

        /* 搜索中bar点击事件 */
        findViewById(R.id.noticeSearchingLayout).setOnClickListener(view -> {
            if(hasStopSearch) return;
            vfmodel.stopFind();
            hasStopSearch = true;
            ((TextView)findViewById(R.id.tv_searching)).setText(R.string.has_stop_search);
            findViewById(R.id.pb_searching).setVisibility(View.GONE);
            view.postDelayed(()->view.setVisibility(View.GONE),800);
        });

        /* init search model */
        vfmodel = new VideoFindModel();
        vfmodel.onProgressListener = new FileFinder.OnProgressListener() {
            public void onComplete(List<File> list) {
                if(hasStopSearch) return;
                findViewById(R.id.noticeSearchingLayout).setVisibility(View.GONE);
            }
            public void onFileFound(File file) {
                if(hasStopSearch) return;
                adapter.getList().add(file);
                adapter.notifyDataSetChanged();
                if(originTitleText != null)
                    getTitleBar().setTitle(originTitleText+"("+adapter.getList().size()+")");
            }
        };

    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        originTitleText = getTitleBar().getTitle().toString();
        vfmodel.startFind();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_video_act,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_search){
            if(isSearching()){
                ToastUtil.toast(R.string.searching_waiting);
                return true;
            }
            if(searchKeyString.length() == 0 && adapter.isEmpty()){
                ToastUtil.toast(R.string.no_any_video_found);
                return true;
            }
            SingleInputDialog.show(this, getString(R.string.name_search), searchKeyString , getString(R.string.search_video_edittext_hint), new SingleInputDialog.OnUserClickListener() {
                public boolean onConfirm(String text) {
                    searchKeyString = text;
                    filterList();
                    return true;
                }
            },true);
        }
        return super.onOptionsItemSelected(item);
    }


    /* 根据关键字 , 过滤显示列表 */
    private void filterList(){
        if(allDataList == null) {
            allDataList = new ArrayList<>(adapter.getList().size());
            allDataList.addAll(adapter.getList());
        }
        ArrayList<File> tempList = new ArrayList<>();
        for (File file : allDataList)
            if(searchKeyString.length()==0 || file.getName().contains(searchKeyString))
                tempList.add(file);
        adapter.getList().clear();
        adapter.getList().addAll(tempList);
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        vfmodel.stopFind();
        hasStopSearch = true;
        super.onDestroy();
    }


    private boolean isSearching(){
        if(hasStopSearch)
            return false;
        View tvSearching = findViewById(R.id.noticeSearchingLayout);
        return tvSearching.getVisibility()==View.VISIBLE;
    }


    /* 跳转到播放 */
    private void jumpToPlay(File file){
        VideoPlayActivity.start(this,file);
    }


    /* 长按菜单 */
    private void showFileLongClickMenu(final File file){
        new AlertDialog.Builder(this).setItems(getResources().getStringArray(R.array.video_long_click_menu), (DialogInterface dialog, int index) ->{
            if(index==0){
                new AlertDialog.Builder(SearchVideoActivity.this).setItems(new String[]{file.getAbsolutePath()},null)
                        .create().show();
            }else if(index==1){
                SimpleConfirmDialog.show(SearchVideoActivity.this, null, getString(R.string.ensure_delete), new SimpleConfirmDialog.OnUserClickListener() {
                    public void onConfirm() {
                        boolean result = file.delete();
                        ToastUtil.toast(result ? R.string.delete_success : R.string.delete_failed);
                        if(result){
                            adapter.getList().remove(file);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }).create().show();
    }

}
