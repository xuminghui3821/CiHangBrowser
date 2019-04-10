package org.fanhuang.cihangbrowser.filter;

import android.content.Intent;

import org.fanhuang.cihangbrowser.network.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaohuihui on 2018/12/27.
 */

public class KeyWordHash {//关键字哈希表
    //思路是将每个关键字的第一个字符存放到Map的第一个值，所有第一个字符相同的关键字则存放到 ArrayList<String>中 以方便后续的查找工作
    public Map<Character,  ArrayList<Keyword>> hash = new HashMap<Character,  ArrayList<Keyword>>();
    public int MaxKeyWordLing = 0;//关键字最大长度
    public int MinKeyWordLing = 999;//关键字最小长度

    public void add(String KeyWord,int type){//添加关键字
        if(KeyWord.isEmpty())
            return;
        Character ch = KeyWord.charAt(0);
        if(hash.get(ch) == null){//该关键字所对应的hash没有导入
            ArrayList<Keyword> list = new ArrayList<Keyword>();
            Keyword keyword = new Keyword(KeyWord,type);
            list.add(keyword);
            hash.put(ch,list);
            if(KeyWord.length() > MaxKeyWordLing)
                MaxKeyWordLing = KeyWord.length();
            if(KeyWord.length() < MinKeyWordLing)
                MinKeyWordLing = KeyWord.length();
        }else {
            ArrayList<Keyword> list = hash.get(ch);
            Keyword keyword = new Keyword(KeyWord,type);
            list.add(keyword);
        }
    }

    public boolean find(String str){
        if(str.isEmpty())
            return false;
        if(hash.get(str.charAt(0)) == null)
            return false;
        else {
            try {
                ArrayList<Keyword> list = hash.get(str.charAt(0));
                for(int i = 0; i < list.size();i++){
                    Keyword keyword = list.get(i);
                    if(keyword.keyword == null)
                        continue;
                    if(Config.PowerfulFilter == false && keyword.type == 1)//未开启强力过滤，该关键字是强力过滤模式
                        continue;
                    if(str.length() < keyword.keyword.length())//字符串长度不够
                        continue;
                    String substr = str.substring(0,keyword.keyword.length()).toLowerCase();
                    if(keyword.keyword.toLowerCase().compareTo(substr) == 0)//找到了
                    {
                        Config.FilterNum++;
                        return true;
                    }
                }
            }
            catch (Exception e){

                e.printStackTrace();
            }

        }
        return false;
    }

    public boolean delete(String str){//删除一个词
        if(str.isEmpty())
            return false;
        if(hash.get(str.charAt(0)) == null)
            return false;
        else {
            ArrayList<Keyword> list = hash.get(str.charAt(0));
            for(int i = 0; i < list.size();i++){
                Keyword keyword = list.get(i);
                if(Config.PowerfulFilter == false && keyword.type == 1)//未开启强力过滤，该关键字是强力过滤模式
                    continue;
                if(str.length() < keyword.keyword.length())//字符串长度不够
                    continue;
                String substr = str.substring(0,keyword.keyword.length());
                if(keyword.keyword.equals(substr))//找到了
                {
                    list.remove(i);//删除这个词
                    return true;
                }
            }
        }
        return false;
    }

    public class Keyword{
        int type;//类型(正常关键字，加强关键字)
        String keyword;
        public Keyword(String str,int type){
            this.keyword = str;
            this.type = type;
        }
    }
}
