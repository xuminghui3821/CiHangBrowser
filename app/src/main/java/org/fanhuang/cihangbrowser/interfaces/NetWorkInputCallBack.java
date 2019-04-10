package org.fanhuang.cihangbrowser.interfaces;

/**
 * Created by xiaohuihui on 2018/12/6.
 */

import java.io.InputStream;

/**
 * 网络请求接口
 */
public interface NetWorkInputCallBack {
    /**
     * @param action   标识，动作
     * @param response 请求成功时候的数据
     */
    void onSuccess(int action, InputStream response);

    /**
     * @param action 标识
     * @param error  请求失败时候的数据
     */
    void onError(int action, String error);
}
