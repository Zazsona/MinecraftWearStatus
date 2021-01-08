package com.zazsona.wearstatus.model.listeners;
import java.net.InetAddress;

public interface GameConnectionLostListener
{
    void onGameConnectionLost(InetAddress gameAddress);
}
