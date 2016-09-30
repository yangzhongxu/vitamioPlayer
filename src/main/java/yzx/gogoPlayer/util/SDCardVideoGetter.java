package yzx.gogoPlayer.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhongxu on 16/9/15
 */
@Deprecated//没用
public class SDCardVideoGetter {

    public Handle get(Context ctx ,final OnCompleteListener cb){
        final Handle handle = new Handle();
        final Context context = ctx.getApplicationContext();
        final Handler handler = new Handler(Looper.myLooper());

        new Thread(){
            public void run() {
                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media.DATA},
                        null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
                if(cursor == null || cursor.getCount() < 1){
                    if(cursor != null) cursor.close();
                    handler.post(new Runnable() {
                        public void run() {
                            cb.onComplete(null);
                        }
                    });
                    return ;
                }
                final ArrayList<String> resultList = new ArrayList<>(cursor.getCount());
                while(cursor.moveToNext()){
                    if(!handle.running){
                        cursor.close();
                        return ;
                    }
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    if(path != null) resultList.add(path);
                }
                cursor.close();
                handler.post(new Runnable() {
                    public void run() {
                        cb.onComplete(resultList);
                    }
                });
            }
        }.start();

        return handle;
    }


    public class Handle{
        private Handle(){}
        private volatile boolean running = true;
        public void cancel(){
            running = false;
        }
    }
    public interface OnCompleteListener{
        void onComplete(List<String> list);
    }
}
