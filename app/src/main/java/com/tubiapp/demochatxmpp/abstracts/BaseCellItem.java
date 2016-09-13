package com.tubiapp.demochatxmpp.abstracts;

/**
 * Created by Binh CAO on 6/13/2015.
 */
public abstract class BaseCellItem {
    private String title;
    private int type;

    public BaseCellItem() {
        super();
        type = BaseCell.reUseIdentifier;
    }

    public BaseCellItem(int type) {
        super();
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
