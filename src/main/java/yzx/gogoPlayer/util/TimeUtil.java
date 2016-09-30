package yzx.gogoPlayer.util;

/**
 * Created by yzx on 2016/9/19
 */
public class TimeUtil {

    /** ---> 00:00 */
    public static String formatTo_mmss(long totalMillion){
        int s = (int) (totalMillion/1000);
        String fen = s/60+"";
        String miao = s%60+"";
        if(fen.length()<2) fen = "0"+fen;
        if(miao.length()<2) miao = "0"+miao;
        return fen + ":" + miao;
    }

}
