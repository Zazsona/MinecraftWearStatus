package com.zazsona.wearstatus.view;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import com.zazsona.wearstatus.R;
import com.zazsona.wearstatus.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity
{
    private ConstraintLayout mFragmentLayout;

    private MainViewModel viewModel;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainViewModel.class);
        fragmentManager = getSupportFragmentManager();

        viewModel.isConnected().observe(this, connected ->
        {
            Fragment newFragment = (connected) ? new ProfileFragment() : new ConnectingFragment();
            fragmentManager.beginTransaction().replace(R.id.fragmentLayout, newFragment).commit();
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        viewModel.startMinecraftConnection();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        viewModel.stopMinecraftConnection();
    }
}
