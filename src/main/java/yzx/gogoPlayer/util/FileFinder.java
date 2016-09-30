package yzx.gogoPlayer.util;

import android.os.Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yzx on 2016/9/14
 */
public class FileFinder {

    public interface Filter{
        boolean is(File file);
    }
    public interface OnProgressListener{
        void onComplete(List<File> list);
        void onFileFound(File file);
    }
    public interface IteratorFileListener{
        void onIterator(File file);
    }
    public static class StopTag{
        public volatile boolean running = true;
    }


    //=====================================


    private FileFinder.Filter filter;
    private FileFinder.OnProgressListener onProgressListener;
    private File parentDir;
    private boolean hasStart = false;
    private volatile StopTag stopTag = new StopTag();
    private List<String> excludeList = new ArrayList<>(0);

    private volatile List<File> resultList = new ArrayList<>(0);
    public List<File> getResultList() {
       return resultList;
    }

    private final Handler mHandler = new Handler();


    public FileFinder(File parentDir , FileFinder.Filter filter,FileFinder.OnProgressListener onProgressListener){
        this.parentDir = parentDir;
        this.filter = filter;
        this.onProgressListener = onProgressListener;
        if(parentDir.isFile()) throw new IllegalStateException("target dir can not be a file");
        if(filter == null)
            this.filter = new Filter() {
                public boolean is(File file) {
                    return true;
                }
            };
    }

    public synchronized  FileFinder setExcludePath(String... dirs){
        if(dirs.length > 0)
            excludeList = Arrays.asList(dirs);
        if(excludeList.contains(parentDir.getAbsolutePath()))
            throw new IllegalStateException("base dir can not be excluded");
        return this;
    }

    public synchronized FileFinder start(){
        if(hasStart) throw new IllegalStateException("the task has used , please new one");
        hasStart = true;
        startFind();
        return this;
    }

    public void stop(){
        stopTag.running = false;
    }


    /*  */
    /*  */
    private void startFind(){
        new Thread(){
            public void run() {
                fileIterator(parentDir, new IteratorFileListener() {
                    public void onIterator(java.io.File file) {
                        if(!filter.is(file))
                            return;
                        resultList.add(file);
                        final File target = file;
                        mHandler.post(new Runnable() {
                            public void run() {
                                if(stopTag.running) onProgressListener.onFileFound(target);
                            }
                        });
                    }
                },excludeList,stopTag);
                if(stopTag.running)
                    mHandler.post(new Runnable() {
                        public void run() {
                            onProgressListener.onComplete(resultList);
                        }
                    });
            }
        }.start();
    }



    /* 同步,广度优先,遍历回调File */
    public void fileIterator(File dir, IteratorFileListener listener,List<String> excludeList,StopTag st){
        LinkedList<File> readyAnalysisList = new LinkedList<>();
        readyAnalysisList.add(dir);
        boolean hasExcludeDir = excludeList != null && !excludeList.isEmpty();
        out:while(!readyAnalysisList.isEmpty()){
            if(!st.running) break;
            File analysisDir = readyAnalysisList.removeFirst();
            File[] childFiles = analysisDir.listFiles();
            if(childFiles == null || childFiles.length == 0)
                continue;
            for (File childFile : childFiles) {
                if(!st.running) break out;
                if(childFile.isFile())
                    listener.onIterator(childFile);
                else if(!hasExcludeDir)
                    readyAnalysisList.add(childFile);
                else if(!excludeList.contains(childFile.getAbsolutePath()))
                    readyAnalysisList.add(childFile);
            }
        }
    }

}
