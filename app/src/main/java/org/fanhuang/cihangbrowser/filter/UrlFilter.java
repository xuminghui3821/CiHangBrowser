package org.fanhuang.cihangbrowser.filter;


import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.entity.UserBlackUrl;
import org.fanhuang.cihangbrowser.entity.UserWhiteUrl;
import org.fanhuang.cihangbrowser.gen.DaoSession;
import org.fanhuang.cihangbrowser.gen.UserBlackUrlDao;
import org.fanhuang.cihangbrowser.gen.UserWhiteUrlDao;
import org.fanhuang.cihangbrowser.network.Config;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by xiaohuihui on 2018/12/15.
 */

public class UrlFilter {
    static UserWhiteUrlDao userWhiteUrlDao = null;
    static UserBlackUrlDao userBlackUrlDao = null;

    static public void InitUrlFilter() {
        userWhiteUrlDao = MyAppAction.getInstances().getDaoSession().getUserWhiteUrlDao();
        userBlackUrlDao = MyAppAction.getInstances().getDaoSession().getUserBlackUrlDao();
    }

    private static Long ip2int(String ip) {
        Long num = 0L;
        if (ip == null) {
            return num;
        }
        try {
            ip = ip.replaceAll("[^0-9\\.]", ""); //去除字符串前的空字符
            String[] ips = ip.split("\\.");
            if (ips.length == 4) {
                num = Long.parseLong(ips[0], 10) * 256L * 256L * 256L + Long.parseLong(ips[1], 10) * 256L * 256L + Long.parseLong(ips[2], 10) * 256L + Long.parseLong(ips[3], 10);
                num = num >>> 0;
            }
        } catch (NullPointerException ex) {

            ex.printStackTrace();
        }
        return num;
    }

    public static boolean ISChinaIP(String url) {//过滤境外域名
        InetAddress addr = null;
        String ip = "";
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (host == "")
            return true;
        try {
            Long URLtoLL;//由IP地址转成整形
            addr = InetAddress.getByName(host);
            ip = addr.getHostAddress();
            URLtoLL = ip2int(ip);//获取该链接所对应的整形
            if (MyAppAction.getInstances().mChinaIPInit == true) {//IP列表已经初始化完毕
                //下面进行折半查找
                List<MyAppAction.ChinaIP> mChinaIPList = MyAppAction.getInstances().getChinaIPList();
                int head = 0;
                int tail = mChinaIPList.size() - 1;
                int middle = 0;
                while (head <= tail) {//折半查找的循环
                    middle = (head + tail) / 2;
                    long max = mChinaIPList.get(middle).max;
                    long min = mChinaIPList.get(middle).min;
                    if (URLtoLL >= min && URLtoLL <= max){
                        Config.FilterNum++;
                        return true;
                    }
                    else if (URLtoLL > max)
                        head = middle + 1;
                    else
                        tail = middle - 1;
                }
                return false;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return true;//返回true代表这个链接是国内的链接
    }

    public static String HostDeleteHead(String host) {//思路是只保留".com"前面的一段字符串开始到结束
        return host;
//        String[] str = host.split("\\.");
//        String newhost = "";
//        int i = 0;
//        for (i = 0; i < str.length; i++)
//            if (i != 0 && (str[i].equals("com") || str[i].equals("org") || str[i].equals("cn") || str[i].equals("net") || str[i].equals("edu")))
//                break;
//        if (i == 0 || i == 1)
//            return host;
//        else {
//            for (--i; i < str.length; i++) {
//                newhost += str[i];
//                newhost += ".";
//            }
//            newhost = newhost.substring(0, newhost.length() - 1);
//        }
//        return newhost;
    }

    public static boolean FilterUrlList(String url) {//返回true代表过滤
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if(host == null)
            return  false;
        host = HostDeleteHead(host);//将连接的头部分祛除
        //先查找是否存在白名单中
        if (userWhiteUrlDao == null || userBlackUrlDao == null)
            return false;

        //白名单查找
        UserWhiteUrl userWhiteUrl = userWhiteUrlDao.queryBuilder().where(UserWhiteUrlDao.Properties.Url.eq(host)).unique();
        if (userWhiteUrl != null) {//存在白名单中则通行
            return false;
        }
        //黑名单查找
        UserBlackUrl userBlackUrl = userBlackUrlDao.queryBuilder().where(UserBlackUrlDao.Properties.Url.eq(host)).unique();
        if (userBlackUrl != null) {//存在于和名单列表则进行过滤
            Config.FilterNum++;
            return true;
        }
        //利用折半查找来查找隐藏的黄网列表
        if (MyAppAction.getInstances().mBlackUrlInit == true) {
            List<String> BlackUrlList = MyAppAction.getInstances().GetBlackList();
            host = uri.getHost();
            int head = 0;
            int tail = BlackUrlList.size() - 1;
            int middle = 0;
            while (head <= tail) {//折半查找的循环
                middle = (head + tail) / 2;
                int item = BlackUrlList.get(middle).compareTo(host);
                if (item == 0) {
                    Config.FilterNum++;
                    return true;//找到了所对应的黄网
                }
                else if (item > 0)
                    tail = middle - 1;
                else
                    head = middle + 1;
            }
        }
        return false;
    }
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String URLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static public boolean KeyWordFilterToUrl(String url){
        if(MyAppAction.getInstances().KeyWordHashInit == false)//初始化未完成
            return false;
        if(url.indexOf("%25") != -1){//为了避免双重编码
            url =URLDecoderString(url);
        }
        if(url.indexOf("%") != -1){//解码
            url =URLDecoderString(url);
        }
        return KeyWordFilterToStr(url);
    }

    static public boolean KeyWordFilterToStr(String str) {//通过给一个字符串来判断其中是否有关键字
        if(MyAppAction.getInstances().KeyWordHashInit == false)//初始化未完成
            return false;
        for (int i = 0; i < str.length(); i++) {
            String substr = str.substring(i, str.length());
            if (substr.length() < MyAppAction.getInstances().keyWordHash.MinKeyWordLing)//剩下的长度已经小于关键字最小长度
                break;
            if (MyAppAction.getInstances().keyWordHash.find(substr))
                return true;
        }
        return false;
    }

    static public boolean UrlIsNoFilter(String url){//查看该链接是否是文件素材
        String substr = url.substring(url.length() - 4,url.length());
        if(substr.equals(".jpg") || substr.equals(".png") || substr.equals(".ico")|| substr.equals(".gif") || substr.equals(".css"))
            return true;

        substr = url.substring(url.length() - 3,url.length());
        if(substr.equals(".js"))
            return true;
        return false;
    }
}
