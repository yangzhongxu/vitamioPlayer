package yzx.gogoPlayer.util;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * Created by yangzhongxu on 16/9/17
 */
public class ImageUtil {

    public static final DiskCacheStrategy DISK_CACHE_STRATEGY = DiskCacheStrategy.SOURCE;

    public static void loadVideoThumbnail(Activity context, File file, ImageView iv){
        Glide.with(context).load(file.getAbsolutePath()).into(iv);
    }

}
