package com.zazsona.wearstatus.client;

import com.google.gson.Gson;
import com.zazsona.wearstatus.messages.Message;
import com.zazsona.wearstatus.messages.PlayerStatusMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class WearConnector
{
    private static final Logger LOGGER = LogManager.getLogger();
    public final int PORT = 25500;

    private static WearConnector instance;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Thread pingThread;
    private boolean manuallyStopped;
    private Object lock = new Object();

    public static WearConnector getInstance()
    {
        if (instance == null)
            instance = new WearConnector();
        return instance;
    }

    private WearConnector()
    {
        pingThread = new Thread(() -> runPingLoop());
        pingThread.start();
    }

    public void startServer()
    {
        try
        {
            stopServer();
            manuallyStopped = false;
            LOGGER.info("Starting wear server...");
            if (serverSocket == null)
                serverSocket = new ServerSocket(PORT);
            serverSocket.setSoTimeout(0);
            LOGGER.info("Wear server created: "+serverSocket.getInetAddress()+":"+serverSocket.getLocalPort());
            clientSocket = serverSocket.accept();
            LOGGER.info("Wear connected! - "+clientSocket.getInetAddress().getHostName());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(clientSocket.getInputStream());

            ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;
            if (clientPlayerEntity != null)
                sendMessage(new PlayerStatusMessage(clientPlayerEntity.getHealth(), 0.0f, clientPlayerEntity.getMaxHealth(), clientPlayerEntity.getFoodStats().getFoodLevel()));
        }
        catch (SocketException e)
        {
            LOGGER.info("Wear Server socket closed - "+e.getMessage());
        }
        catch (IOException e)
        {
            LOGGER.error("Unable to run Wear Server - "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopServer()
    {
        try
        {
            if (clientSocket != null)
            {
                clientSocket.close();
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            }
            if (serverSocket != null)
                serverSocket.close();
            manuallyStopped = true;
            LOGGER.info("Wear Server was stopped.");
        }
        catch (IOException e)
        {
            LOGGER.error("Could not gracefully stop Wear Server - "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message)
    {
        try
        {
            if (clientSocket != null && outputStream != null && !clientSocket.isClosed())
            {
                synchronized (lock)
                {
                    Gson gson = new Gson();
                    String messageJson = gson.toJson(message);
                    outputStream.writeObject(messageJson);
                    outputStream.flush();
                }
            }
        }
        catch (IOException e)
        {
            if (!manuallyStopped)
            {
                LOGGER.warn("Unable to send message - "+e.getMessage()+"\nResetting connection.");
                stopServer();
                startServer();
            }
        }
    }

    private void runPingLoop()
    {
        try
        {
            while (true)
            {
                Thread.sleep(1500);
                sendMessage(new Message("PING"));
            }
        }
        catch (InterruptedException e)
        {
            LOGGER.info("Ping loop stopped.");
        }
    }
}
