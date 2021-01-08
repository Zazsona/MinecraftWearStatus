package com.zazsona.wearstatus.model.listeners;

import com.zazsona.wearstatus.model.messages.PlayerStatusMessage;

public interface PlayerStatusUpdateListener
{
    void onPlayerStatusUpdated(PlayerStatusMessage newStatus);
}
