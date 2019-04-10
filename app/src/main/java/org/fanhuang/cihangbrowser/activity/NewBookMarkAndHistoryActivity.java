package org.fanhuang.cihangbrowser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.fragment.BookMarkFragment;
import org.fanhuang.cihangbrowser.fragment.HistoryFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by xiaohuihui on 2018/12/5.
 */

public class NewBookMarkAndHistoryActivity extends SupportActivity implements BookMarkFragment.OnFragmentInteractionListener {


    @BindView(R.id.mark_line)
    TextView markLine;
    @BindView(R.id.history_line)
    TextView historyLine;
    @BindView(R.id.back)
    MaterialRippleLayout back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.bookMark)
    RadioButton bookMark;
    @BindView(R.id.history)
    RadioButton history;
    @BindView(R.id.radio)
    RadioGroup radio;
    @BindView(R.id.fragment)
    FrameLayout fragment;
    @BindView(R.id.all)
    TextView all;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.r2)
    RelativeLayout r2;
    @BindView(R.id.r1)
    RelativeLayout r1;
    @BindView(R.id.l1)
    LinearLayout l1;
    private SupportFragment[] mFragments = new SupportFragment[2];
    private int prePositionId;
    private final static String PREPOSITION = "preposition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();//获取传递进来的intent
        String state = intent.getStringExtra("state");
        setContentView(R.layout.activity_new_book_mark_and_history);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            mFragments[0] = BookMarkFragment.newInstance();
            mFragments[1] = HistoryFragment.newInstance();
            loadMultipleRootFragment(R.id.fragment, 0,
                    mFragments[0],
                    mFragments[1]
            );
            if(state.equals( "history")) {
                markLine.setVisibility(View.INVISIBLE);
                historyLine.setVisibility(View.VISIBLE);
                history.setChecked(true);
                bookMark.setTextColor(getResources().getColor(R.color.gray));
                history.setTextColor(getResources().getColor(R.color.text_back));
                showHideFragment(mFragments[1], mFragments[0]);
            }
            else {
                markLine.setVisibility(View.VISIBLE);
                historyLine.setVisibility(View.INVISIBLE);
                bookMark.setTextColor(getResources().getColor(R.color.text_back));
                history.setTextColor(getResources().getColor(R.color.gray));
                bookMark.setChecked(true);
                showHideFragment(mFragments[0], mFragments[1]);
            }
        } else {
            radio.check(prePositionId);
            prePositionId = savedInstanceState.getInt(PREPOSITION, 0);
            mFragments[0] = findFragment(BookMarkFragment.class);
            mFragments[1] = findFragment(HistoryFragment.class);
            if (prePositionId==0) {
                markLine.setVisibility(View.VISIBLE);
                historyLine.setVisibility(View.INVISIBLE);
                bookMark.setTextColor(getResources().getColor(R.color.text_back));
                history.setTextColor(getResources().getColor(R.color.gray));
                bookMark.setChecked(true);

                showHideFragment(mFragments[0], mFragments[1]);
            }else {
                markLine.setVisibility(View.INVISIBLE);
                historyLine.setVisibility(View.VISIBLE);
                bookMark.setTextColor(getResources().getColor(R.color.gray));
                history.setTextColor(getResources().getColor(R.color.text_back));
                history.setChecked(true);
                showHideFragment(mFragments[1], mFragments[0]);
            }
        }
        init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {//保存页面状态 在下次打开该页面时会读取到该状态
        outState.putInt(PREPOSITION, prePositionId);
    }

    private void init() {
        r1.setVisibility(View.VISIBLE);
        r2.setVisibility(View.INVISIBLE);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = getPositionFromId(checkedId);
                r1.setVisibility(View.VISIBLE);
                r2.setVisibility(View.INVISIBLE);
                BookMarkFragment bookMarkFragment = (BookMarkFragment) mFragments[0];
                bookMarkFragment.init();
                //showHideFragment(mFragments[position], mFragments[getPositionFromId(prePositionId)]);
                prePositionId = checkedId;
                if (position==0) {
                    markLine.setVisibility(View.VISIBLE);
                    historyLine.setVisibility(View.INVISIBLE);
                    bookMark.setTextColor(getResources().getColor(R.color.text_back));
                    history.setTextColor(getResources().getColor(R.color.gray));
                    showHideFragment(mFragments[0], mFragments[1]);
                }else {
                    markLine.setVisibility(View.INVISIBLE);
                    historyLine.setVisibility(View.VISIBLE);
                    bookMark.setTextColor(getResources().getColor(R.color.gray));
                    history.setTextColor(getResources().getColor(R.color.text_back));
                    showHideFragment(mFragments[1], mFragments[0]);
                }
            }
        });
    }

    @OnClick(R.id.back)
    public void back(View view) {
        this.finish();
    }

    private int getPositionFromId(int id) {
        int index = 0;
        switch (id) {
            case R.id.bookMark:
                index = 0;
                break;
            case R.id.history:
                index = 1;
                break;
        }
        return index;
    }

    @Override
    public void onFragmentInteraction(int uri) {
        if (uri == 1) {
            r1.setVisibility(View.INVISIBLE);
            r2.setVisibility(View.VISIBLE);
        } else {
            r1.setVisibility(View.VISIBLE);
            r2.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//按下按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {//是退出按钮
            if (prePositionId == 0) {//收藏页
                BookMarkFragment bookMarkFragment = (BookMarkFragment) mFragments[0];
                if(bookMarkFragment.markAdapter.getAction() == 1){
                    cancel();
                    return  true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.all)
    public void all() {
        BookMarkFragment bookMarkFragment = (BookMarkFragment) mFragments[0];
        if (all.getText().equals("全选")) {
            bookMarkFragment.setAall(true);
            all.setText("全不选");
        } else {
            bookMarkFragment.setAall(false);
            all.setText("全选");
        }

    }

    @OnClick(R.id.cancel)
    public void cancel() {
        r1.setVisibility(View.VISIBLE);
        r2.setVisibility(View.INVISIBLE);
        BookMarkFragment bookMarkFragment = (BookMarkFragment) mFragments[0];
        bookMarkFragment.init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            BookMarkFragment bookMarkFragment = (BookMarkFragment) mFragments[0];
            bookMarkFragment.init();
        }
        r1.setVisibility(View.VISIBLE);
        r2.setVisibility(View.INVISIBLE);

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

}
