package org.fanhuang.cihangbrowser.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by xiaohuihui on 2018/12/27.
 */

@Entity
public class KeyWordList {
    @Id(autoincrement = true)
    private Long id;
    private String keyword;
    @Generated(hash = 434105235)
    public KeyWordList(Long id, String keyword) {
        this.id = id;
        this.keyword = keyword;
    }
    @Generated(hash = 324037743)
    public KeyWordList() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getKeyword() {
        return this.keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}