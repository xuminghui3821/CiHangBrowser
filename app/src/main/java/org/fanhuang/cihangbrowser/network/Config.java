package org.fanhuang.cihangbrowser.network;

import org.fanhuang.cihangbrowser.utils.SDCardUtils;

/**
 * Created by xiaohuihui on 2018/11/8.
 */

public class Config {
    public static boolean isShowHome = false;//显示主页
    public static String FILE_PATH = SDCardUtils.getSDCardPath() + "cihangDownload/";//文件下载存放位置
    public final static String update = "http://download.fhzd.org/chbrowser/SoftUpgrade.txt";//更新信息
    public static Boolean FilterOutsideIPFlag = false;//IP过滤
    public static Boolean FilterMode = false;//过滤模式（不过滤，和开启）
    public static Boolean NoImage = false;//无图模式
    public static Boolean KeyWordFilter = false;//关键字过滤
    public static Boolean PowerfulFilter = false;//强力过滤
    public static Boolean ContentFilter = false;//网站内容过滤
    public static Boolean GuanggaoFilter = false;//广告过滤
    public static int FilterNum = 0;//过滤个数
    public static int ADVFilterNum = 0;//广告过滤个数
    public static Boolean useadv = false;
}
