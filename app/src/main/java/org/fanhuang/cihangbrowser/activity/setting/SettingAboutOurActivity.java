package org.fanhuang.cihangbrowser.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import org.fanhuang.cihangbrowser.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaohuihui on 2018/12/14.
 */

public class SettingAboutOurActivity extends AppCompatActivity {
    @BindView(R.id.weixin)
    TextView weixin;
    @BindView(R.id.yigong)
    TextView yigong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abourour);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.weixin)
    public void weixin(View view){
        Intent intent = new Intent();
        intent.putExtra("url","http://www.fhzd.org/cms/mobile/art.php?aid=11988");
        setResult(1,intent);
        this.finish();
    }
//    @OnClick(R.id.juanzhu)
//    public void juanzhu(View view){ Intent intent = new Intent();
//        intent.putExtra("url","http://veg001.com/zimingPlugin/wang_ye_jing_hua_qi/source/data/sponsor.html");
//        setResult(1,intent);
//        this.finish();
//    }
    @OnClick(R.id.yigong)
    public void yigong(View view){ Intent intent = new Intent();
        intent.putExtra("url","http://www.fhzd.org/cms/mobile/art.php?aid=3814");
        setResult(1,intent);
        this.finish();
    }
}
