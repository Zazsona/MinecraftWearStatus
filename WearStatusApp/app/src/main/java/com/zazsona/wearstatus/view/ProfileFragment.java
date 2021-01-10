package com.zazsona.wearstatus.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zazsona.wearstatus.R;
import com.zazsona.wearstatus.viewmodel.MainViewModel;

public class ProfileFragment extends Fragment
{
    private ConstraintLayout mProfileRoot;
    private ImageView mHearts[];
    private ImageView mFood[];
    private ImageView mSky;

    private MainViewModel viewModel;

    public ProfileFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mProfileRoot = view.findViewById(R.id.profileLayout);
        mSky = view.findViewById(R.id.skyWheel);
        mHearts = new ImageView[10];
        mHearts[0] = view.findViewById(R.id.heart1);
        mHearts[1] = view.findViewById(R.id.heart2);
        mHearts[2] = view.findViewById(R.id.heart3);
        mHearts[3] = view.findViewById(R.id.heart4);
        mHearts[4] = view.findViewById(R.id.heart5);
        mHearts[5] = view.findViewById(R.id.heart6);
        mHearts[6] = view.findViewById(R.id.heart7);
        mHearts[7] = view.findViewById(R.id.heart8);
        mHearts[8] = view.findViewById(R.id.heart9);
        mHearts[9] = view.findViewById(R.id.heart10);
        mFood = new ImageView[10];
        mFood[0] = view.findViewById(R.id.food1);
        mFood[1] = view.findViewById(R.id.food2);
        mFood[2] = view.findViewById(R.id.food3);
        mFood[3] = view.findViewById(R.id.food4);
        mFood[4] = view.findViewById(R.id.food5);
        mFood[5] = view.findViewById(R.id.food6);
        mFood[6] = view.findViewById(R.id.food7);
        mFood[7] = view.findViewById(R.id.food8);
        mFood[8] = view.findViewById(R.id.food9);
        mFood[9] = view.findViewById(R.id.food10);

        viewModel.getPlayerStatus().observe(this, newStatus ->
        {
            if (newStatus == null)
                return;
            if (newStatus.getHealthChange() < 0)
            {
                Vibrator v = (Vibrator) getActivity().getSystemService(getActivity().getApplicationContext().VIBRATOR_SERVICE);
                int vibrateTime = Math.min(Math.round(100*(Math.abs(newStatus.getHealthChange())))/2, 1000);
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

        viewModel.getSettings().observe(this, newSettings ->
        {
            mProfileRoot.setRotation(newSettings.getRotation());
        });

    }
}