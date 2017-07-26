package com.example.junghyen.prototype1start_up;

/**
 * Created by jungh on 2017-02-23.
 */

public class ListItemActivity {
    private int icon_image;
    private String nickname;
    private String left_person;
    private String recent_time;

    public ListItemActivity(int icon_image, String nickname, String left_person, String recent_time){
        this.icon_image = icon_image;
        this.nickname = nickname;
        this.left_person = left_person;
        this.recent_time = recent_time;
    }

    public String getRecent_time() {
        return recent_time;
    }

    public void setRecent_time(String recent_time) {
        this.recent_time = recent_time;
    }

    public String getLeft_person() {

        return left_person;
    }

    public void setLeft_person(String left_person) {
        this.left_person = left_person;
    }

    public String getNickname() {

        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getIcon_image() {

        return icon_image;
    }

    public void setIcon_image(int icon_image) {
        this.icon_image = icon_image;
    }
}
