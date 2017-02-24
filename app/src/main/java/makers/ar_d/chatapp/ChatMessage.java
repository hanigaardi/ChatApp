package makers.ar_d.chatapp;

import java.util.Date;

/**
 * Created by Diethel on 2/24/2017.
 */
public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private String photoUrl;


    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.photoUrl = photoUrl;

        messageTime = new Date().getTime();
    }

    public ChatMessage() {
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
