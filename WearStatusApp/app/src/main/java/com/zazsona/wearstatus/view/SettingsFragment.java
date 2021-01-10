package com.zazsona.wearstatus.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.zazsona.wearstatus.R;
import com.zazsona.wearstatus.viewmodel.MainViewModel;

public class SettingsFragment extends Fragment
{
    private ImageView mRotationView;
    private SeekBar mRotationBar;

    private MainViewModel viewModel;

    public SettingsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(MainViewModel.class);
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
        float rotation = viewModel.getSettings().getValue().getRotation();
        if (rotation == 0.0f)
            mRotationBar.setProgress(0);
        else if (rotation == 90.0f)
            mRotationBar.setProgress(1);
        else if (rotation == 180.0f)
            mRotationBar.setProgress(2);
        else if (rotation == 270.0f)
            mRotationBar.setProgress(3);

        mRotationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                if (i == 0)
                    viewModel.getSettings().getValue().setRotation(0.0f);
                else if (i == 1)
                    viewModel.getSettings().getValue().setRotation(90.0f);
                else if (i == 2)
                    viewModel.getSettings().getValue().setRotation(180.0f);
                else if (i == 3)
                    viewModel.getSettings().getValue().setRotation(270.0f);
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

        viewModel.getSettings().observe(this, newSettings ->
        {
            mRotationView.setRotation(newSettings.getRotation());
        });
        super.onViewCreated(view, savedInstanceState);
    }
}