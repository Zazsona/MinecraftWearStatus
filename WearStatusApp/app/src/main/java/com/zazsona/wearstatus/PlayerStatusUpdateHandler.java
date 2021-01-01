package com.zazsona.wearstatus;

import com.zazsona.wearstatus.messages.PlayerStatusMessage;

public interface PlayerStatusUpdateHandler
{
    void onPlayerStatusUpdated(PlayerStatusMessage newStatus);
}
