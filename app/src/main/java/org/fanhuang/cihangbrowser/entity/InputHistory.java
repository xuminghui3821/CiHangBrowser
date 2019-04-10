package org.fanhuang.cihangbrowser.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Lan on 2017/4/11.
 */
@Entity
public class InputHistory {
    @Id
    private Long id;
    private String name;
    @Generated(hash = 141781792)
    public InputHistory(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    @Generated(hash = 1474844400)
    public InputHistory() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
