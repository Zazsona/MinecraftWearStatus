package com.zazsona.wearstatus.model;

import java.io.IOException;
import java.net.*;

public class WearBroadcaster
{
    public final int PORT = 25501;
    public final String REQUEST_BODY = "WEAR_IDENTIFY_REQUEST";
    public final String RESPONSE_BODY = "WEAR_IDENTIFY_RESPONSE";

    private static WearBroadcaster instance;
    private DatagramSocket socket;

    public static WearBroadcaster getInstance()
    {
        if (instance == null)
            instance = new WearBroadcaster();
        return instance;
    }

    /**
     * Sends a broadcast message looking for Minecraft versions with the WearStatus mod installed, and returns the first respondent.
     * @return the respondent, or null if there were no replies within the timeout.
     */
    public InetAddress sendBroadcastSearch()
    {
        try
        {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setSoTimeout(2000);
            byte[] packetData = REQUEST_BODY.getBytes();
            DatagramPacket dp = new DatagramPacket(packetData, packetData.length, InetAddress.getByName("255.255.255.255"), PORT);
            socket.send(dp);
            return getBroadcastResponse();
        }
        catch (IOException e)
        {
            System.err.println("Broadcast failed to send: "+e.getMessage());
            e.printStackTrace();
            return null;
        }
        finally
        {
            socket.close();
        }
    }

    private InetAddress getBroadcastResponse()
    {
        try
        {
            if (socket != null)
            {
                byte[] packetData = new byte[RESPONSE_BODY.getBytes().length];
                DatagramPacket dp = new DatagramPacket(packetData, packetData.length);
                socket.receive(dp);
                String receivedData = new String(dp.getData());
                if (receivedData.equals(RESPONSE_BODY))
                    return dp.getAddress();
            }
            return null;
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("Broadcast Request timed out.");
            return null;
        }
        catch (IOException e)
        {
            System.err.println("Unable to acquire response.");
            e.printStackTrace();
            return null;
        }
    }

    public void stopBroadcastSearch()
    {
        if (socket != null)
            socket.close();
    }

}
