package com.jacob.listview;

/**
 * Created by jacob-wj on 2015/4/5.
 */
public class GamesBean {

    private int avatar;

    private String name;

    public GamesBean(int avatar, String name) {
        this.avatar = avatar;
        this.name = name;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
