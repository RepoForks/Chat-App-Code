package com.tubiapp.demochatxmpp.service;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tubiapp.demochatxmpp.Database.DatabaseHelper;
import com.tubiapp.demochatxmpp.Items.Message_Model;
import com.tubiapp.demochatxmpp.Items.Paticipant_Model;
import com.tubiapp.demochatxmpp.Items.UserList_Model;
import com.tubiapp.demochatxmpp.R;
import com.tubiapp.demochatxmpp.UserList;
import com.tubiapp.demochatxmpp.adapters.ChatAdapter;
import com.tubiapp.demochatxmpp.adapters.UserList_Adapter;
import com.tubiapp.demochatxmpp.apis.ExecutorManager;
import com.tubiapp.demochatxmpp.utils.Utility;
import com.tubiapp.demochatxmpp.utils.interfaces.IncomingChatCallback;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.iqlast.LastActivityManager;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Created by webclues on 5/23/2016.
 */
public class XmppConnectionService extends Service {
    public static final String SERVICE_NAME = "quorg.in";
    private static final String TAG = "XMPPChatManager";
    private static final String HOST = "119.18.52.175";
    public static ArrayList<String> All_msg_id;
    static AbstractXMPPConnection connection;
    static DatabaseHelper db;
    private static ChatManager chatManager;
    private static MyMessageListener messageListener;
    private static IncomingChatCallback incomingChatCallback;
    private static ChatAdapter mchat_adapter;
    private static Context mcontext;
    private static UserList_Adapter userList_adapter;
    private static Roster roster;
    private static String groupchat_old_stanzaid = "";
    private static MultiUserChatManager multiuserchatmanager;
    private static MultiUserChat multiuserchat;

    public static void setIncomingChatCallback(IncomingChatCallback incomingChatCall) {
        incomingChatCallback = incomingChatCall;
    }

    public static void sendMessage(String sendingMessage, boolean yes_no, boolean groupchatflag) {
        sendMessage(sendingMessage, Utility.getBuddyID(mcontext), yes_no, false);
    }


    public static void sendMessage(String message, String buddyJID, boolean yes_no_flag_set, boolean groupchat_flag) {

        if (!groupchat_flag) {

            Chat chat = chatManager.createChat(buddyJID, messageListener);
            try {
                //ChatStateManager.getInstance(connection).setCurrentState(ChatState.active, chat);
                Message msg = new Message();
                msg.setBody(message);
                msg.setSubject("" + yes_no_flag_set);
                msg.setType(Message.Type.chat);
                Log.e("Message", "----" + msg.toXML().toString());
                chat.sendMessage(msg);
                Calendar cal = Calendar.getInstance();
                String from_email = Utility.getUser_Email(mcontext) + "@" + SERVICE_NAME;
                Log.e("name", from_email);
                Log.e("buddyid", "---" + Utility.getBuddyID(mcontext));
                db.addMessage(new Message_Model(from_email, String.valueOf(msg.getTo()), String.valueOf(msg.getBody()), String.valueOf(cal.getTime())));
                All_msg_id.add(msg.getStanzaId());
                mchat_adapter.updateView(All_msg_id.size(), "not_delivered");

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                //MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
                multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
                Message msg = new Message();
                msg.setBody(message);
                msg.setSubject("" + yes_no_flag_set);
                msg.setType(Message.Type.chat);
                Log.e("buddyJID", "---" + buddyJID);
                multiuserchat.sendMessage(msg);

                Calendar cal = Calendar.getInstance();
                String from_email = Utility.getUser_Email(mcontext) + "@" + SERVICE_NAME;

                Log.e("name", from_email);
                Log.e("buddyid", "---" + Utility.getBuddyID(mcontext));
                Log.e("=====From Email====>", "" + from_email);
                db.addMessage(new Message_Model(from_email, String.valueOf(msg.getTo()), String.valueOf(msg.getBody()), String.valueOf(cal.getTime())));
                All_msg_id.add(msg.getStanzaId());
                mchat_adapter.updateView(All_msg_id.size(), "not_delivered");

                /*multiuserchat.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(Message message) {
                        Log.e("=====message=====>", "" + message);
                    }
                });*/


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFriendEmail() {
       /* if (Utility.getUser_Email(context).contains(Utility.getBuddyID(context))) {
            return Utility.getBuddyID(context) + "@" + SERVICE_NAME;
        } else {*/
        return Utility.getBuddyID(mcontext);
//        }
    }

    public static void listenRosterChange() {

        Roster roster = Roster.getInstanceFor(connection);
        roster.addRosterListener(new RosterListener() {
                                     public void entriesAdded(Collection<String> addresses) {
                                     }

                                     public void entriesDeleted(Collection<String> addresses) {
                                     }

                                     public void entriesUpdated(Collection<String> addresses) {
                                     }

                                     public void presenceChanged(final Presence presence) {
                                         ExecutorManager.getMainExecutor().execute(new Runnable() {
                                             @Override
                                             public void run() {
                                                 String toastMessage = "Presence changed: " + presence.getFrom() + " - " + presence;
                                                 Log.e("===presenceChanged===>", "" + toastMessage);
                                                 // Toast.makeText(mcontext, toastMessage, Toast.LENGTH_LONG).show();

                                                 if (presence.getStatus() != null) {
                                                     if (UserList.userlist != null) {
                                                         for (int i = 0; i < UserList.userlist.size(); i++) {
                                                             String email_id = UserList.userlist.get(i).getUser_email();
                                                             if (presence.getFrom().split("/")[0].equals(email_id)) {
                                                                 userList_adapter.updateView(i, "Online");
                                                             }
                                                         }
                                                     }
                                                 } else {
                                                     if (UserList.userlist != null) {
                                                         for (int i = 0; i < UserList.userlist.size(); i++) {
                                                             String email_id = UserList.userlist.get(i).getUser_email();
                                                             if (presence.getFrom().split("/")[0].equals(email_id)) {
                                                                 userList_adapter.updateView(i, "Offline");
                                                             }
                                                         }
                                                     }
                                                 }
                                             }
                                         });
                                     }
                                 }
        );
    }

    public static void getuser_status() {
        // ArrayList<UserList_Model> userlist = new ArrayList<UserList_Model>();
        final Roster roster = Roster.getInstanceFor(connection);
        if (!roster.isLoaded())
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        Collection<RosterEntry> entries = roster.getEntries();
        Presence presence;
        for (RosterEntry entry : entries) {
            presence = roster.getPresence(entry.getUser());
          /*  Log.e("===Name====>", "" + presence.getType());
            Log.e("===Type-Name====>", "" + presence.getType().name());*/

            if (presence != null) {
                if (presence.getType() == Presence.Type.available) {
                    if (UserList.userlist != null) {
                        for (int i = 0; i < UserList.userlist.size(); i++) {
                            String email_id = UserList.userlist.get(i).getUser_email();
                            if (presence.getFrom().split("/")[0].equals(email_id)) {
                                userList_adapter.updateView(i, "Online");
                            }
                        }
                    }
                } else if (presence.getType() == Presence.Type.unavailable) {
                    if (UserList.userlist != null) {
                        for (int i = 0; i < UserList.userlist.size(); i++) {
                            String email_id = UserList.userlist.get(i).getUser_email();
                            if (presence.getFrom().split("/")[0].equals(email_id)) {
                                userList_adapter.updateView(i, "Offline");
                            }
                        }
                    }
                }
            }
//            Log.e("===Status====>", "" + presence.getStatus());
        }


        //roster.createGroup("Test");

    }

    public static int get_user_status() {

        Presence availability = roster.getPresence(Utility.getBuddyID(mcontext));
        Presence.Mode userMode = availability.getMode();

        Log.e("===userMode====>", "" + userMode);

        int userState = 0;
        /** 0 for offline, 1 for online, 2 for away,3 for busy*/
        if (userMode == Presence.Mode.dnd) {
            userState = 3;
        } else if (userMode == Presence.Mode.away || userMode == Presence.Mode.xa) {
            userState = 2;
        } else if (userMode == Presence.Mode.available) {
            userState = 1;
        }

        Log.e("====userState====>", "" + userState);

        return userState;

    }

    public static ArrayList<UserList_Model> getContactList() {

        ArrayList<UserList_Model> userlist = new ArrayList<UserList_Model>();

        final Roster roster = Roster.getInstanceFor(connection);
        if (!roster.isLoaded())
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

        Collection<RosterEntry> entries = roster.getEntries();
        Presence presence;

        for (RosterEntry entry : entries) {
            presence = roster.getPresence(entry.getUser());
            UserList_Model um = new UserList_Model();
            um.setUser_name(entry.getName());
            um.setUser_email(entry.getUser());

            Log.e("====getNAME===>", "" + entry.getName());
            Log.e("====getUSER===>", "" + entry.getUser());

/*

            Log.e("===User====>", "" + entry.getUser());
            Log.e("===Name====>", "" + entry.getName());
            Log.e("===Name====>", "" + presence.getType());
            Log.e("===Type-Name====>", "" + presence.getType().name());

            if (presence != null) {
                if (presence.getType() == Presence.Type.available) {
                    if (UserList.userlist != null) {
                        for (int i = 0; i < UserList.userlist.size(); i++) {
                            String email_id = UserList.userlist.get(i).getUser_email();
                            if (presence.getFrom().split("/")[0].equals(email_id)) {
                                userList_adapter.updateView(i, "Online");
                            }
                        }
                    }
                } else if (presence.getType() == Presence.Type.unavailable) {
                    if (UserList.userlist != null) {
                        for (int i = 0; i < UserList.userlist.size(); i++) {
                            String email_id = UserList.userlist.get(i).getUser_email();
                            if (presence.getFrom().split("/")[0].equals(email_id)) {
                                userList_adapter.updateView(i, "Offline");
                            }
                        }
                    }
                }
            }
            Log.e("===Status====>", "" + presence.getStatus());
*/

            userlist.add(um);
        }

        return userlist;

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String parseXMLAndStoreIt(String mymsg, String myid) {

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


        } catch (Exception e) {
            e.printStackTrace();
        }


        return msg;

    }

    public static boolean createGroup(String groupname) {
        if (connection == null) {
            return false;
        }

        try {
            //MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

            multiuserchat = multiuserchatmanager.getMultiUserChat(groupname + "@conference." + SERVICE_NAME);

            multiuserchat.create(groupname);

            //muc.grantAdmin(connection.getUser().toString());

            Form form = multiuserchat.getConfigurationForm();
            Form submitForm = form.createAnswerForm();

            List<String> owners = new ArrayList<String>();
            owners.add(connection.getUser().toString());
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            submitForm.setAnswer("muc#roomconfig_roomdesc", groupname);


            multiuserchat.sendConfigurationForm(submitForm);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean join(String group) {
        try {

            //MultiUserChatManager mchatManager = MultiUserChatManager.getInstanceFor(connection);
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            if (!multiuserchat.isJoined()) {
                Log.d("CONNECT", "Joining room !! " + group + " and username " + group);
                boolean createNow = false;
                try {
                    multiuserchat.createOrJoin(group + "@" + SERVICE_NAME);
                    createNow = true;
                } catch (Exception e) {
                    Log.d("CONNECT", "Error while creating the room " + group + e.getMessage());
                }

                if (createNow) {
                    multiuserchat.sendConfigurationForm(new Form(DataForm.Type.submit)); //this is to create the room immediately after join.
                }
            }
            Log.d("CONNECT", "Room created!!");
            return true;
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addGroup(String groupName) {
        try {
            roster.createGroup(groupName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean addUser() {
        try {
            /* roster.createEntry("2@" + SERVICE_NAME, "2", null);
            return true;*/
            //final MultiUserChatManager mchatManager = MultiUserChatManager.getInstanceFor(connection);
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));


            // joinMultiUserChat("2@"+SERVICE_NAME);

           /* chatRoom.invite("2@"+SERVICE_NAME, "hi surya");
            mchatManager.addInvitationListener(new InvitationListener() {
                @Override
                public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
                    System.out.println(" Entered invitation handler... ");
                    try
                    {
                        MultiUserChat chatRoom = mchatManager.getMultiUserChat(inviter);
                        chatRoom.join("2");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    System.out.println(" Invitation Accepted... ");
                }
            });*/
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static MultiUserChat joinMultiUserChat() {
        try {
            //MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);
            multiuserchat.join(connection.getUser().toString(), null, history, SmackConfiguration.getDefaultPacketReplyTimeout());
            System.out.println("The conference room success....");
            return multiuserchat;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("The conference room to fail....");
            return null;
        }
    }

    public static List<HostedRoom> RoomList() {
        List<HostedRoom> roomlist = new ArrayList<HostedRoom>();
        try {
            // MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            roomlist = multiuserchatmanager.getHostedRooms("conference." + SERVICE_NAME);
            /*for (HostedRoom room : roomlist) {
                Log.e("====Room Name====>", "" + room.getName());
                Log.e("====Room JID====>", "" + room.getJid());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return roomlist;
    }

    public static boolean Add_Member(String joinuser) {
        if (connection == null) {
            return false;
        }
        try {
            //MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            // Create a MultiUserChat using an XMPPConnection for a room
            /*Log.e("===Group Link===",""+Utility.getBuddyID(mcontext) + "@conference." + SERVICE_NAME);
            MultiUserChat muc2 = manager.getMultiUserChat(Utility.getBuddyID(mcontext) + "@conference." + SERVICE_NAME);
            muc2.join(joinuser);*/
            // MultiUserChat muc = new MultiUserChat(connection);
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            multiuserchat.create(joinuser);
            multiuserchat.join(joinuser);


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void change_groupname(String groupname) {
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
//            multiuserchat.changeSubject(groupname);

            Form form = multiuserchat.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            submitForm.setAnswer("muc#roomconfig_roomdesc", groupname);
            submitForm.setAnswer("muc#roomconfig_roomname", groupname);
            multiuserchat.sendConfigurationForm(submitForm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void change_adminGrant(String groupname) {
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            multiuserchat.grantAdmin(groupname + "@" + SERVICE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void change_ownerGrant(String groupname) {
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            multiuserchat.grantOwnership(groupname + "@" + SERVICE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Remove_ownerGrant(String groupname) {
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            multiuserchat.revokeOwnership(groupname + "@" + SERVICE_NAME);
            multiuserchat.revokeAdmin(groupname + "@" + SERVICE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Remove_adminGrant(String groupname) {
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            multiuserchat.revokeAdmin(groupname + "@" + SERVICE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Paticipant_Model> Get_AdminList() {
        ArrayList<Paticipant_Model> adminliststring = new ArrayList<Paticipant_Model>();
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            List<Affiliate> adminlist = multiuserchat.getAdmins();
            List<Affiliate> ownerlist = multiuserchat.getOwners();
            List<Occupant> partilist = multiuserchat.getParticipants();

            for (Affiliate affiliate : adminlist) {
                Paticipant_Model pm = new Paticipant_Model();
                pm.setP_name(affiliate.getJid());
                String values = "";
                try {
                    values = affiliate.getRole().toString();
                } catch (Exception e) {
                    values = "";
                }
                pm.setP_role(values);
                adminliststring.add(pm);
            }

            for (Affiliate affiliate : ownerlist) {
                Paticipant_Model pm = new Paticipant_Model();
                pm.setP_name(affiliate.getJid());
                String values = "";
                try {
                    values = affiliate.getRole().toString();
                } catch (Exception e) {
                    values = "";
                }
                pm.setP_role(values);
                adminliststring.add(pm);
            }

            for (Occupant affiliate : partilist) {
                Paticipant_Model pm = new Paticipant_Model();
                pm.setP_name(affiliate.getJid());
                String values = "";
                try {
                    values = affiliate.getRole().toString();
                } catch (Exception e) {
                    values = "";
                }
                pm.setP_role(values);
                adminliststring.add(pm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return adminliststring;

    }

    public static ArrayList<Paticipant_Model> Get_UserList() {
        ArrayList<Paticipant_Model> adminliststring = new ArrayList<Paticipant_Model>();
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            List<Affiliate> adminlist = multiuserchat.getAdmins();
            List<Affiliate> ownerlist = multiuserchat.getOwners();
            List<Occupant> partilist = multiuserchat.getParticipants();

            for (Affiliate affiliate : adminlist) {
                Paticipant_Model pm = new Paticipant_Model();
                pm.setP_name(affiliate.getJid());
                String values = "";
                try {
                    values = affiliate.getAffiliation().toString();
                } catch (Exception e) {
                    values = "";
                }
                pm.setP_role(values);
                adminliststring.add(pm);
            }

            for (Affiliate affiliate : ownerlist) {
                Paticipant_Model pm = new Paticipant_Model();
                pm.setP_name(affiliate.getJid());
                String values = "";
                try {
                    values = affiliate.getAffiliation().toString();
                } catch (Exception e) {
                    values = "";
                }
                pm.setP_role(values);
                adminliststring.add(pm);
            }

            for (Occupant affiliate : partilist) {
                Paticipant_Model pm = new Paticipant_Model();
                pm.setP_name(affiliate.getJid());
                String values = "";
                try {
                    values = affiliate.getAffiliation().toString();
                } catch (Exception e) {
                    values = "";
                }
                pm.setP_role(values);
                adminliststring.add(pm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return adminliststring;

    }

    public static void Get_MemberList() {
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            List<Affiliate> adminlist = multiuserchat.getMembers();

            for (Affiliate affiliate : adminlist) {
                Log.e("===Member=Jid====>", "" + affiliate.getJid());
                Log.e("===Member=Nick====>", "" + affiliate.getNick());
                Log.e("===Member=Affiliation=>", "" + affiliate.getAffiliation());
                Log.e("===Member=Role====>", "" + affiliate.getRole());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> Get_OwnerList() {
        ArrayList<String> adminliststring = new ArrayList<String>();
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            List<Affiliate> adminlist = multiuserchat.getOwners();

            for (Affiliate affiliate : adminlist) {
                adminliststring.add(affiliate.getJid());
                Log.e("===Owner=Jid====>", "" + affiliate.getJid());
                Log.e("===Owner=Nick====>", "" + affiliate.getNick());
                Log.e("===Owner=Affiliation=>", "" + affiliate.getAffiliation());
                Log.e("===Owner=Role====>", "" + affiliate.getRole());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return adminliststring;
    }

    public static ArrayList<String> Get_ParticipantList() {
        ArrayList<String> adminliststring = new ArrayList<String>();
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            List<Occupant> adminlist = multiuserchat.getParticipants();

            for (Occupant affiliate : adminlist) {
                adminliststring.add(affiliate.getJid());
                Log.e("===Participant=Jid====>", "" + affiliate.getJid());
                Log.e("===Participant=Nick=>", "" + affiliate.getNick());
                Log.e("=Participant=Affil=>", "" + affiliate.getAffiliation());
                Log.e("===Participant=Role==>", "" + affiliate.getRole());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return adminliststring;
    }

    public static ArrayList<String> Get_ModeratorList() {
        ArrayList<String> adminliststring = new ArrayList<String>();
        try {
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            List<Occupant> adminlist = multiuserchat.getModerators();
            for (Occupant affiliate : adminlist) {
                adminliststring.add(affiliate.getJid());
                Log.e("===Moderator=Jid====>", "" + affiliate.getJid());
                Log.e("===Moderator=Nick=>", "" + affiliate.getNick());
                Log.e("=Moderator=Affil=>", "" + affiliate.getAffiliation());
                Log.e("===Moderator=Role==>", "" + affiliate.getRole());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adminliststring;
    }

    public static String getUser_online_offline_status(boolean userflag) {

        String values = "";
        if (userflag) {
            Presence presence = roster.getPresence(Utility.getBuddyID(mcontext));
            if (presence.getType() == Presence.Type.available) {
                values = "Online";
            } else {

                long past = getLastSeen(userflag) * 1000;
                long past_diff = Calendar.getInstance().getTimeInMillis() - past;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(past_diff);
                Log.e("======date====>", "" + cal.getTime());

                values = "Last Seen - " + cal.getTime();
            }
        }
        return values;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static void Set_typing_state(ChatState chatstae) {
        Chat chat = chatManager.createChat(Utility.getUser_Email(mcontext) + "@" + SERVICE_NAME);
        try {
            ChatStateManager.getInstance(connection).setCurrentState(chatstae, chat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listenIncomingChat(boolean groupchat) {
        if (!groupchat) {
            // MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            multiuserchat = multiuserchatmanager.getMultiUserChat(Utility.getBuddyID(mcontext));
            multiuserchat.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    if (message.getBody() != null) {
                        String sid = message.getStanzaId();
                        Log.e("=====sid=====>", "" + sid);
                        if (!groupchat_old_stanzaid.equals(sid) || groupchat_old_stanzaid.trim().length() == 0) {
                            groupchat_old_stanzaid = sid;
                            Log.e("****message******", "" + message);

                            String user_on_off = Utility.getUSER_On_Off(mcontext);
                            if (user_on_off.equals("Chat_activity_offline") || user_on_off.equals("Userlist_offline") || user_on_off.length() == 0) {

                                Calendar cal = Calendar.getInstance();
                                db.addMessage_Offline(new Message_Model(String.valueOf(message.getFrom()), String.valueOf(message.getTo()), String.valueOf(message.getBody()), String.valueOf(cal.getTime())));
                                Notify(message.getFrom(), message.getBody());

                            }

                            Calendar cal = Calendar.getInstance();
                            db.addMessage(new Message_Model(String.valueOf(message.getFrom().split("/")[0]), String.valueOf(message.getTo().split("/")[0]), String.valueOf(message.getBody()), String.valueOf(cal.getTime())));


                            if (incomingChatCallback != null) {
                                incomingChatCallback.onGroupChatMessage(message);
                            }
                        }
                    }
                }
            });


            multiuserchat.addSubjectUpdatedListener(new SubjectUpdatedListener() {
                @Override
                public void subjectUpdated(String subject, String from) {
                    Log.e("=====subject======>", "" + subject);
                    Log.e("=====from======>", "" + from);
                }
            });
        } else {
            //ChatManager chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addChatListener(
                    new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally) {
                            // flag = createdLocally;
                            Log.e("====chatCreated====", "====chatCreated====");
                            chat.addMessageListener(new ChatMessageListener() {
                                @Override
                                public void processMessage(Chat chat, Message message) {
                                    String user_on_off = Utility.getUSER_On_Off(mcontext);
                                    if (user_on_off.equals("Chat_activity_offline") || user_on_off.equals("Userlist_offline") || user_on_off.length() == 0) {
                                        if (message.getBody() != null) {
                                            Calendar cal = Calendar.getInstance();
                                            db.addMessage_Offline(new Message_Model(String.valueOf(message.getFrom()), String.valueOf(message.getTo()), String.valueOf(message.getBody()), String.valueOf(cal.getTime())));
                                            Notify(message.getFrom(), message.getBody());
                                        }
                                    }
                                    if (message.getBody() != null) {
                                        Calendar cal = Calendar.getInstance();
                                        db.addMessage(new Message_Model(String.valueOf(message.getFrom().split("/")[0]), String.valueOf(message.getTo().split("/")[0]), String.valueOf(message.getBody()), String.valueOf(cal.getTime())));
                                    }
                                }
                            });

                            Log.e("===IncomingChatCall===>", "" + incomingChatCallback);
                            if (incomingChatCallback != null) {
                                incomingChatCallback.onChatArrived(chat, createdLocally);
                            }
                        }
                    });


        }


    }

    private static void Notify(String notificationTitle, String notificationMessage) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mcontext);

        mBuilder.setContentTitle(notificationTitle.split("/")[0]);
        mBuilder.setContentText(notificationMessage);

        mBuilder.setSmallIcon(R.drawable.chat_noti);

        mBuilder.setNumber(db.getOfflineMessageCount());
        // mBuilder.mNumber = db.getOfflineMessageCount();

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        List<Message_Model> offline_message = db.getAll_OfflineMessage();

        String[] events = new String[db.getOfflineMessageCount()];

        for (int j = 0; j < offline_message.size(); j++) {
            events[j] = offline_message.get(j).getMessage_text();
        }

        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);


        Intent resultIntent = new Intent(mcontext, UserList.class);
        Bundle bundle = new Bundle();
        bundle.putString("User_Email", notificationTitle.split("/")[0]);
        resultIntent.putExtras(bundle);

        //This ensures that navigating backward from the Activity leads out of the app to Home page
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mcontext);

        // Adds the back stack for the Intent
        stackBuilder.addParentStack(UserList.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT //can only be used once
                );
        // start the activity when the user clicks the notification text
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);

        NotificationManager myNotificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);


        myNotificationManager.notify(999, mBuilder.build());


       /* NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")

        Notification notification = new Notification(R.drawable.actionbar_setting_icon, "New Message", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, UserList.class);

        Bundle bundle = new Bundle();
        bundle.putString("User_Email", notificationTitle.split("/")[0]);
        notificationIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(mcontext, notificationTitle, notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);*/
    }

    public static long getLastSeen(boolean groupchatflag) {

        long lastseen = 0;
        if (groupchatflag) {
            try {
                LastActivityManager activity = LastActivityManager.getInstanceFor(connection);
                LastActivity lastactivity = activity.getLastActivity(Utility.getBuddyID(mcontext));
                lastseen = lastactivity.getIdleTime();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
        return lastseen;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mcontext = this;
        initXMPPConnection();
        connectToChatServerAndLogin();
    }

    public void initXMPPConnection() {
        connection = new XMPPTCPConnection(buildXMPPTCPConnectionConfig(Utility.getUser_Email(mcontext), Utility.getUser_Pass(mcontext)));
        SASLAuthentication.registerSASLMechanism(new SASLPlainMechanism());
        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
    }

    private XMPPTCPConnectionConfiguration buildXMPPTCPConnectionConfig(String email, String password) {
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration
                .builder()
                .setHost(HOST);
        configBuilder.setUsernameAndPassword(email, password);
        configBuilder.setServiceName(SERVICE_NAME);
        configBuilder.setDebuggerEnabled(true);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        return configBuilder.build();
    }

    public void connectToChatServerAndLogin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection.connect();

                    initManagersDependOnConnection();
                    connection.login();
                    Delivery_Recipt_Request();

                    listenRosterChange();
                    listenIncomingChat(false);
                    setStatus(true, "I'm come back");


                    UserList.setlistviewdata();

                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();

                }
            }

        }).start();
    }

    public void initManagersDependOnConnection() {

        chatManager = ChatManager.getInstanceFor(connection);
        All_msg_id = new ArrayList<String>();
        mchat_adapter = new ChatAdapter(mcontext);
        userList_adapter = new UserList_Adapter(mcontext);
        messageListener = new MyMessageListener();
        roster = Roster.getInstanceFor(connection);
        multiuserchatmanager = MultiUserChatManager.getInstanceFor(connection);

        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        db = new DatabaseHelper(getApplicationContext());


    }

    public void setlistviewdata() {
        Handler mainHandler = new Handler(mcontext.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

               /* ArrayList<UserList_Model> userdata = new ArrayList<UserList_Model>();
                userdata = getContactList();
                UserList.setvalues(userdata);*/
                //userList_adapter.notifyDataSetChanged();

            }
        };
        mainHandler.post(myRunnable);


    }

    public void Delivery_Recipt_Request() {
        DeliveryReceiptManager.getInstanceFor(connection).setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
        DeliveryReceiptManager.getInstanceFor(connection).getAutoReceiptMode();
        DeliveryReceiptManager.getInstanceFor(connection).addReceiptReceivedListener(new ReceiptReceivedListener() {
            @Override
            public void onReceiptReceived(String fromJid, String toJid, String deliveryReceiptId, Stanza stanza) {

                for (int i = 0; i < All_msg_id.size(); i++) {
                    String id = All_msg_id.get(i);
                    if (deliveryReceiptId.equals(id)) {
                        mchat_adapter.updateView(i, "delivered");

                    }
                }
            }
        });
    }

    public void setStatus(boolean available, String status) {
        Presence.Type type = available ? Presence.Type.available : Presence.Type.unavailable;
        Presence presence = new Presence(type);
        presence.setStatus(status);
        try {
            connection.sendPacket(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }


    public void destroy() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
    }

    public void loadOldMessage() {
//        connection.pre
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        setStatus(false, "Gone fishing");
        destroy();
    }

    class MyMessageListener implements ChatMessageListener {
        @Override
        public void processMessage(Chat chat, Message message) {

            Log.e("====Message====>", "" + message);
            Log.e("====chat====>", "" + chat);

            String from = message.getFrom();
            String body = message.getBody();
            Log.w(TAG, String.format("Listener Received message '%1$s' from %2$s", body, from));
        }

    }


}
