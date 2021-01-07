package com.zazsona.wearstatus.listeners;
import java.net.InetAddress;

public interface GameConnectionLostListener
{
    void onGameConnectionLost(InetAddress gameAddress);
}
