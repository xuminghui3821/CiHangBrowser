package org.fanhuang.cihangbrowser.network;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.fanhuang.cihangbrowser.interfaces.NetWorkCallBack;
import org.fanhuang.cihangbrowser.interfaces.NetWorkInputCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jameson.io.library.util.LogUtils;

/**
 * Created by xiaohuihui on 2018/12/6.
 */

public class Commdbase {
    private NetWorkCallBack mCallBack;
    private NetWorkInputCallBack mInput;
    private Context mContext;
    private final static String URL = "http://192.168.1.125:8080/";
    static private RequestQueue requestQueue;
    private String str = "";
    private String noStr = "";

    /**
     * @param context  上下文对象
     * @param callBack 回调函数
     */
    public Commdbase(Context context, NetWorkCallBack callBack) {
        this.mContext = context;
        this.mCallBack = callBack;
    }

    public Commdbase(Context context, NetWorkInputCallBack callBack) {
        this.mContext = context;
        this.mInput = callBack;
    }

    //获取参数
    public void requestGetStringDataMap(String API, final int action) {
        Log.v("url", API);
        requestQueue = getRequestQueue();
        StringRequest stringRequest = (StringRequest) new StringRequest(Request.Method.GET, API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mCallBack.onSuccess(action, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("error", error.toString());
                mCallBack.onError(action, "网络连接失败");
            }
        }).setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void requestStringDataHertMap(String API, final int action) {
        requestQueue = getRequestQueue();
        StringRequest stringRequest = (StringRequest) new StringRequest(Request.Method.GET, URL + API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!str.equals(response)) {
                            str = response;
                            Log.v("resultdata+++++++", str);
                            mCallBack.onSuccess(action, str);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("error", error.toString());
                mCallBack.onError(action, "网络连接失败");

            }
        }).setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    public void requestPost(String API, final Object object, final int action) {
        requestQueue = getRequestQueue();

        StringRequest stringRequest = (StringRequest) new StringRequest(Request.Method.POST, URL + API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("resultdata+++++++", response);
                        mCallBack.onSuccess(action, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mCallBack.onError(action, error.toString());
                Log.v("error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> q = new HashMap<String, String>();
                q.put("reqData", JSON.toJSONString(object));
                q.put("sessionId", "fec48a550a9043eb9b5bb7683d3f6f0e");
                return q;
            }
        }.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void requestPost(String API, final Map<String, String> map, final int action) {
        requestQueue = getRequestQueue();
        StringRequest stringRequest = (StringRequest) new StringRequest(Request.Method.POST, API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("resultdata+++++++", response);
                        mCallBack.onSuccess(action, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mCallBack.onError(action, error.toString());
                Log.v("error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        }.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void requestPostNoSession(String API, final Map<String, String> object, final int action) {
        requestQueue = getRequestQueue();

        StringRequest stringRequest = (StringRequest) new StringRequest(Request.Method.POST, API,   //API="https://mgtu.hao.cn/inter.php?a=browser_count"
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtils.e(response);
                        mCallBack.onSuccess(action, response);   //action:1
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mCallBack.onError(action, error.toString());
                Log.v("error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return object;
            }
        }.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void requestStringData(String API, final int action) {
        requestQueue = getRequestQueue();
        StringRequest stringRequest = (StringRequest) new StringRequest(Request.Method.GET, URL + API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mCallBack.onSuccess(action, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mCallBack.onError(action, error.toString());
                Log.v("error", error.toString());
            }
        }).setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(mContext);
        }
        File cacheDir = new File(mContext.getCacheDir(), "volley");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        requestQueue.start();
        // clear all volley caches.
        requestQueue.add(new ClearCacheRequest(cache, null));
        return requestQueue;
    }

    private void post() {

    }

    //获取参数
    public void requestStringDataMap(String API, final int action) {
        Log.v("url", API);
        requestQueue = getRequestQueue();
        CharsetStringRequest stringRequest = (CharsetStringRequest) new CharsetStringRequest(Request.Method.GET, API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mCallBack.onSuccess(action, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("error", error.toString());
                mCallBack.onError(action, "网络连接失败");
            }
        }).setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}
