package com.zazsona.wearstatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import com.google.gson.Gson;
import com.zazsona.wearstatus.listeners.PlayerStatusUpdateListener;
import com.zazsona.wearstatus.messages.Message;
import com.zazsona.wearstatus.messages.PlayerStatusMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class WearConnector
{
    public final int PORT = 25500;

    private static WearConnector instance;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean manuallyStopped;
    private ArrayList<PlayerStatusUpdateListener> playerStatusListeners = new ArrayList<>();

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
                connectivityManager.bindProcessToNetwork(network);
                initialiseSocket();
            }
        };
        connectivityManager.requestNetwork(request, networkCallback);
    }

    private void initialiseSocket()
    {
        try
        {
            stopConnection();
            manuallyStopped = false;
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
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.err.println("Unable to run Wear Client - "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void listenForMessages()
    {
        try
        {
            while (!socket.isClosed() && socket != null)
            {
                String json = (String) inputStream.readObject();
                Gson gson = new Gson();
                Message message = gson.fromJson(json, Message.class);
                if (message.getMessageType().equals(PlayerStatusMessage.MESSAGE_TYPE))
                {
                    PlayerStatusMessage playerStatus = new Gson().fromJson(json, PlayerStatusMessage.class);
                    RunListeners(playerStatus);
                }
                else if (message.getMessageType().equals("PING"))
                {
                    //Do nothing.
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("Lost connection to the server - "+e.getMessage());
            e.printStackTrace();
            if (!manuallyStopped)
                initialiseSocket();
        }
    }

    public void stopConnection()
    {
        try
        {
            if (socket != null)
            {
                socket.close();
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                manuallyStopped = true;
                System.out.println("Wear Client was stopped.");
            }
        }
        catch (IOException e)
        {
            System.err.println("Could not gracefully stop Wear Client - "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void AddListener(PlayerStatusUpdateListener handler)
    {
        playerStatusListeners.add(handler);
    }

    private void RunListeners(PlayerStatusMessage playerStatus)
    {
        for (PlayerStatusUpdateListener handler : playerStatusListeners)
            handler.onPlayerStatusUpdated(playerStatus);
    }
}
