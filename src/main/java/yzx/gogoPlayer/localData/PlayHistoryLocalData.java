package yzx.gogoPlayer.localData;

import java.util.ArrayList;
import java.util.List;

import yzx.gogoPlayer.AppApplication;

/**
 * Created by yzx on 2016/9/14
 */
public class PlayHistoryLocalData {

    private static final String xml_file_name = "play_history_local_data".hashCode()+"";
    private static final String KEY = "key";
    private static final int MAX_COUNT = 10;
    public static PlayHistoryLocalData get(){ return new PlayHistoryLocalData(); }

    private SPUtil mSP;
    private PlayHistoryLocalData(){
        if(mSP == null) mSP = new SPUtil(AppApplication.getApplication(),xml_file_name);
    }


    public List<String> notifyNewItem(String filePath){
        List<String> originList = getAll();
        if(originList == null) originList = new ArrayList<>(1);
        if(originList.contains(filePath)){
            originList.remove(filePath);
            originList.add(0 , filePath);
            mSP.saveObj(KEY,originList);
            return originList;
        }
        originList.add(0,filePath);
        if(originList.size() > MAX_COUNT) originList.remove(originList.size() - 1);
        mSP.saveObj(KEY,originList);
        return originList;
    }

    public List<String> getAll(){
        Object result = mSP.getObj(KEY, true, String.class);
        if(result == null) return null;
        List<String> list = (List<String>) result;
        if(list.isEmpty()) return null;
        return list;
    }

    public List<String> delete(String filePath){
        List<String> originList = getAll();
        if(originList== null) return null;
        originList.remove(filePath);
        mSP.saveObj(KEY,originList.isEmpty() ? null : originList);
        return originList.isEmpty() ? null : originList;
    }


    public void clear() {
        mSP.clear();
    }

}
