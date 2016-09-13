package com.tubiapp.demochatxmpp.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tubiapp.demochatxmpp.ChatActivity;
import com.tubiapp.demochatxmpp.Items.ChatItem;
import com.tubiapp.demochatxmpp.R;
import com.tubiapp.demochatxmpp.abstracts.BaseArrayAdapter;
import com.tubiapp.demochatxmpp.apis.model.User;

import java.util.List;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by Justin on 7/29/15.
 */
public class ChatAdapter extends BaseArrayAdapter<ChatItem> {

    protected LayoutInflater inflater;
    Handler handler;
    private TextView tvChatContent;

    public ChatAdapter(Context context, List<ChatItem> objects) {
        super(context, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public ChatAdapter(Context context) {
        super(context, null);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatItem item = getItem(position);

        convertView = inflater.inflate(R.layout.item_list_chat, parent, false);
        tvChatContent = (TextView) convertView.findViewById(R.id.tvChatContent);


        User us = item.getUser();
        tvChatContent.setText(us.getEmail() + " : " + item.getMessage());


        LinearLayout layout = (LinearLayout) convertView
                .findViewById(R.id.bubble_layout);
        LinearLayout parent_layout = (LinearLayout) convertView
                .findViewById(R.id.bubble_layout_parent);
        if (us.getright_left_msg()) {
            layout.setBackgroundResource(R.drawable.bubble2);
            parent_layout.setGravity(Gravity.RIGHT);
        } else {
            layout.setBackgroundResource(R.drawable.bubble1);
            parent_layout.setGravity(Gravity.LEFT);
        }


        return convertView;
    }

    public void updateView(final int index, final String flag) {
        handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                if (flag.equals("delivered")) {
                    Log.e("updateView==delivered", "updateView==delivered");
                    View v = ChatActivity.lvChatContent.getChildAt(index);
                    if (v == null)
                        return;
                    ImageView delivery_rec_tick = (ImageView) v.findViewById(R.id.delivery_recipt);
                    delivery_rec_tick.setImageResource(R.drawable.send_to_user);
                    notifyDataSetChanged();
                }
                if (flag.equals("not_delivered")) {
                    Log.e("=not_delivered", "updateView==not_delivered");
                    View v = ChatActivity.lvChatContent.getChildAt(index);
                    if (v == null)
                        return;
                    ImageView delivery_rec_tick = (ImageView) v.findViewById(R.id.delivery_recipt);
                    delivery_rec_tick.setImageResource(R.drawable.send_server);
                    notifyDataSetChanged();
                }
                if (flag.equals("recived")) {
                    Log.e("updateView==recived", "updateView==recived");
                    View v = ChatActivity.lvChatContent.getChildAt(index);
                    if (v == null) {
                        return;
                    }
                    ImageView delivery_rec_tick = (ImageView) v.findViewById(R.id.delivery_recipt);
                    delivery_rec_tick.setImageResource(R.drawable.send_to_user);
                    notifyDataSetChanged();
                }
            }
        });

    }
}
