package com.example.myebook.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonUtil {

    public static String getText(String fileName) {
        StringBuilder sb = new StringBuilder("");
        try {
            //打开文件输入流
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            //读取文件内容
            while(len > 0){
                sb.append(new String(buffer,0,len));
                //继续将数据放到buffer中
                len = inputStream.read(buffer);
            }
            //关闭输入流
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Map<String, String> objStringToMapList(String objdata){
        Map<String, String> mapList =  new HashMap<>();
        try{
            JSONObject obj = new JSONObject(objdata);
            Iterator it = obj.keys();
            String vol = "";//值
            String key = null;//键
            while(it.hasNext()){//遍历JSONObject
                key = (String) it.next().toString();
                vol = obj.getString(key);
                mapList.put(key, vol);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return mapList;
    }

    public static ArrayList<String> objStringToArrayList(String objdata){
        ArrayList<String> arrList = new ArrayList<String>();
        try{
            JSONObject obj = new JSONObject(objdata);
            Iterator it = obj.keys();
            String vol = "";//值
            String key = null;//键
            while(it.hasNext()){//遍历JSONObject
                key = (String) it.next().toString();
                vol = obj.getString(key);
                arrList.add(vol);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return arrList;
    }

    public static ArrayList<String> arrayStringToArrayList(String arraydata){
        ArrayList<String> arrList = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(arraydata);
            for (int i=0; i < jsonArray.length(); i++){
                arrList.add(jsonArray.get(i).toString());
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return arrList;
    }

    //百度文字识别数据处理
    public static String stringDataFindArrayList(String data){
        Map<String, String> mapList =  new HashMap<>();
        String word_result = "";
        try{
            JSONObject obj = new JSONObject(data);
            Iterator it = obj.keys();
            String vol = "";//值
            String key = null;//键
            while(it.hasNext()){//遍历JSONObject
                key = (String) it.next().toString();
                vol = obj.getString(key);
                mapList.put(key, vol);
                if(key.equals("words_result")){
                    word_result = vol;
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        ArrayList<String> str_list = arrayStringToArrayList(word_result);
        //提取 words
        String words = "";
        for(int n = 0; n < str_list.size(); n++){
            try{
                JSONObject obj = new JSONObject(str_list.get(n));
                Iterator it = obj.keys();
                String vol = "";//值
                String key = null;//键
                while(it.hasNext()){//遍历JSONObject
                    key = (String) it.next().toString();
                    vol = obj.getString(key);
                    words += vol;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return words;
    }
}
