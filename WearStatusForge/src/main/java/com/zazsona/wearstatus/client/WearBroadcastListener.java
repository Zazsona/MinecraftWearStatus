package com.zazsona.wearstatus.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;

public class WearBroadcastListener
{
    private static final Logger LOGGER = LogManager.getLogger();
    public final int PORT = 25501;
    public final String REQUEST_BODY = "WEAR_IDENTIFY_REQUEST";
    public final String RESPONSE_BODY = "WEAR_IDENTIFY_RESPONSE";

    private static WearBroadcastListener instance;
    private DatagramSocket socket;

    public static WearBroadcastListener getInstance()
    {
        if (instance == null)
            instance = new WearBroadcastListener();
        return instance;
    }

    public void startListener()
    {
        try
        {
            socket = new DatagramSocket(PORT);
            byte[] packetData = new byte[REQUEST_BODY.getBytes().length];
            DatagramPacket dp = new DatagramPacket(packetData, packetData.length);
            while (true) //Broken by IOException (E.g, socket closed)
            {
                socket.receive(dp);
                String receivedData = new String(dp.getData());
                if (receivedData.equals(REQUEST_BODY))
                {
                    sendResponse(dp.getAddress(), dp.getPort());
                }
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Broadcast listener stopped: "+e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            socket.close();
        }
    }

    public void stopListener()
    {
        socket.close();
    }

    private void sendResponse(InetAddress target, int port)
    {
        try
        {
            if (socket != null)
            {
                byte[] packetData = RESPONSE_BODY.getBytes();
                DatagramPacket dp = new DatagramPacket(packetData, packetData.length, target, port);
                socket.send(dp);
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Unable to send response to wear identify broadcast: "+e.getMessage());
            e.printStackTrace();
        }
    }

}
