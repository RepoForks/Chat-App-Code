package com.tubiapp.demochatxmpp.utils.interfaces;


import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by Justin on 7/30/15.
 */
public interface IncomingChatCallback {
    void onChatArrived(Chat chat, boolean createdLocally);

    void onGroupChatMessage(Message message);

}
