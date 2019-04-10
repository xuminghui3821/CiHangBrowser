package org.fanhuang.cihangbrowser.activity.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.view.fontSliderBar.FontSliderBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingTypefaceActivity extends AppCompatActivity implements FontSliderBar.OnSliderBarChangeListener {


    @BindView(R.id.back)
    MaterialRippleLayout back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.typeface)
    TextView typeface;
    @BindView(R.id.small)
    RadioButton small;
    @BindView(R.id.in)
    RadioButton in;
    @BindView(R.id.standard)
    RadioButton standard;
    @BindView(R.id.big)
    RadioButton big;
    @BindView(R.id.super_big)
    RadioButton superBig;
    @BindView(R.id.radio)
    RadioGroup radio;
    @BindView(R.id.activity_setting_typeface)
    ScrollView activitySettingTypeface;
    private int[] ints = {10, 14, 16, 21, 24};
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_typeface);
        ButterKnife.bind(this);
        title.setText("字体大小");
        type = (int) SharedPreferencesUtils.get(this, "typeface", 0);
        if (type == 0) {
            typeface.setTextSize(ints[2]);
            SharedPreferencesUtils.put(this, "typeface", 3);
        } else {
            type = (int) SharedPreferencesUtils.get(this, "typeface", 0);
            typeface.setTextSize(ints[type - 1]);
        }
        type = (int) SharedPreferencesUtils.get(this, "typeface", 0);
        setRadioButton(type);
        // sliderbar.setThumbIndex()
        //sliderbar.setThumbIndex(2);

    }

    @OnClick(R.id.back)
    public void back(View view) {
        this.finish();
    }

    @Override
    public void onIndexChanged(FontSliderBar rangeBar, int index) {
        typeface.setTextSize(ints[index]);
        SharedPreferencesUtils.put(this, "typeface", index + 1);
    }

    @OnClick(R.id.small)
    public void small(View view) {
        SharedPreferencesUtils.put(this, "typeface", 1);
        typeface.setTextSize(ints[0]);
    }

    @OnClick(R.id.in)
    public void in(View view) {
        SharedPreferencesUtils.put(this, "typeface", 2);
        typeface.setTextSize(ints[1]);
    }

    @OnClick(R.id.standard)
    public void standard(View view) {
        SharedPreferencesUtils.put(this, "typeface", 3);
        typeface.setTextSize(ints[2]);
    }

    @OnClick(R.id.big)
    public void big(View view) {
        SharedPreferencesUtils.put(this, "typeface", 4);
        typeface.setTextSize(ints[3]);
    }

    @OnClick(R.id.super_big)
    public void super_big(View view) {
        SharedPreferencesUtils.put(this, "typeface", 5);
        typeface.setTextSize(ints[4]);
    }

    private void setRadioButton(int aciton) {
        switch (aciton) {
            case 1:
                small.setChecked(true);
                break;
            case 2:
                in.setChecked(true);
                break;
            case 3:
                standard.setChecked(true);
                break;
            case 4:
                big.setChecked(true);
                break;
            case 5:
                superBig.setChecked(true);
                break;
        }
    }

}
