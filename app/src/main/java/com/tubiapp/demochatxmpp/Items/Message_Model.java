package com.tubiapp.demochatxmpp.Items;

/**
 * Created by webclues on 5/26/2016.
 */
public class Message_Model {

    String message_id;
    String message_from;
    String message_to;
    String message_text;
    String message_time;

    public Message_Model() {

    }

    public Message_Model(String m_id) {
        message_id = m_id;
    }

    public Message_Model(String m_id, String m_from, String m_to, String m_text, String m_time) {
        message_id = m_id;
        message_from = m_from;
        message_to = m_to;
        message_text = m_text;
        message_time = m_time;
    }

    public Message_Model(String m_from, String m_to, String m_text, String m_time) {
        message_from = m_from;
        message_to = m_to;
        message_text = m_text;
        message_time = m_time;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_from() {
        return message_from;
    }

    public void setMessage_from(String message_from) {
        this.message_from = message_from;
    }

    public String getMessage_to() {
        return message_to;
    }

    public void setMessage_to(String message_to) {
        this.message_to = message_to;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getMessage_time() {
        return message_time;
    }

    public void setMessage_time(String message_time) {
        this.message_time = message_time;
    }
}
