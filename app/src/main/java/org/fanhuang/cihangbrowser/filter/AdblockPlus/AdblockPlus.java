package org.fanhuang.cihangbrowser.filter.AdblockPlus;

import android.text.TextUtils;
import android.util.Log;

import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.interfaces.NetWorkCallBack;
import org.fanhuang.cihangbrowser.network.Commdbase;
import org.fanhuang.cihangbrowser.network.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.xml.transform.OutputKeys.ENCODING;


public class AdblockPlus {
    public Map<String, String> BlackFilterByKeyword = new HashMap<String,String>(30000);//存放黑名单关键字的map
    public boolean InitOver = false;
    public AdblockPlus() {
        if(!File_LoadingFile())//不存在该文件
            Link_dateFilter();
        InitOver = true;
        System.gc();//清理一下内存
    }

    public boolean File_LoadingFile()//通过本地文件更新
    {
        String path = MyAppAction._Contenxt.getFilesDir().getAbsolutePath() + "/adv/black_adv.txt";
        //StringBuilder stringBuilder = new StringBuilder();
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    while ((line = buffreader.readLine()) != null) {
                        AddText(line);
                        //stringBuilder.append(line);
                    }
                    long begintime = (long) SharedPreferencesUtils.get(MyAppAction._Contenxt, "adbfiltertime", 0l);
                    long nowtime = new Date().getTime();
                    if(nowtime - begintime > 172800000)//172800是2天的豪秒数，黑名单列表每两天更新一次
                    {
                        Commdbase commdbase = new Commdbase(MyAppAction._Contenxt, new NetWorkCallBack() {
                            @Override
                            public void onSuccess(int action, String response) {
                                if (action == 1) {
                                    String[] list = response.split("\n");
                                    List<String> blacklist = new ArrayList<>();
                                    for (int i = 0; i < list.length; i++) {
                                        String str = list[i];
                                        if (str.length() >= 1 &&(str.charAt(0) == '!' || str.charAt(0) == '['))//注释不处理
                                            continue;
                                        else if (str.indexOf("@@") != -1)//不处理白名单
                                            continue;
                                        else if (str.indexOf("domain=") != -1)//此功能在所涉及到参数 在webview中无法提供所以过滤
                                            continue;
                                        else if (str.indexOf("third-party") != -1)//同上
                                            continue;
                                        else if (str.indexOf("#") != -1)//不针对页面元素进行过滤
                                            continue;
                                        else{
                                            blacklist.add(str);
                                        }
                                    }
                                    savefiltertofile(blacklist);
                                    long nowtime = new Date().getTime();
                                    SharedPreferencesUtils.put(MyAppAction._Contenxt, "adbfiltertime", nowtime);//记录过滤列表更新时间
                                }
                            }

                            @Override
                            public void onError(int action, String error) {
                                Log.e("xiaohuihui",error);
                            }
                        });
                        try {
                            commdbase.requestGetStringDataMap("https://easylist-downloads.adblockplus.org/easylistchina+easylist.txt", 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {

                e.printStackTrace();
                return false;
            } catch (IOException e) {

                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void Link_dateFilter(){//通过网络更新广告拦截资源
        InitOver = false;
        Commdbase commdbase = new Commdbase(MyAppAction._Contenxt, new NetWorkCallBack() {
            @Override
            public void onSuccess(int action, String response) {
                if (action == 1) {
                    String[] list = response.split("\n");
                    List<String> blacklist = new ArrayList<>();
                    for (int i = 0; i < list.length; i++) {
                        String str = list[i];
                        if (str.length() >= 1 &&(str.charAt(0) == '!' || str.charAt(0) == '['))//注释不处理
                            continue;
                        else if (str.indexOf("@@") != -1)//不处理白名单
                            continue;
                        else if (str.indexOf("domain=") != -1)//此功能在所涉及到参数 在webview中无法提供所以过滤
                            continue;
                        else if (str.indexOf("third-party") != -1)//同上
                            continue;
                        else if (str.indexOf("#") != -1)//不针对页面元素进行过滤
                            continue;
                        else{
                            blacklist.add(str);
                            AddText(str);
                        }
                    }
                    savefiltertofile(blacklist);
                    long nowtime = new Date().getTime();
                    SharedPreferencesUtils.put(MyAppAction._Contenxt, "adbfiltertime", nowtime);//记录过滤列表更新时间
                }
            }

            @Override
            public void onError(int action, String error) {
                Log.e("xiaohuihui",error);
            }
        });
        try {
            commdbase.requestGetStringDataMap("https://easylist-downloads.adblockplus.org/easylistchina+easylist.txt", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savefiltertofile(List<String> list){//将文件保存到目录中
        BufferedWriter writer ;
        try {
            String path = MyAppAction._Contenxt.getFilesDir().getAbsolutePath() ;
            File file = new File(path + "/adv") ;
            if(!file.exists()){
                file.mkdirs() ;
            }
            File file2 = new File(file.getAbsoluteFile() + "/black_adv.txt") ;
            FileOutputStream out = new FileOutputStream(file2);
            writer = new BufferedWriter(new OutputStreamWriter(out)) ;
            try {
                String all = TextUtils.join("\n", list);
                writer.write(all) ;
                writer.close() ;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void AddText(String filter) {
        String Pattern = GetPattern(filter);
        if (Pattern.length() > 0) {//
            AddBlack(Pattern);//将黑名单条目进行添加
        }
    }

    private String GetPattern(String text) {
        String temppattern = text;
        if (text.contains("$")) {
            if (Pattern.matches(".*\\$(~?[\\w-]+(?:=[^,]*)?(?:,~?[\\w-]+(?:=[^,]*)?)*)$", text)) {
                Matcher matcher = Pattern.compile("\\$(~?[\\w-]+(?:=[^,]*)?(?:,~?[\\w-]+(?:=[^,]*)?)*)$").matcher(text);
                matcher.find();
                if (matcher.groupCount() > 0) {
                    temppattern = temppattern.substring(0, matcher.start(1) - 1);
                    if (temppattern.length() >= 2 && temppattern.charAt(0) == '/'
                            && temppattern.charAt(temppattern.length() - 1) == '/')
                        return "";
                }
            }
        }
        return temppattern;
    }

    private String BlackFindKeyword(String pattern) {
        String result = "";
        if (pattern == null)
            return result;

        List<String> candidates = new ArrayList<String>();
        Pattern _pattern = Pattern.compile("[^a-z0-9%*][a-z0-9%]{3,}(?=[^a-z0-9%*])");
        Matcher _matcher = _pattern.matcher(pattern.toLowerCase());
        while (_matcher.find()) {
            _matcher.group();
            int begin = _matcher.start();
            int end = _matcher.end();
            String nstr = pattern.substring(begin, end).toLowerCase();
            candidates.add(nstr);
        }

        if (candidates.size() == 0)
            return result;

        int resultCount = 0xFFFFFF;
        int resultLength = 0;

        for (int i = 0, l = candidates.size(); i < l; i++) {
            String candidate = candidates.get(i).substring(1);
            String str = BlackFilterByKeyword.get(candidate);
            String[] filters = null;
            if (str != null)
                filters = str.split("#");
            int count;
            if (filters != null) {
                count = filters.length;
            } else
                count = 0;
            if (count < resultCount || (count == resultCount && candidate.length() > resultLength)) {
                result = candidate;
                resultCount = count;
                resultLength = candidate.length();
            }
        }
        candidates = null;
        return result;
    }

    private void AddBlack(String pattern) {
        String Keyword = BlackFindKeyword(pattern);//获取该条协议的关键字
        String oldEntry = BlackFilterByKeyword.get(Keyword);//通过该关键字查看是否已经在Map中
        if (oldEntry == null) {
            BlackFilterByKeyword.put(Keyword, pattern);
        } else {
            oldEntry += "#" + pattern;//#是作为分隔符使用的，一个关键字可以对应多条连接
            BlackFilterByKeyword.put(Keyword, oldEntry);
        }
    }

    private Boolean matches(String pattern, String url) {//通过正则表达式来匹配当前的连接是否是黑名单链接
        if (pattern == "")
            return false;
        String reg = pattern;

        reg = reg.replaceAll("\\*+", "*");//
        if (reg.charAt(0) == '*')//
            reg = reg.substring(1);
        if (reg.charAt(reg.length() - 1) == '*')//
            reg = reg.substring(0, reg.length() - 1);

        reg = reg.replaceAll("\\^\\|$", "^");//
        String temp = "";
        int item = 0;
        Pattern _pattern = Pattern.compile("\\W");//
        Matcher _matcher = _pattern.matcher(reg);//
        while (_matcher.find()) {//
            _matcher.group();//
            int begin = _matcher.start();//
            int end = _matcher.end();//
            String nstr = reg.substring(begin, end);
            if (begin - 1 > 0)
                temp += reg.substring(item, begin);
            temp += "\\" + nstr;
            item = end;
        }
        if (item != 0) {
            temp += reg.substring(item, reg.length());
            reg = temp;
        }

        reg = reg.replace("\\*", ".*");
        reg = reg.replace("\\^", "(?:[\\x00-\\x24\\x26-\\x2C\\x2F\\x3A-\\x40\\x5B-\\x5E\\x60\\x7B-\\x7F]|$)");
        reg = reg.replaceAll("^\\\\\\|\\\\\\|", "^[\\\\w\\\\-]+:\\\\/+(?!\\\\/)(?:[^\\\\/]+\\\\.)?");
        reg = reg.replaceAll("^\\\\\\|", "^");
        reg = reg.replaceAll("\\\\\\|$", "\\$");
        reg = reg.toLowerCase();


        Pattern RegPattern = Pattern.compile(reg);
        Matcher matcher = RegPattern.matcher(url);
        if (matcher.find())
            return true;

        return false;
    }

    private Boolean BlackListCheckEntryMatch(String substr, String url) {
        String str = BlackFilterByKeyword.get(substr);
        String[] list = null;
        if (str != null)
            list = str.split("#");
        if (list == null)
            return false;
        for (int i = 0; i < list.length; i++) {
            if (matches(list[i], url))
                return true;
        }
        return false;
    }

    public Boolean UrlFilter(String url) {//通过链接与黑名单进行匹配
        List<String> candidates = new ArrayList<String>();
        Pattern _pattern = Pattern.compile("[a-z0-9%]{3,}");
        Matcher _matcher = _pattern.matcher(url.toLowerCase());
        while (_matcher.find()) {//获取该链接所有的关键字
            _matcher.group();
            int begin = _matcher.start();
            int end = _matcher.end();
            String nstr = url.substring(begin, end).toLowerCase();
            candidates.add(nstr);
        }

        for (int i = 0; i < candidates.size(); i++) {//用每个关键字去匹配看看是否为黑名单
            String substr = candidates.get(i);
            if (BlackListCheckEntryMatch(substr, url)) {
                candidates = null;
                return true;
            }
        }
        candidates = null;
        return false;
    }
}
