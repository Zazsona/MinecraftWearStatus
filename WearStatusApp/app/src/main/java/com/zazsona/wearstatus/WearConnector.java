package com.zazsona.wearstatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import com.google.gson.Gson;
import com.zazsona.wearstatus.messages.PlayerStatusMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class WearConnector
{
    public final int PORT = 25500;

    private static WearConnector instance;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private ArrayList<PlayerStatusUpdateHandler> handlers = new ArrayList<>();

    public static WearConnector getInstance()
    {
        if (instance == null)
            instance = new WearConnector();
        return instance;
    }

    private WearConnector()
    {
        //Required private constructor
    }

    public void startConnection(Context context)
    {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(Network network)
            {
                try
                {
                    connectivityManager.bindProcessToNetwork(network);
                    System.out.println("Starting connection...");
                    socket = new Socket("192.168.1.254", PORT);
                    System.out.println("Connected!");
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.flush();
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    listenForMessages();
                }
                catch (SocketException e)
                {
                    System.err.println("Wear Client socket closed - "+e.getMessage());
                }
                catch (IOException e)
                {
                    System.err.println("Unable to run Wear Client - "+e.getMessage());
                    e.printStackTrace();
                }

            }
        };
        connectivityManager.requestNetwork(request, networkCallback);
    }

    private void listenForMessages()
    {
        try
        {
            while (socket.isConnected() && socket != null)
            {
                String json = (String) inputStream.readObject();
                PlayerStatusMessage playerStatus = new Gson().fromJson(json, PlayerStatusMessage.class);
                RunHandlers(playerStatus);
                System.out.println("Got update!");
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("Lost connection to the server - "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopConnection()
    {
        try
        {
            socket.close();
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            System.out.println("Wear Client was stopped.");
        }
        catch (IOException e)
        {
            System.err.println("Could not gracefully stop Wear Client - "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void AddHandler(PlayerStatusUpdateHandler handler)
    {
        handlers.add(handler);
    }

    public void RunHandlers(PlayerStatusMessage playerStatus)
    {
        for (PlayerStatusUpdateHandler handler : handlers)
            handler.onPlayerStatusUpdated(playerStatus);
    }
}
