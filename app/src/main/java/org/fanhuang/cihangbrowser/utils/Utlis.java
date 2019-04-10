package org.fanhuang.cihangbrowser.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiaohuihui on 2018/12/5.
 */

public class Utlis {
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = null;
        try {
            b1 = view.getDrawingCache();
        } catch (OutOfMemoryError e) {
            // TODO: handle exception
        }
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public static String getTime() {
        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        String str = String.valueOf(time);
        return str;
    }

    public static void setViewGroupEnebled(ViewGroup view, boolean enabled) {
        int childern = view.getChildCount();

        for (int i = 0; i < childern; i++) {
            View child = view.getChildAt(i);
            if (child instanceof ViewGroup) {
                setViewGroupEnebled((ViewGroup) child, enabled);
            }
            child.setEnabled(enabled);
        }
        view.setEnabled(enabled);
    }

    public static String getStandardDate(String timeStr) {
        String temp = "";
        try {
            long now = System.currentTimeMillis() / 1000;
            long publish = Long.parseLong(getTimes(timeStr)) / 1000;
            long diff = now - publish;
            long months = diff / (60 * 60 * 24 * 30);
            long days = diff / (60 * 60 * 24);
            long hours = (diff - days * (60 * 60 * 24)) / (60 * 60);
            long minutes = (diff - days * (60 * 60 * 24) - hours * (60 * 60)) / 60;
            if (months > 0) {
                temp = months + "月前";
            } else if (days > 0) {
                temp = days + "天前";
            } else if (hours > 0) {
                temp = hours + "小时前";
            } else {
                temp = minutes + "分钟前";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static int getDay(String timeStr) {
        long days = 0;
        try {
            long now = System.currentTimeMillis() / 1000;
            long publish = Long.parseLong(timeStr);
            long diff = now - publish;
            days = diff / (60 * 60 * 24);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) days;
    }


    public static String getTimes(String timeString) {
        String timeStamp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CANADA);
        Date d;
        try {
            d = sdf.parse(timeString);
            long l = d.getTime();
            timeStamp = String.valueOf(l);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    public static String getDataTime(String timeString) {
        SimpleDateFormat sdr = new SimpleDateFormat("MM月dd日", Locale.CANADA);
        Date date = new Date(Long.parseLong(timeString) * 1000);
        return sdr.format(date);
    }

    public static String getDataTimess(String timeString) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CANADA);
        Date date = new Date(Long.parseLong(timeString) * 1000);
        return sdr.format(date);
    }

    public static String getTimeNit(String timeString) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);
        Date date = new Date(Long.parseLong(timeString) * 1000);
        return sdr.format(date);
    }

    /**
     * 获取当前屏幕
     *
     * @param activity
     * @return
     */
    public static Bitmap myShot(Activity activity) {
// 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
// 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();
// 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();
// 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
// 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);
// 销毁缓存信息
        view.destroyDrawingCache();
        return bmp;
    }

    public static void MoveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.setStackFromEnd(true);
    }

    public static byte[] GetBitmapByte(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
            byte[] result = output.toByteArray();//转换成功了
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        return null;

    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        //%.2f 即是保留两位小数的浮点数，后面跟上对应单位就可以了，不得不说java很方便
        if (size >= gb) {
            return String.format("%.2f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            //如果大于100MB就不用保留小数位啦
            return String.format(f > 100 ? "%.0f MB" : "%.2f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            //如果大于100kB就不用保留小数位了
            return String.format(f > 100 ? "%.0f KB" : "%.2f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static String convertFileSizeMB(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        float f = (float) size / mb;
        //如果大于100MB就不用保留小数位啦
        return String.format(f > 100 ? "%.0f" : "%.2f", f);
    }


    public static String getReallyFileName(String url) {
        String filename = "";
        URL myURL;
        HttpURLConnection conn = null;
        if (url == null || url.length() < 1) {
            return null;
        }

        try {
            myURL = new URL(url);
            conn = (HttpURLConnection) myURL.openConnection();
            conn.connect();
            conn.getResponseCode();
            URL absUrl = conn.getURL();// 获得真实Url
            Log.e("H3c", "x:" + absUrl);
            // 打印输出服务器Header信息
            // Map<String, List<String>> map = conn.getHeaderFields();
            // for (String str : map.keySet()) {
            // if (str != null) {
            // Log.e("H3c", str + map.get(str));
            // }
            // }
            filename = conn.getHeaderField("Content-Disposition");// 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
            if (filename == null || filename.length() < 1) {
                filename = absUrl.getFile();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }

        return filename;
    }

    public static String getFileName(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1, url.length());
        if (filename.length() > 20) {
            filename = System.nanoTime() + url.substring(url.length() - 4, url.length());
        }
        return filename;
    }

    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return strBuf.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {

            e.printStackTrace();
            return "";
        }
    }
    //dp转换px
    public static int dpToPx(Context context,int dp){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp*scale+0.5f);
    }

    public static int px2dp(Context context, int pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);// + 0.5f是为了让结果四舍五入
    }

    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static boolean isURL2(String str){
        String regex = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
//                 + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return match( regex ,str );
    }

    public static String GetHost(String Url){
        if(!isURL2(Url))//如果传进来的链接不是网站则返回null
            return null;

        URL url = null;
        String host = null;
        try {
            url = new URL(Url);
            host = url.getHost();// 获取主机名
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
         return  host;
    }

}
