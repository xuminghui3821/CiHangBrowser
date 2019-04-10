package org.fanhuang.cihangbrowser.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaohuihui on 2018/12/20.
 */

@Entity
public class UserBlackUrl {
    @Id(autoincrement = true)
    private Long id;
    private String url;
    @Generated(hash = 295642614)
    public UserBlackUrl(Long id, String url) {
        this.id = id;
        this.url = url;
    }
    @Generated(hash = 868399762)
    public UserBlackUrl() {
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
