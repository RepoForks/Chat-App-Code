package com.tubiapp.demochatxmpp.cells;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tubiapp.demochatxmpp.Items.ChatItem;
import com.tubiapp.demochatxmpp.R;
import com.tubiapp.demochatxmpp.abstracts.BaseCell;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by Justin on 7/29/15.
 */
public class ChatCell extends BaseCell {
    private TextView tvChatContent;


    public ChatCell(Context context, View rootView) {
        super(context, rootView);
    }

    @Override
    protected void initUiComponents(View rootView) {
        tvChatContent = (TextView) rootView.findViewById(R.id.tvChatContent);

    }

    public void setData(ChatItem item) {
        tvChatContent.setText(item.getMessage());

    }

}
