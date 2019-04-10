package org.fanhuang.cihangbrowser.activity.filtersetting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.app.BottomSheetDialog;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.ListView;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.network.Config;
import org.fanhuang.cihangbrowser.utils.CustomToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaohuihui on 2018/12/18.
 */

public class FilterSettingMainActivity extends AppCompatActivity {
    @BindView(R.id.filter_outside_ip)
    LinearLayout filter_outside_ip;
    @BindView(R.id.filter_mode_select_icon)
    ImageView filter_mode_select_icon;
    @BindView(R.id.filter_outside_ip_icon)
    ImageView filter_outside_ip_icon;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.filter_outside_ip_question)
    ImageView filter_outside_ip_question;
    @BindView(R.id.guanggao_select_icon)
    ImageView guanggao_select_icon;
    @BindView(R.id.key_word_filter_select_icon)
    ImageView key_word_filter_select_icon;
    @BindView(R.id.strict_key_word_filter_select_icon)
    ImageView strict_key_word_filter_select_icon;
    @BindView(R.id.noImgIcon)
    ImageView noImgIcon;

    @BindView(R.id.content_filter_question)
    ImageView content_filter_question;
    @BindView(R.id.content_filter_question_select_icon)
    ImageView content_filter_question_select_icon;
    @BindView(R.id.num)
    TextView num;
    @BindView(R.id.adv_num)
    TextView adv_num;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_setting_main_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        num.setText(String.valueOf(Config.FilterNum));
        adv_num.setText(String.valueOf(Config.ADVFilterNum));
        title.setText("过滤");
        if(Config.FilterMode){
            filter_mode_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        }else{
            filter_mode_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        }
        if (Config.FilterOutsideIPFlag) {
            filter_outside_ip_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        } else {
            filter_outside_ip_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        }
        if(Config.KeyWordFilter){
            key_word_filter_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        }else{
            key_word_filter_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        }
        if(Config.PowerfulFilter){
            strict_key_word_filter_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        }else{
            strict_key_word_filter_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        }
        if(Config.ContentFilter){
            content_filter_question_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        }else{
            content_filter_question_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        }
        if (Config.GuanggaoFilter) {
            guanggao_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        } else {
            guanggao_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        }
        if (Config.NoImage) {
            noImgIcon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        } else {
            noImgIcon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        }
    }

    @OnClick(R.id.filter_outside_ip)
    public void filteroutsideip() {
        if (Config.FilterOutsideIPFlag) {
            filter_outside_ip_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        } else {
            filter_outside_ip_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        }
        Config.FilterOutsideIPFlag = !Config.FilterOutsideIPFlag;
        SharedPreferencesUtils.put(this, "filter_outside_ip", Config.FilterOutsideIPFlag);
    }
    @OnClick(R.id.noImg)
    public void noImg() {
        Config.NoImage = !Config.NoImage;
        SharedPreferencesUtils.put(this, "no_img", Config.NoImage);
        if (Config.NoImage) {
            noImgIcon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
            CustomToast.toast("已开启无图模式");
        } else {
            noImgIcon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
            CustomToast.toast("已关闭无图模式");
        }
    }
    @OnClick(R.id.content_filter)
    public void OnClickContentFilter() {
        if (Config.ContentFilter) {
            content_filter_question_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        } else {
            content_filter_question_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        }
        Config.ContentFilter = !Config.ContentFilter;
        SharedPreferencesUtils.put(this, "contentfilter", Config.ContentFilter);
    }

    @OnClick(R.id.filter_mode_select)
    public  void FilterModeSelect(){
        if (Config.FilterMode) {
            filter_mode_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        } else {
            filter_mode_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        }
        Config.FilterMode = !Config.FilterMode;
        SharedPreferencesUtils.put(this, "filtermode", Config.FilterMode);
    }

    @OnClick(R.id.key_word_filter)
    public void OnClickKeyWOrdFilter(){
        if (Config.KeyWordFilter) {
            key_word_filter_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        } else {
            key_word_filter_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        }
        Config.KeyWordFilter = !Config.KeyWordFilter;
        SharedPreferencesUtils.put(this, "keywordfilter", Config.KeyWordFilter);
    }

    @OnClick(R.id.strict_key_word_filter)
    public void OnClickStrictKeyWOrdFilter(){
        if (Config.PowerfulFilter) {
            strict_key_word_filter_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        } else {
            strict_key_word_filter_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        }
        Config.PowerfulFilter = !Config.PowerfulFilter;
        SharedPreferencesUtils.put(this, "powerfulfilter", Config.PowerfulFilter);
    }

    @OnClick(R.id.guanggao_mode_select)
    public void OnClickGuanggao(){
        if (Config.GuanggaoFilter) {
            guanggao_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        } else {
            guanggao_select_icon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        }
        Config.GuanggaoFilter = !Config.GuanggaoFilter;
        SharedPreferencesUtils.put(this, "guanggaofilter", Config.GuanggaoFilter);
    }

    @OnClick(R.id.filter_outside_ip_question)
    public void FilterOutsideIPQuestion(){
        View view = LayoutInflater.from(this).inflate(R.layout.outside_question_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this)
                .setView(view).create();
        mDialog.setCancelable(true);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @OnClick(R.id.filter_mode_question)
    public void OnClickFilterModeQuestion(){
        View view = LayoutInflater.from(this).inflate(R.layout.outside_question_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this)
                .setView(view).create();
        TextView title = view.findViewById(R.id.title);
        TextView info = view.findViewById(R.id.info);
        title.setText("过滤模式");
        info.setText("\t\t通过黑白名单的方式来过滤网站，网站格式为域名，例如将\"baidu.com\"设为黑名单则链接域名包含\"baidu.com\"都认为是黑名单链接。");
        mDialog.setCancelable(true);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @OnClick(R.id.content_filter_question)
    public void OnClickContentFilterQuestion(){
        View view = LayoutInflater.from(this).inflate(R.layout.outside_question_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this)
                .setView(view).create();
        TextView title = view.findViewById(R.id.title);
        TextView info = view.findViewById(R.id.info);
        title.setText("网页内容过滤");
        info.setText("\t\t此功能会造成部分手机卡顿的情况，所以作为单独功能使用，在不使用的情况下只开启关键字过滤也会处理一些网页中的内容，但并不彻底，开启此功能后会彻底清理");
        mDialog.setCancelable(true);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @OnClick(R.id.key_word_question)
    public void OnClickKeyWordQuestion(){
        View view = LayoutInflater.from(this).inflate(R.layout.outside_question_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this)
                .setView(view).create();
        TextView title = view.findViewById(R.id.title);
        TextView info = view.findViewById(R.id.info);
        title.setText("关键字过滤");
        info.setText("\t\t过滤方式是判断网站的链接中和网站标题是否包含关键字词汇，如果包含则会过滤该链接");
        mDialog.setCancelable(true);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @OnClick(R.id.strict_key_word_question)
    public void OnClickStrictKeyWordQuestion(){
        View view = LayoutInflater.from(this).inflate(R.layout.outside_question_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this)
                .setView(view).create();
        TextView title = view.findViewById(R.id.title);
        TextView info = view.findViewById(R.id.info);
        title.setText("关键字过滤");
        info.setText("\t\t该严格关键字中包含有色情信息擦边的词汇，会增加误判。");
        mDialog.setCancelable(true);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    @OnClick(R.id.back)
    public void back() {
        this.finish();
    }

    @OnClick(R.id.blacklist)
    public void  OnClickBlackList() {
        startActivityForResult(new Intent(this, BlackListActivity.class), 1);
    }

    @OnClick(R.id.noimgwhitelist)
    public void  OnClickNoImgWhiteList() {
        startActivityForResult(new Intent(this, NoImageWhiteListActivity.class), 1);
    }

    @OnClick(R.id.advertisinglist)
    public void  OnClickAdvertisingList() {
        startActivityForResult(new Intent(this, AdvertisingListActivity.class), 1);
    }

    @OnClick(R.id.whitelist)
    public void OnClickWhiteList(){
        startActivityForResult(new Intent(this, WhiteListActivity.class), 2);
    }


    @OnClick(R.id.key_word_list)
    public void OnClickKeyWordList(){
        startActivityForResult(new Intent(this, KeyListActivity.class), 3);
    }
}
