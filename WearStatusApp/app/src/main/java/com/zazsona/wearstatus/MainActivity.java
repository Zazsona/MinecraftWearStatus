package com.zazsona.wearstatus;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.zazsona.wearstatus.listeners.PlayerStatusUpdateListener;
import com.zazsona.wearstatus.messages.PlayerStatusMessage;

public class MainActivity extends WearableActivity
{

    private TextView mTextView;
    private ImageView mHearts[];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity activityContext = this;
        mTextView = (TextView) findViewById(R.id.healthText);

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

        // Enables Always-on
        setAmbientEnabled();
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
                        mTextView.setText(String.valueOf(Math.ceil(newStatus.getHealth())));
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

                    }
                });
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        final Context context = this.getApplicationContext();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                WearConnector.getInstance().startConnection(context);
            }
        }).start();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                WearConnector.getInstance().stopConnection();
            }
        }).start();
    }
}
