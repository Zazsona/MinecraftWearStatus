package com.zazsona.wearstatus.listeners;
import java.net.InetAddress;

public interface GameConnectedListener
{
    void onGameConnected(InetAddress gameAddress);
}
