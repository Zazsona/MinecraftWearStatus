package com.zazsona.wearstatus.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zazsona.wearstatus.model.WearBroadcaster;
import com.zazsona.wearstatus.model.WearConnector;
import com.zazsona.wearstatus.model.listeners.PlayerStatusUpdateListener;
import com.zazsona.wearstatus.model.listeners.WorldStatusUpdateListener;
import com.zazsona.wearstatus.model.messages.PlayerStatusMessage;
import com.zazsona.wearstatus.model.messages.WorldStatusMessage;

import java.net.InetAddress;

public class MainViewModel extends AndroidViewModel
{
    private MutableLiveData<PlayerStatusMessage> playerStatus;
    private MutableLiveData<WorldStatusMessage> worldStatus;
    private MutableLiveData<Boolean> isConnected;
    private boolean isConnectionStopped;

    public MainViewModel(Application application)
    {
        super(application);
        WearConnector.getInstance().AddListener((PlayerStatusUpdateListener) newStatus -> playerStatus.postValue(newStatus));
        WearConnector.getInstance().AddListener((WorldStatusUpdateListener) newStatus -> worldStatus.postValue(newStatus));
        playerStatus = new MutableLiveData<>();
        worldStatus = new MutableLiveData<>();
        isConnected = new MutableLiveData<>();
        isConnected.setValue(false);
    }

    public LiveData<PlayerStatusMessage> getPlayerStatus()
    {
        return playerStatus;
    }

    public LiveData<WorldStatusMessage> getWorldStatus()
    {
        return worldStatus;
    }

    public LiveData<Boolean> isConnected()
    {
        return isConnected;
    }

    public void stopMinecraftConnection()
    {
        isConnectionStopped = true;
        new Thread(() ->
                   {
                       WearConnector.getInstance().stopConnection();
                       WearBroadcaster.getInstance().stopBroadcastSearch();
                   }).start();
    }

    public void startMinecraftConnection()
    {
        stopMinecraftConnection();
        isConnectionStopped = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(Network network)
            {
                connectivityManager.bindProcessToNetwork(network);
                final InetAddress minecraftAddress = getMinecraftInstanceAddress();
                if (minecraftAddress != null && !isConnectionStopped)
                    WearConnector.getInstance().startConnection(minecraftAddress, gameAddress -> isConnected.postValue(true), gameAddress -> isConnected.postValue(false));
            }
        };
        connectivityManager.requestNetwork(request, networkCallback);
    }

    private InetAddress getMinecraftInstanceAddress()
    {
        InetAddress address = null;
        while (address == null && !isConnectionStopped)
            address = WearBroadcaster.getInstance().sendBroadcastSearch();
        return address;
    }
}
