package org.fanhuang.cihangbrowser.java_bean;

import org.fanhuang.cihangbrowser.entity.History;

import java.util.List;

/**
 * Created by xiaohuihui on 2018/12/6.
 */

public class HistoryBean {
    private String time;
    private List<History> histories;

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
