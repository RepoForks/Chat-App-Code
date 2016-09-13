package com.tubiapp.demochatxmpp.Items;

import com.tubiapp.demochatxmpp.abstracts.BaseCellItem;
import com.tubiapp.demochatxmpp.apis.model.User;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by Justin on 7/29/15.
 */
public class ChatItem extends BaseCellItem {
    private User user;
    private String content;


    public ChatItem(User user, String content) {
        this.content = content;
        this.user = user;

    }


   /* public String getMessage() {
        return user.getEmail() + ": " + content;
    }*/

    public String getMessage() {
        return content;
    }

    public User getUser() {
        return user;
    }


}
