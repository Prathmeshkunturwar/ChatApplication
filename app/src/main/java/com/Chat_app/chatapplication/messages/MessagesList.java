package com.learnoset.chatapplication.messages;

public class MessagesList {

    private final String chatKey, fullName, mobile, lastMessage;
    private int unseenMessages;

    public MessagesList(String chatKey, String fullName, String mobile, String lastMessage, int unseenMessages) {
        this.chatKey = chatKey;
        this.fullName = fullName;
        this.mobile = mobile;
        this.lastMessage = lastMessage;
        this.unseenMessages = unseenMessages;
    }

    public String getChatKey() {
        return chatKey;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
    }
}
