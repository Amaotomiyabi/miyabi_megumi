package com.miyabi.reptile.main;

import com.miyabi.reptile.persistence.Persistence;
import com.miyabi.reptile.url.UrlFactory;

/**
 * 构建组
 *
 * @author miyabi
 * @date 2021-04-07-10-43
 * @since 1.0
 **/


public class ComponentGroup {
    private Persistence persistence;
    private UrlFactory urlFactory;

    public ComponentGroup(Persistence persistence, UrlFactory urlFactory) {
        this.persistence = persistence;
        this.urlFactory = urlFactory;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public UrlFactory getUrlFactory() {
        return urlFactory;
    }

    public void setUrlFactory(UrlFactory urlFactory) {
        this.urlFactory = urlFactory;
    }
}
