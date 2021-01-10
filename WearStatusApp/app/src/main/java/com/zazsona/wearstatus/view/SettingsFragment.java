package com.zazsona.wearstatus.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.zazsona.wearstatus.R;

public class SettingsFragment extends Fragment
{
    private ImageView mRotationView;
    private SeekBar mRotationBar;

    public SettingsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        mRotationView = view.findViewById(R.id.rotationView);
        mRotationBar = view.findViewById(R.id.rotationBar);

        mRotationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                if (i == 0)
                {
                    mRotationView.setRotation(0.0f);
                }
                else if (i == 1)
                {
                    mRotationView.setRotation(90.0f);
                }
                else if (i == 2)
                {
                    mRotationView.setRotation(180.0f);
                }
                else if (i == 3)
                {
                    mRotationView.setRotation(270.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}