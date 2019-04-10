package org.fanhuang.cihangbrowser.activity.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import org.fanhuang.cihangbrowser.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaohuihui on 2018/9/14.
 */

public class SettingWisdomActivity extends AppCompatActivity {
    @BindView(R.id.title)
    TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wisdom);
        ButterKnife.bind(this);
        title.setText("慈航法语");
    }

}
