package com.tubiapp.demochatxmpp;

import android.content.Context;
import android.widget.ListView;

import com.tubiapp.demochatxmpp.Items.Paticipant_Model;
import com.tubiapp.demochatxmpp.abstracts.BaseActivity;
import com.tubiapp.demochatxmpp.adapters.Participant_Adapter;
import com.tubiapp.demochatxmpp.service.XmppConnectionService;

import java.util.ArrayList;

/**
 * Created by webclues on 6/9/2016.
 */
public class GroupParticiapntList extends BaseActivity {


    private static Context mContext;
    private ListView particiapntListview;

    @Override
    protected void initDatas() {
        mContext = GroupParticiapntList.this;
    }

    @Override
    protected void initRootViews() {

        setContentView(R.layout.group_particiapntlist_activity);


    }

    @Override
    protected void initUIComponents() {

        particiapntListview = (ListView) findViewById(R.id.group_p_list_main);


    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void loadData() {

        ArrayList<Paticipant_Model> adminlist = XmppConnectionService.Get_UserList();
//        ArrayList<String> memberlist = XmppConnectionService.Get_MemberList();
//        ArrayList<String> ownerlist = XmppConnectionService.Get_OwnerList();
//        ArrayList<String> participantlist = XmppConnectionService.Get_ParticipantList();
        //ArrayList<String> modetatorlist = XmppConnectionService.Get_ModeratorList();

       /* ArrayAdapter<String> admin_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, adminlist);
        adminname.setAdapter(admin_adapter);

        ArrayAdapter<String> owner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ownerlist);
        ownername.setAdapter(owner_adapter);

        ArrayAdapter<String> participant_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, participantlist);
        particiapntListview.setAdapter(participant_adapter);*/

        Participant_Adapter padapter = new Participant_Adapter(this, adminlist);
        particiapntListview.setAdapter(padapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
