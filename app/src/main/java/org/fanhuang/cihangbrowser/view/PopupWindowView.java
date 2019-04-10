package org.fanhuang.cihangbrowser.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.interfaces.PopupCallback;

/**
 * Created by xiaohuihui on 2018/12/5.
 */

public class PopupWindowView extends PopupWindow implements View.OnClickListener {
    private int popupWidth;
    private int popupHeight;
    private GridView myGridView;
    private ImageView popBack;
    private View parentView;
    private View view;
    private Context context;
    private PopupCallback callback;
    private ImageView myself_iv;
    private LinearLayout pop_mymark, pop_history, pop_down, pop_seacher, pop_addmark, pop_refresh, pop_set, pop_exite,myselfIvParent;
    private TextView pop_lognow;


    public PopupWindowView(Context context) {
        super(context);
        initView(context);
        this.context = context;
        this.callback = (PopupCallback) context;
        initData(context, false);
        setPopConfig();
    }

    /**
     * 初始化控件
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     *
     * @param context
     */
    private void initView(final Context context) {
        parentView = View.inflate(context, R.layout.view_pupowindow, null);

        initPopView(parentView);
        //点击"个人",页面跳转


        RelativeLayout ri = (RelativeLayout) parentView.findViewById(R.id.rl);
        ri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindowView.this.dismiss();
            }
        });
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
//                setBackgroundAlpha(activity, 1);
                view.setBackgroundResource(R.mipmap.main_set);
            }
        });
    }

    private void initPopView(View parentView) {

        pop_mymark = ((LinearLayout) parentView.findViewById(R.id.pop_mymark_parent));
        pop_mymark.setOnClickListener(this);

        pop_history = ((LinearLayout) parentView.findViewById(R.id.pop_history_parent));
        pop_history.setOnClickListener(this);

        pop_down = ((LinearLayout) parentView.findViewById(R.id.pop_down_parent));
        pop_down.setOnClickListener(this);

        pop_seacher = ((LinearLayout) parentView.findViewById(R.id.pop_seacher_parent));
        pop_seacher.setOnClickListener(this);

        pop_addmark = ((LinearLayout) parentView.findViewById(R.id.pop_addmark_parent));
        pop_addmark.setOnClickListener(this);

        pop_refresh = ((LinearLayout) parentView.findViewById(R.id.pop_refresh_parent));
        pop_refresh.setOnClickListener(this);

        pop_set = ((LinearLayout) parentView.findViewById(R.id.pop_set_parent));
        pop_set.setOnClickListener(this);

        pop_exite = ((LinearLayout) parentView.findViewById(R.id.pop_exite_parent));
        pop_exite.setOnClickListener(this);
    }

    /**
     * 初始化数据
     * <h3>Version</h3> 1.0
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     *
     * @param context
     */
    private void initData(Context context, boolean show) {
        //给ListView添加数据

    }

    /**
     * 配置弹出框属性
     *
     * @version 1.0
     * @createAuthor
     * @updateAuthor
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void setPopConfig() {
        parentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = parentView.getMeasuredHeight();
        popupWidth = parentView.getMeasuredWidth();
        this.setHeight(popupHeight);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        //ColorDrawable dw = new ColorDrawable(0xb0000000);
        ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(true);// 设置外部触摸会关闭窗口
        this.setAnimationStyle(R.style.take_photo_anim);
        this.setContentView(this.parentView);//设置要显示的视图
        //获取自身的长宽高
    }

    public GridView getListView() {
        return myGridView;
    }

    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     *
     * @param v
     */
    public void showUp(View v) {
        //获取需要在其上方显示的控件的位置信息
        //获取需要在其上方显示的控件的位置信息
        //获取测量后的高度
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        this.update();
        // showAtLocation(v, Gravity.NO_GRAVITY, (location[0]) - popupWidth / 2, location[1] - popupHeight);
        //  setBackgroundAlpha(activity, 0.7f);
        showAsDropDown(v);
    }

    /**
     * 设置显示在v上方（以v的中心位置为开始位置）
     *
     * @param v
     */
    public void showUp2(View v, View view1, boolean isShwo) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        // setBackgroundAlpha(activity, 0.7f);
        //   showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
//        showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
        showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //  setBackgroundAlpha(activity, 0.7f);
//        showAsDropDown(v);
        this.view = view1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_mymark_parent:
                callback.popupCallback(R.id.pop_mymark_parent);
                PopupWindowView.this.dismiss();
                break;
            case R.id.pop_history_parent:
                callback.popupCallback(R.id.pop_history_parent);
                PopupWindowView.this.dismiss();
                break;
            case R.id.pop_down_parent:
                callback.popupCallback(R.id.pop_down_parent);
                PopupWindowView.this.dismiss();
                break;
            case R.id.pop_seacher_parent:
                callback.popupCallback(R.id.pop_seacher_parent);
                PopupWindowView.this.dismiss();
                break;
            case R.id.pop_addmark_parent:
                callback.popupCallback(R.id.pop_addmark_parent);
                PopupWindowView.this.dismiss();
                break;
            case R.id.pop_refresh_parent:
                callback.popupCallback(R.id.pop_refresh_parent);
                PopupWindowView.this.dismiss();
                break;
            case R.id.pop_set_parent:
                callback.popupCallback(R.id.pop_set_parent);
                PopupWindowView.this.dismiss();
                break;
            case R.id.pop_exite_parent:
                callback.popupCallback(R.id.pop_exite_parent);
                PopupWindowView.this.dismiss();
                break;

        }
    }
}
