package yzx.gogoPlayer.localData;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.util.Collection;

/**
 * Created by yzx on 2016/9/14
 */
public class SPUtil {

    private final SharedPreferences sp;
    public SPUtil(Context context,String fileName){
        sp = context.getApplicationContext().getSharedPreferences(fileName,Context.MODE_PRIVATE);
    }
    public SharedPreferences getSp() {return sp;}


    public void save(String key, String value){sp.edit().putString(key,value).apply();}

    public void save(String key,float value){ sp.edit().putFloat(key,value).apply(); }

    public String getString(String key){return sp.getString(key,null);}

    public float getNumber(String key){return sp.getFloat(key,0);}

    public void delete(String key){sp.edit().remove(key).apply();}

    public void clear() { sp.edit().clear().apply(); }


    public void saveObj(String key,Object obj){
        if(obj == null){
            delete(key);
            return ;
        }
        if(obj instanceof Collection){
            Collection list = (Collection)obj;
            if(list.isEmpty()){
                delete(key);
                return ;
            }
        }
        String json = JSON.toJSONString(obj);
        if(!isBlankStr(json)) save(key,json);
        else delete(key);
    }

    public Object getObj(String key,boolean isArrayData,Class clz){
        String json = getString(key);
        if(isBlankStr(json)){
            delete(key);
            return null;
        }
        try{
            if(isArrayData)return JSON.parseArray(json, clz);
            else return JSON.parseObject(json,clz);
        }catch (Exception e){
            delete(key);
            return null;
        }
    }


    //=====================


    private static boolean isBlankStr(String str){
        return str==null || str.trim().length() < 1;
    }

}
