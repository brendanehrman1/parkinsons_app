package com.xlsoft.planowestapp3;

import android.media.Image;

public class TiltMenuEntry {
    private String menuDesc;
    private int menuItem;

    public TiltMenuEntry(String desc, int item) {
        menuDesc = desc;
        menuItem = item;
    }

    public String getDesc() {
        return menuDesc;
    }

    public int getItem() {
        return menuItem;
    }
}
