package org.fanhuang.cihangbrowser.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by fan on 2017/7/14.
 */

public class Base64DecodeUtils {

    public static String getDecoder(String encode)  {

        byte[] asBytes = Base64.decode(encode, Base64.DEFAULT);
        try {
            return new String(asBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
