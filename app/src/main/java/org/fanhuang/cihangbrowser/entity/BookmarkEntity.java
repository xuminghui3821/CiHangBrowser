package org.fanhuang.cihangbrowser.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaohuihui on 2018/12/5.
 */
@Entity
public class BookmarkEntity {
    @Id(autoincrement = true)
    private Long id;
    private String time;
    private byte[] ico;
    private String title;
    private String url;
    @Generated(hash = 826854866)
    public BookmarkEntity(Long id, String time, byte[] ico, String title,
            String url) {
        this.id = id;
        this.time = time;
        this.ico = ico;
        this.title = title;
        this.url = url;
    }
    @Generated(hash = 869114924)
    public BookmarkEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public byte[] getIco() {
        return this.ico;
    }
    public void setIco(byte[] ico) {
        this.ico = ico;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
