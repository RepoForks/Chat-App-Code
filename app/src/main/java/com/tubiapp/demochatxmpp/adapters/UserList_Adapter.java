package com.tubiapp.demochatxmpp.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tubiapp.demochatxmpp.Items.UserList_Model;
import com.tubiapp.demochatxmpp.R;
import com.tubiapp.demochatxmpp.UserList;

import java.util.ArrayList;


public class UserList_Adapter extends BaseAdapter {

    private Context mcontext;
    private ArrayList<UserList_Model> userlistview;
    private Handler handler;

    public UserList_Adapter(Context context, ArrayList<UserList_Model> data) {
        mcontext = context;
        userlistview = data;

    }

    public UserList_Adapter(Context context) {
        mcontext = context;
    }


    @Override
    public int getCount() {
        return userlistview.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.adapter_userlist_innerview, parent, false);

        Log.e("===Notify===", "===Notify===");

        TextView user_name = (TextView) rowView.findViewById(R.id.adapter_user_name);

        user_name.setText(userlistview.get(position).getUser_name());

        return rowView;
    }

    public void updateView(final int index, final String status) {
        handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {


                View v = UserList.user_listview.getChildAt(index);
                if (v == null)
                    return;
                TextView delivery_rec_tick = (TextView) v.findViewById(R.id.adapter_user_status);
                delivery_rec_tick.setText(status);
                notifyDataSetChanged();


            }
        });

    }

    public void updateView_type_state(final int index, final String status) {
        handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {

                View v = UserList.user_listview.getChildAt(index);

                if (v == null) {

                    return;
                }
                TextView delivery_rec_tick = (TextView) v.findViewById(R.id.adapter_user_typestate);
                delivery_rec_tick.setVisibility(View.VISIBLE);
                delivery_rec_tick.setText(status);
                notifyDataSetChanged();


            }
        });

    }

    public void updateView_unread_message(final int index, final String status) {
        handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                View v = UserList.user_listview.getChildAt(index);

                if (v == null) {
                    Log.e("hiiiiii", "hiiiiiiiii");
                    return;
                }

                Log.e("---compare----", "" + index + "<=====>" + status);

                TextView delivery_rec_tick = (TextView) v.findViewById(R.id.adapter_user_unread_msg);
                delivery_rec_tick.setText(status);
                if (Integer.parseInt(status) != 0) {
                    Log.e("unread_message", "_unread_message");

                    delivery_rec_tick.setVisibility(View.VISIBLE);

                    //notifyDataSetChanged();
                } else {
                    delivery_rec_tick.setVisibility(View.GONE);
                }
            }
        });
    }
}
