package com.zazsona.wearstatus.messages;

public class WorldStatusMessage extends Message
{
    public static final String MESSAGE_TYPE = "WORLD_STATUS";
    private String worldType;
    private long worldTime;

    public WorldStatusMessage(String worldType, long worldTime)
    {
        super(MESSAGE_TYPE);
        this.worldType = worldType;
        this.worldTime = worldTime;
    }

    /**
     * Gets worldType
     *
     * @return worldType
     */
    public String getWorldType()
    {
        return worldType;
    }

    /**
     * Gets worldTime
     *
     * @return worldTime
     */
    public long getWorldTime()
    {
        return worldTime;
    }
}
