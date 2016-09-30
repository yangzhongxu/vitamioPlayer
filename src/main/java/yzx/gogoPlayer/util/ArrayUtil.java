package yzx.gogoPlayer.util;

/**
 * Created by yzx on 2016/9/18
 */
public class ArrayUtil {

    public static int firstIndexOf(String[] array,String str){
        for (int i = 0; i < array.length; i++){
            if(str == null && array[i] == null)
                return i;
            if(array[i].equals(str))
                return i;
        }
        return -1;
    }

}
