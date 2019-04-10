package org.fanhuang.cihangbrowser.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaohuihui on 2019/1/18.
 */

@Entity
public class AdvertisingUrl {
        @Id(autoincrement = true)
        private Long id;
        private String url;
        @Generated(hash = 2101781327)
        public AdvertisingUrl(Long id, String url) {
            this.id = id;
            this.url = url;
        }
        @Generated(hash = 998906098)
        public AdvertisingUrl() {
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
