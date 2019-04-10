package org.fanhuang.cihangbrowser.interfaces;

/**
 * 网络请求接口
 */
public interface NetWorkCallBack {
    /**
     * @param action   标识，动作
     * @param response 请求成功时候的数据
     */
    void onSuccess(int action, String response);

    /**
     * @param action 标识
     * @param error  请求失败时候的数据
     */
    void onError(int action, String error);
}
