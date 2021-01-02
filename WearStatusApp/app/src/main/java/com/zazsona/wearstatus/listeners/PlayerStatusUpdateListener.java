package com.zazsona.wearstatus.listeners;

import com.zazsona.wearstatus.messages.PlayerStatusMessage;

public interface PlayerStatusUpdateListener
{
    void onPlayerStatusUpdated(PlayerStatusMessage newStatus);
}
