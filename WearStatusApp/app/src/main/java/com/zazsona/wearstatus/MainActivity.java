package com.zazsona.wearstatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.zazsona.wearstatus.listeners.GameConnectedListener;
import com.zazsona.wearstatus.listeners.PlayerStatusUpdateListener;
import com.zazsona.wearstatus.listeners.WorldStatusUpdateListener;
import com.zazsona.wearstatus.messages.PlayerStatusMessage;
import com.zazsona.wearstatus.messages.WorldStatusMessage;

import java.net.InetAddress;

public class MainActivity extends WearableActivity
{
    private ImageView mHearts[];
    private ImageView mFood[];
    private ImageView mSky;
    private ConstraintLayout mSearchView;

    private boolean stopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity activityContext = this;

        mSky = findViewById(R.id.skyWheel);
        mSearchView = findViewById(R.id.searchView);

        mHearts = new ImageView[10];
        mHearts[0] = findViewById(R.id.heart1);
        mHearts[1] = findViewById(R.id.heart2);
        mHearts[2] = findViewById(R.id.heart3);
        mHearts[3] = findViewById(R.id.heart4);
        mHearts[4] = findViewById(R.id.heart5);
        mHearts[5] = findViewById(R.id.heart6);
        mHearts[6] = findViewById(R.id.heart7);
        mHearts[7] = findViewById(R.id.heart8);
        mHearts[8] = findViewById(R.id.heart9);
        mHearts[9] = findViewById(R.id.heart10);

        mFood = new ImageView[10];
        mFood[0] = findViewById(R.id.food1);
        mFood[1] = findViewById(R.id.food2);
        mFood[2] = findViewById(R.id.food3);
        mFood[3] = findViewById(R.id.food4);
        mFood[4] = findViewById(R.id.food5);
        mFood[5] = findViewById(R.id.food6);
        mFood[6] = findViewById(R.id.food7);
        mFood[7] = findViewById(R.id.food8);
        mFood[8] = findViewById(R.id.food9);
        mFood[9] = findViewById(R.id.food10);

        WearConnector.getInstance().AddListener(new PlayerStatusUpdateListener()
        {
            @Override
            public void onPlayerStatusUpdated(final PlayerStatusMessage newStatus)
            {
                activityContext.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (newStatus.getHealthChange() < 0)
                        {
                            Vibrator v = (Vibrator) getSystemService(activityContext.getApplicationContext().VIBRATOR_SERVICE);
                            int vibrateTime = Math.min(Math.round(100*(Math.abs(newStatus.getHealthChange()/2))), 1000);
                            v.vibrate(VibrationEffect.createOneShot(vibrateTime, VibrationEffect.DEFAULT_AMPLITUDE));
                        }

                        double heartsValue = Math.ceil(newStatus.getHealth())/2.0f;
                        for (int i = 0; i<mHearts.length; i++)
                        {
                            if (i >= heartsValue)
                                mHearts[i].setImageResource(R.mipmap.empty);
                            else if (i < heartsValue && (i+1) > heartsValue)
                                mHearts[i].setImageResource(R.mipmap.half);
                            else
                                mHearts[i].setImageResource(R.mipmap.full);
                        }

                        double foodValue = Math.ceil(newStatus.getHunger())/2.0f;
                        for (int i = 0; i<mFood.length; i++)
                        {
                            if (i >= foodValue)
                                mFood[i].setImageResource(R.mipmap.food_empty);
                            else if (i < foodValue && (i+1) > foodValue)
                                mFood[i].setImageResource(R.mipmap.food_half);
                            else
                                mFood[i].setImageResource(R.mipmap.food_full);
                        }
                    }
                });
            }
        });

        WearConnector.getInstance().AddListener(new WorldStatusUpdateListener()
        {
            @Override
            public void onWorldStatusUpdated(final WorldStatusMessage newStatus)
            {
                activityContext.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String worldType = newStatus.getWorldType().toUpperCase();
                        if (worldType.contains("OVERWORLD"))
                            mSky.setImageResource(R.mipmap.sky);
                        else if (worldType.contains("THE_NETHER"))
                            mSky.setImageResource(R.mipmap.nether);
                        else if (worldType.contains("THE_END"))
                            mSky.setImageResource(R.mipmap.end);

                        float dayFraction = newStatus.getWorldTime()/24000.0f;
                        float rotation = (360.0f*dayFraction);
                        mSky.setRotation(rotation);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mSearchView.setVisibility(View.VISIBLE);
        final ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(Network network)
            {
                connectivityManager.bindProcessToNetwork(network);

                InetAddress address = null;
                while (address == null && !stopped)
                {
                    address = WearBroadcaster.getInstance().sendBroadcastSearch();
                    System.out.println("B");
                }

                if (address != null && !stopped)        //Messy
                {
                    final InetAddress finalAddress = address;
                    new Thread(() -> WearConnector.getInstance().startConnection(finalAddress, gameAddress -> runOnUiThread(() -> mSearchView.setVisibility(View.INVISIBLE)))).start();
                }
            }
        };
        connectivityManager.requestNetwork(request, networkCallback);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        stopped = true;
        mSearchView.setVisibility(View.VISIBLE);
        new Thread(() ->
                   {
                       WearConnector.getInstance().stopConnection();
                       WearBroadcaster.getInstance().stopBroadcastSearch();
                   }).start();
    }
}
