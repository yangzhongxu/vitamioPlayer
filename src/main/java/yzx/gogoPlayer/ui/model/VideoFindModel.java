package yzx.gogoPlayer.ui.model;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import yzx.gogoPlayer.util.FileFinder;

/**
 * Created by yangzhongxu on 16/9/16
 */
public class VideoFindModel{


    private static final ArrayList<String> videoSuffixList = new ArrayList<>(7);
    static{
        videoSuffixList.add(".mp4");
        videoSuffixList.add(".3gp");
        videoSuffixList.add(".rmvb");
        videoSuffixList.add(".mkv");
        videoSuffixList.add(".avi");
        videoSuffixList.add(".flv");
        videoSuffixList.add(".mov");
    }
    public static boolean isSupportVideoFile(File file){
        if(!file.isFile()) return false;
        int index = file.getName().lastIndexOf(".");
        if(index < 0 ) return false;
        String suffix = file.getName().substring(index);
        return videoSuffixList.contains(suffix.toLowerCase());
    }


    private FileFinder card1Finder,card2Finder;
    private List<File> card1ResultList , card2ResultList;

    /* call back, initialized outside */
    public FileFinder.OnProgressListener onProgressListener;


    /**
     * start
     */
    public void startFind() {
        if(card1Finder != null || card2Finder != null)
            return ;
        class F{
            boolean isFileAccept(File file){
                int index = file.getName().lastIndexOf(".");
                if(index < 0 ) return false;
                String suffix = file.getName().substring(index);
                return videoSuffixList.contains(suffix.toLowerCase());
            }
            void callComplete(){
                List<File> allResultList = new ArrayList<>(card1ResultList.size()+card2ResultList.size());
                allResultList.addAll(card1ResultList);
                allResultList.addAll(card2ResultList);
                onProgressListener.onComplete(allResultList);
            }
        }
        final F f = new F();


        File sdCardFile = Environment.getExternalStorageDirectory();
        card1Finder = new FileFinder(sdCardFile, new FileFinder.Filter() {
            public boolean is(File file) {
                return f.isFileAccept(file);
            }
        }, new FileFinder.OnProgressListener() {
            public void onComplete(List<File> list) {
                card1ResultList = list == null ? new ArrayList<File>(0) : list;
                if(card2ResultList != null)
                    f.callComplete();
            }
            public void onFileFound(File file) {
                onProgressListener.onFileFound(file);
            }
        }).start();


        File sdCard2File = new File("/storage/sdcard1");
        if(sdCard2File.getAbsolutePath().equals(sdCardFile.getAbsolutePath()))
            return;
        if(sdCard2File.canRead() && !sdCard2File.isFile() && sdCard2File.exists()){
            card2Finder = new FileFinder(sdCard2File, new FileFinder.Filter() {
                public boolean is(File file) {
                    return f.isFileAccept(file);
                }
            }, new FileFinder.OnProgressListener() {
                public void onComplete(List<File> list) {
                    card2ResultList = list == null?new ArrayList<File>(0) : list;
                    if(card1ResultList != null)
                        f.callComplete();
                }
                public void onFileFound(File file) {
                    onProgressListener.onFileFound(file);
                }
            }).start();
        }else{
            card2ResultList = new ArrayList<>(0);
        }
    }


    /**
     * stop
     */
    public void stopFind() {
        if(card1Finder != null) card1Finder.stop();
        if(card2Finder != null) card2Finder.stop();
    }

}
