package com.tubiapp.demochatxmpp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tubiapp.demochatxmpp.Items.UserList_Model;
import com.tubiapp.demochatxmpp.abstracts.BaseActivity;
import com.tubiapp.demochatxmpp.adapters.UserList_Adapter;
import com.tubiapp.demochatxmpp.service.XmppConnectionService;
import com.tubiapp.demochatxmpp.utils.Utility;

import org.jivesoftware.smackx.muc.HostedRoom;

import java.util.ArrayList;
import java.util.List;

public class UserList extends BaseActivity {

    public static ListView user_listview;
    public static ArrayList<UserList_Model> userlist;
    public static UserList_Adapter userlist_adapter;
    private static Context mcontext;
    private static String useremail_bundle;

  /*  public static void setvalues(ArrayList<UserList_Model> userlistdata) {

        if (user_listview != null) {
            userlist = userlistdata;
            userlist_adapter = new UserList_Adapter(mcontext, userlist);
            user_listview.setAdapter(userlist_adapter);
        }
    }*/

    public static void setlistviewdata() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                if (user_listview != null) {
                    userlist = XmppConnectionService.getContactList();
                    List<HostedRoom> rooms = XmppConnectionService.RoomList();
                    for (HostedRoom room : rooms) {
                        UserList_Model um = new UserList_Model();
                        um.setUser_name(room.getName());
                        um.setUser_email(room.getJid());
                        userlist.add(um);
                    }

                    userlist_adapter = new UserList_Adapter(mcontext, userlist);
                    user_listview.setAdapter(userlist_adapter);






                   /* DatabaseHelper db = new DatabaseHelper(mcontext);
                    List<Message_Model> offlinemessage = db.getAll_OfflineMessage();

                    if (userlist.size() > 0) {
                        Log.e("====userlist innner", "====userlist inner" + userlist.size());
                        for (int i = 0; i < userlist.size(); i++) {
                            int counter = 0;
                            String name = userlist.get(i).getUser_email();
                            for (int j = 0; j < offlinemessage.size(); j++) {
                                Message_Model model = offlinemessage.get(j);
                                Log.e("===compare====>",""+name+"<====>"+model.getMessage_from());
                                if (name.equals(model.getMessage_from().split("/")[0])) {
                                    counter++;
                                }
                            }
                            Log.e("===counter====", "" + counter);


                            userlist_adapter.updateView_unread_message(i, String.valueOf(counter));


                        }
                    }*/


                    if (userlist.size() > 0) {

                        for (int i = 0; i < userlist.size(); i++) {
                            String name = userlist.get(i).getUser_email();

                            if (name.equals(useremail_bundle)) {
                                user_listview.performItemClick(
                                        user_listview.getAdapter().getView(i, null, null),
                                        i,
                                        user_listview.getAdapter().getItemId(i));
                            }
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("===onResume==", "===onResume==");
        Utility.writeSharedPreferences_User_On_Off(mcontext, "Userlist_online");
//        XmppConnectionService.listenRosterChange();
        user_listview.post(new Runnable() {
            @Override
            public void run() {
                XmppConnectionService.getuser_status();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Toast.makeText(getBaseContext(), "You selected Group Create", Toast.LENGTH_SHORT).show();
                showInputDialog();
                break;

        }
        return true;

    }

    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(UserList.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserList.this);
        alertDialogBuilder.setView(promptView);

        final TextView texttitle = (TextView) promptView.findViewById(R.id.textView);
        texttitle.setText("Enter Group Name");

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        XmppConnectionService.createGroup(editText.getText().toString().trim());
                        setlistviewdata();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.writeSharedPreferences_User_On_Off(mcontext, "Userlist_offline");
    }

    @Override
    protected void initDatas() {
        initChatManager();
    }

    @Override
    protected void initRootViews() {
        setContentView(R.layout.activity_userlist);
    }

    @Override
    protected void initUIComponents() {
        init();
    }

    @Override
    protected void initListeners() {

        user_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = userlist.get(position).getUser_name();
                String email = userlist.get(position).getUser_email();
                String userid = userlist.get(position).getUser_email();
                Log.e("*********name******>", "" + name);
                Log.e("*********email******>", "" + email);
                Log.e("===userid====>", "" + "quorg290");
                Utility.writeSharedPreferences_BuddyID(getApplicationContext(), "quorg290@quorg.in/spark");
                Intent chatIntent = new Intent(UserList.this, ChatActivity.class);
                startActivity(chatIntent);
            }
        });
    }

    @Override
    public void loadData() {

        try {
            setlistviewdata();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initChatManager() {
        mcontext = this;
    }

    private void init() {


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            useremail_bundle = bundle.getString("User_Email");

            /*DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            db.delete_OfflineMessage();*/

        }

        userlist = new ArrayList<UserList_Model>();
        user_listview = (ListView) findViewById(R.id.user_list);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*Intent service = new Intent(UserList.this, XmppConnectionService.class);
        stopService(service);*/

    }


}
