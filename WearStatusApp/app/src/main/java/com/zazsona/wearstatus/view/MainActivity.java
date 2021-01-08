package com.zazsona.wearstatus.view;

import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import com.zazsona.wearstatus.R;
import com.zazsona.wearstatus.model.listeners.PlayerStatusUpdateListener;
import com.zazsona.wearstatus.model.listeners.WorldStatusUpdateListener;
import com.zazsona.wearstatus.model.messages.PlayerStatusMessage;
import com.zazsona.wearstatus.model.messages.WorldStatusMessage;
import com.zazsona.wearstatus.model.WearConnector;
import com.zazsona.wearstatus.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity
{
    private ImageView mHearts[];
    private ImageView mFood[];
    private ImageView mSky;
    private ConstraintLayout mSearchView;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainViewModel.class);
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

        viewModel.getPlayerStatus().observe(this, newStatus ->
        {
            if (newStatus == null)
                return;
            if (newStatus.getHealthChange() < 0)
            {
                Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
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
        });

        viewModel.getWorldStatus().observe(this, newStatus ->
        {
            if (newStatus == null)
                return;
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
        });

        viewModel.isConnected().observe(this, newValue ->
        {
            if (newValue)
                mSearchView.setVisibility(View.INVISIBLE);
            else
            {
                mSearchView.setVisibility(View.VISIBLE);
                viewModel.startMinecraftConnection();
            }

        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mSearchView.setVisibility(View.VISIBLE);
        viewModel.startMinecraftConnection();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mSearchView.setVisibility(View.VISIBLE);
        viewModel.stopMinecraftConnection();
    }
}
