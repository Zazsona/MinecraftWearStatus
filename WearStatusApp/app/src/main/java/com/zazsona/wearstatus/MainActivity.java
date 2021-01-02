package com.zazsona.wearstatus;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import com.zazsona.wearstatus.listeners.PlayerStatusUpdateListener;
import com.zazsona.wearstatus.messages.PlayerStatusMessage;

public class MainActivity extends WearableActivity
{

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity activityContext = this;
        mTextView = (TextView) findViewById(R.id.healthText);
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
