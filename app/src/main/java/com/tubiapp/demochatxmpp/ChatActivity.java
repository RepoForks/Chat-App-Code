package com.tubiapp.demochatxmpp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tubiapp.demochatxmpp.Database.DatabaseHelper;
import com.tubiapp.demochatxmpp.Items.ChatItem;
import com.tubiapp.demochatxmpp.Items.Message_Model;
import com.tubiapp.demochatxmpp.abstracts.BaseActivity;
import com.tubiapp.demochatxmpp.adapters.ChatAdapter;
import com.tubiapp.demochatxmpp.adapters.UserList_Adapter;
import com.tubiapp.demochatxmpp.apis.ExecutorManager;
import com.tubiapp.demochatxmpp.apis.model.User;
import com.tubiapp.demochatxmpp.service.XmppConnectionService;
import com.tubiapp.demochatxmpp.utils.Utility;
import com.tubiapp.demochatxmpp.utils.interfaces.IncomingChatCallback;
import com.tubiapp.demochatxmpp.webapicall.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends BaseActivity implements View.OnClickListener, IncomingChatCallback {

    public static ListView lvChatContent;
    private static boolean yes_no_flag = false;
    private static boolean get_yes_no_flag = false;
    private static UserList_Adapter user_list_adapter;
    private static Context mcontext;
    private static String sendingMessage;
    private static boolean groupchatflag;
    private static Bitmap finalbitmap;
    private static String send_image_url = "http://webcluesglobal.com/qa/mapi/brnetwork/api.php";
    DatabaseHelper db;
    private ChatAdapter adapter;
    private EditText edtChat;
    private ImageView btnSend;
    private TextView text_yes_no_ans;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String parseXMLAndStoreIt(String mymsg, String myid) {

        XmlPullParserFactory xmlFactoryObject;
        XmlPullParser myparser = null;

        int event;
        String text = null;
        String msg = "";

        InputStream inputstream = new ByteArrayInputStream(mymsg.toString().getBytes(StandardCharsets.UTF_8));
        try {

            xmlFactoryObject = XmlPullParserFactory.newInstance();
            myparser = xmlFactoryObject.newPullParser();
            myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            myparser.setInput(inputstream, null);


        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            event = myparser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myparser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = myparser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("active")) {

                            msg = "Active";
                        }
                        if (name.equals("composing")) {

                            msg = "Typing...";
                        }
                        if (name.equals("paused")) {
                            msg = "Paused...";
                        }
                        if (name.equals("inactive")) {

                            msg = "Inactive";
                        }
                        break;
                }
                event = myparser.next();
            }



          /*  for (int i = 0; i < UserList.userlist.size(); i++) {
                String user_id = UserList.userlist.get(i).getUser_email();
                if (myid.split("/")[0].equals(user_id)) {
                    user_list_adapter.updateView_type_state(i, msg);
                }
            }

            setToast(msg);*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return msg;

    }

    @Override
    protected void initDatas() {
        mcontext = ChatActivity.this;
        initChatManager();
    }

    @Override
    protected void initRootViews() {
        setContentView(R.layout.activity_chat);

        XmppConnectionService.All_msg_id.clear();
    }

    private void setToast(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                actionBar.setSubtitle(Html.fromHtml("<font color='#ffffff'>" + msg + "</font>"));
            }
        });

    }

    @Override
    protected void initUIComponents() {


        lvChatContent = (ListView) findViewById(R.id.lvChatContent);
        edtChat = (EditText) findViewById(R.id.edtChat);
        text_yes_no_ans = (TextView) findViewById(R.id.text_yes_no_ans);
        btnSend = (ImageView) findViewById(R.id.btnSend);
        user_list_adapter = new UserList_Adapter(mcontext);


        db = new DatabaseHelper(mcontext);
        db.delete_OfflineMessage();

        String userstatus = XmppConnectionService.getUser_online_offline_status(groupchatflag);
        actionBar.setSubtitle(Html.fromHtml("<font color='#ffffff'>" + userstatus + "</font>"));


        text_yes_no_ans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!yes_no_flag) {
                    yes_no_flag = true;
                    Toast.makeText(mcontext, "" + yes_no_flag, Toast.LENGTH_SHORT).show();
                } else {
                    yes_no_flag = false;
                    Toast.makeText(mcontext, "" + yes_no_flag, Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (get_yes_no_flag) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(mcontext.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtChat.getWindowToken(), 0);
                    Yes_No_Popup();
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(mcontext.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edtChat, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    XmppConnectionService.Set_typing_state(ChatState.active);
                } else {
                    XmppConnectionService.Set_typing_state(ChatState.composing);
                }
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        Utility.writeSharedPreferences_User_On_Off(mcontext, "Chat_activity_online");


    }


    @Override
    protected void onPause() {
        super.onPause();
        Utility.writeSharedPreferences_User_On_Off(mcontext, "Chat_activity_offline");
    }

    public void Yes_No_Popup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Select Answer only yes no");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                sendingMessage = "yes";
                XmppConnectionService.sendMessage(sendingMessage, yes_no_flag, groupchatflag);
                get_yes_no_flag = false;

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendingMessage = "no";
                XmppConnectionService.sendMessage(sendingMessage, yes_no_flag, groupchatflag);
                get_yes_no_flag = false;
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void initListeners() {
        btnSend.setOnClickListener(this);

    }

    private void scrollMyListViewToBottom() {
        lvChatContent.post(new Runnable() {
            @Override
            public void run() {
                lvChatContent.setSelection(lvChatContent.getCount() - 1);
            }
        });
    }

    @Override
    protected void loadData() {
        List<ChatItem> listItems = new ArrayList<>();
        adapter = new ChatAdapter(this, listItems);
        lvChatContent.setAdapter(adapter);

        String from_email = Utility.getUser_Email(mcontext) + "@" + XmppConnectionService.SERVICE_NAME;
        String to_email = Utility.getBuddyID(mcontext);

        Message_Model mm = new Message_Model();
        mm.setMessage_from(from_email);
        mm.setMessage_to(to_email);

        List<Message_Model> message_history = new ArrayList<Message_Model>();
        message_history = db.getMessage_by_id(mm);

        for (int i = 0; i < message_history.size(); i++) {
            Message_Model message_m = message_history.get(i);
            if (message_m.getMessage_from().equals(from_email)) {
                User user = new User("Me", null, true);
                ChatItem item = new ChatItem(user, message_m.getMessage_text());
                adapter.add(item);
            } else {
                User user = new User(to_email, null, false);
                ChatItem item = new ChatItem(user, message_m.getMessage_text());
                adapter.add(item);
            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initChatManager() {

        String groupname = Utility.getBuddyID(mcontext);

        try {

            if (groupname.split("@")[1].contains("conference")) {
                XmppConnectionService.joinMultiUserChat();
                groupchatflag = false;

            } else {
                Log.e("====OUT Group ======", "====OUT Group ======");
                groupchatflag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        XmppConnectionService.setIncomingChatCallback(this);
        XmppConnectionService.listenIncomingChat(groupchatflag);
        XmppConnectionService.get_user_status();
        XmppConnectionService.RoomList();


    }

    @Override
    public void onClick(View view) {
        sendingMessage = edtChat.getText().toString();
        edtChat.setText(null);

        if (sendingMessage.trim().toString().length() > 0) {
            XmppConnectionService.sendMessage(sendingMessage, yes_no_flag, groupchatflag);
        }
    }

    private void showNewMessage(final Chat chat, final Message message) {
        ExecutorManager.getMainExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Log.w("XMPPChat", "received message: " + message.getBody() + " from: " + chat.getParticipant());
                if (message.getBody() != null) {
                    if (message.getSubject() != null) {
                        if (Boolean.parseBoolean(message.getSubject())) {
                            get_yes_no_flag = true;
                            InputMethodManager imm = (InputMethodManager) getSystemService(mcontext.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edtChat.getWindowToken(), 0);
                        } else {
                            get_yes_no_flag = false;
                        }
                    }

                    User user = new User(message.getFrom(), null, false);
                    ChatItem item = new ChatItem(user, message.getBody());
                    adapter.add(item);

                    for (int i = 0; i < XmppConnectionService.All_msg_id.size(); i++) {
                        String id = XmppConnectionService.All_msg_id.get(i);
                        if (message.getStanzaId().equals(id)) {
                            adapter.updateView(i, "recived");
                        }
                    }
                    XmppConnectionService.All_msg_id.add(message.getStanzaId());
                    scrollMyListViewToBottom();
                }
            }
        });
    }


    private void showNewMessage(final Message message) {
        ExecutorManager.getMainExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Log.w("XMPPChat", "received message: " + message.getBody());
                if (message.getBody() != null) {
                    if (message.getSubject() != null) {
                        if (Boolean.parseBoolean(message.getSubject())) {
                            get_yes_no_flag = true;
                            InputMethodManager imm = (InputMethodManager) getSystemService(mcontext.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edtChat.getWindowToken(), 0);
                        } else {
                            get_yes_no_flag = false;
                        }
                    }


                    User user = new User(message.getFrom(), null, false);
                    ChatItem item = new ChatItem(user, message.getBody());
                    adapter.add(item);

                    for (int i = 0; i < XmppConnectionService.All_msg_id.size(); i++) {
                        String id = XmppConnectionService.All_msg_id.get(i);
                        if (message.getStanzaId().equals(id)) {
                            adapter.updateView(i, "recived");
                        }
                    }
                    XmppConnectionService.All_msg_id.add(message.getStanzaId());
                    scrollMyListViewToBottom();
                }
            }
        });
    }

    private void showMyMessage() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {

                try {


                    if (sendingMessage.equals("") || sendingMessage.trim().length() > 0) {

                        if (sendingMessage.contains("http://")) {

                        } else {
                            User user = new User("Me", null, true);
                            ChatItem item = new ChatItem(user, sendingMessage);
                            adapter.add(item);
                            sendingMessage = "";
                            scrollMyListViewToBottom();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showMyMessage(final Message msg) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                User user = new User("Me", null, true);
                ChatItem item = new ChatItem(user, msg.getBody());
                adapter.add(item);
                scrollMyListViewToBottom();
            }
        });
    }

    @Override
    public void onChatArrived(Chat chat, boolean createdLocally) {
        if (createdLocally) {
            showMyMessage();
        } else {
            chat.addMessageListener(new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {

                    if (message.getBody() != null) {
                        String myid = chat.getParticipant();
                        String user_status = parseXMLAndStoreIt(message.toString(), myid);
                        for (int i = 0; i < UserList.userlist.size(); i++) {
                            String user_id = UserList.userlist.get(i).getUser_email();
                            if (myid.split("/")[0].equals(user_id)) {
                                user_list_adapter.updateView_type_state(i, user_status);
                            }
                        }
                        setToast(user_status);
                        showNewMessage(chat, message);
                    }
                }
            });


            chat.addMessageListener(new ChatStateListener() {
                @Override
                public void stateChanged(Chat chat, ChatState state) {
                    Log.e("*******chat*******", "" + chat);
                    Log.e("*******state*******", "" + state);
                }

                @Override
                public void processMessage(Chat chat, Message message) {
                    Log.e("*******chat*******", "" + chat);
                    Log.e("*******message*******", "" + message);
                }
            });

        }
    }

    @Override
    public void onGroupChatMessage(Message message) {

        String values = message.getFrom().split("/")[0];


        if (values.split("/")[0].equals(Utility.getUser_Email(mcontext) + "@" + XmppConnectionService.SERVICE_NAME)) {
            showMyMessage(message);
        } else {
            showNewMessage(message);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (groupchatflag) {
            inflater.inflate(R.menu.menu_user, menu);
        } else {
            inflater.inflate(R.menu.menu_chat, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.action_addmember:
                showInputDialog("change_group_name");
                break;

            case R.id.action_admingrant:
                showInputDialog("change_admin");
                break;

            case R.id.action_ownergrant:
                showInputDialog("change_owner");
                break;

            case R.id.action_remove_admingrant:
                showInputDialog("remove_admin");
                break;

            case R.id.action_remove_ownergrant:
                showInputDialog("remove_owner");
                break;

            case R.id.action_take_image:
                SelectImage(0);
                break;

            case R.id.action_gallery:
                SelectImage(1);
                break;

            case R.id.action_listparticipant:

                Intent ina = new Intent(this, GroupParticiapntList.class);
                startActivity(ina);

                break;

        }
        return true;
    }

    private void SelectImage(int image) {
        if (image == 0) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = new File(android.os.Environment
                    .getExternalStorageDirectory(), "temp.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(intent, 1);
        } else if (image == 1) {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }

    /* on Activity Result */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    /* Take picture from camera */
                    File f = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                    try {
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                        Bitmap bitmap = BitmapFactory.decodeFile(
                                f.getAbsolutePath(), bitmapOptions);

                        finalbitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                bitmap.getWidth(), bitmap.getHeight(), null,
                                true);

                        /*upload_image_change_image
                                .setImageResource(R.drawable.green_upload_photo_box);*/


                        String path = android.os.Environment
                                .getExternalStorageDirectory()
                                + File.separator
                                + "CTSTemp" + File.separator + "default";
                        f.delete();
                        OutputStream outFile = null;
                        File file = new File(path, String.valueOf(System
                                .currentTimeMillis()) + ".jpg");
                        try {
                            outFile = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85,
                                    outFile);
                            outFile.flush();
                            outFile.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new Send_Image().execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (requestCode == 2) {

					/* Select picture from gallery */
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage,
                            filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();

                    finalbitmap = BitmapFactory.decodeFile(picturePath);

                    /*upload_image_change_image
                            .setImageResource(R.drawable.green_upload_photo_box);*/

                    new Send_Image().execute();

                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
                /*upload_image_change_image
                        .setImageResource(R.drawable.upload_photo_box);*/
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        }

    }

    protected void showInputDialog(final String values_compare) {

        LayoutInflater layoutInflater = LayoutInflater.from(ChatActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatActivity.this);
        alertDialogBuilder.setView(promptView);

        final TextView texttitle = (TextView) promptView.findViewById(R.id.textView);

        if (values_compare.equals("change_group_name")) {
            texttitle.setText("Enter Group Name");
        } else if (values_compare.equals("change_admin")) {
            texttitle.setText("Enter Admin Name");
        } else if (values_compare.equals("change_owner")) {
            texttitle.setText("Enter Owner Name");
        } else if (values_compare.equals("remove_admin")) {
            texttitle.setText("Enter Admin Name");
        } else if (values_compare.equals("remove_owner")) {
            texttitle.setText("Enter Owner Name");
        }


        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (values_compare.equals("change_group_name")) {
                            XmppConnectionService.change_groupname(editText.getText().toString());
                        } else if (values_compare.equals("change_admin")) {
                            XmppConnectionService.change_adminGrant(editText.getText().toString());
                        } else if (values_compare.equals("change_owner")) {
                            XmppConnectionService.change_ownerGrant(editText.getText().toString());
                        } else if (values_compare.equals("remove_admin")) {
                            XmppConnectionService.Remove_adminGrant(editText.getText().toString());
                        } else if (values_compare.equals("remove_owner")) {
                            XmppConnectionService.Remove_ownerGrant(editText.getText().toString());
                        }
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

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ChatActivity.this);
            mProgressDialog.setMessage("Download Image...");
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            //image.setImageBitmap(result);
            // Close progressdialog
            mProgressDialog.dismiss();
        }
    }

    public class Send_Image extends AsyncTask<Void, Void, Boolean> {
        String response = "";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(ChatActivity.this);
            dialog.setMessage("Sending Image.....");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                ServiceHandler sh = new ServiceHandler();

                List<NameValuePair> list = new ArrayList<NameValuePair>();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                finalbitmap.compress(Bitmap.CompressFormat.PNG, 100,
                        byteArrayOutputStream);
                byte[] attachmentBytes = byteArrayOutputStream.toByteArray();

                String base64image = Base64.encodeToString(attachmentBytes,
                        Base64.DEFAULT);

                list.add(new BasicNameValuePair("image", base64image));

                response = sh.makeServiceCall(send_image_url, sh.POST, list);

                Log.e("response===>", "" + response);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (response.trim().length() > 0) {
                sendingMessage = response;
                XmppConnectionService.sendMessage(sendingMessage, yes_no_flag, groupchatflag);
            }
        }
    }


    class MyMessageListener implements ChatMessageListener {
        @Override
        public void processMessage(Chat chat, Message message) {

            Log.e("====Message====>", "" + message);
            Log.e("====chat====>", "" + chat);

            String from = message.getFrom();
            String body = message.getBody();
            Log.w("", String.format("Listener Received message '%1$s' from %2$s", body, from));
        }

    }


}
