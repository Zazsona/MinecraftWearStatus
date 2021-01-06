package com.zazsona.wearstatus.listeners;

import com.zazsona.wearstatus.messages.WorldStatusMessage;

public interface WorldStatusUpdateListener
{
    void onWorldStatusUpdated(WorldStatusMessage newStatus);
}
