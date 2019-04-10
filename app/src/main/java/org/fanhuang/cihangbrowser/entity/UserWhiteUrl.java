package org.fanhuang.cihangbrowser.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaohuihui on 2018/12/20.
 */
@Entity
public class UserWhiteUrl {
    @Id(autoincrement = true)
    private Long id;
    private String url;
    @Generated(hash = 1512483351)
    public UserWhiteUrl(Long id, String url) {
        this.id = id;
        this.url = url;
    }
    @Generated(hash = 2104085957)
    public UserWhiteUrl() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
