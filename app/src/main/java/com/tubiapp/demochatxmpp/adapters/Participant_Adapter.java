package com.tubiapp.demochatxmpp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tubiapp.demochatxmpp.Items.Paticipant_Model;
import com.tubiapp.demochatxmpp.R;

import java.util.ArrayList;

/**
 * Created by webclues on 6/9/2016.
 */
public class Participant_Adapter extends BaseAdapter {

    private Context mcontext;
    private ArrayList<Paticipant_Model> userdata;

    public Participant_Adapter(Context ct, ArrayList<Paticipant_Model> data) {
        mcontext = ct;
        userdata = data;

    }

    @Override
    public int getCount() {
        return userdata.size();
    }


    @Override
    public Object getItem(int position) {
        return userdata.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.participant_inner_view, parent, false);

        TextView user = (TextView) rowView.findViewById(R.id.p_user_adapter);
        TextView rolename = (TextView) rowView.findViewById(R.id.p_role_adapter);

        user.setText(userdata.get(position).getP_name());
        rolename.setText(userdata.get(position).getP_role());


        return rowView;

    }
}
