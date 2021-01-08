package com.zazsona.wearstatus.model.listeners;

import com.zazsona.wearstatus.model.messages.WorldStatusMessage;

public interface WorldStatusUpdateListener
{
    void onWorldStatusUpdated(WorldStatusMessage newStatus);
}
