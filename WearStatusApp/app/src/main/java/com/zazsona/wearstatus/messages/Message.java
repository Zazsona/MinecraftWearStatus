package com.zazsona.wearstatus.messages;

public class Message
{
    private final String messageType;

    protected Message(String messageType)
    {
        this.messageType = messageType;
    }

    /**
     * Gets messageType
     *
     * @return messageType
     */
    public String getMessageType()
    {
        return messageType;
    }
}
