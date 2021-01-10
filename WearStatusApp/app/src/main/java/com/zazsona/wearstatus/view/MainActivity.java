package com.zazsona.wearstatus.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.wear.widget.drawer.WearableNavigationDrawerView;

import com.zazsona.wearstatus.R;
import com.zazsona.wearstatus.view.TopNavigationAdapter.DrawerOptions;
import com.zazsona.wearstatus.viewmodel.MainViewModel;

import static com.zazsona.wearstatus.view.TopNavigationAdapter.DrawerOptions.*;

public class MainActivity extends AppCompatActivity
{
    private MainViewModel viewModel;
    private FragmentManager mFragmentManager;
    private WearableNavigationDrawerView mDrawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainViewModel.class);
        mFragmentManager = getSupportFragmentManager();

        mDrawerView = findViewById(R.id.top_navigation_drawer);
        mDrawerView.setAdapter(new TopNavigationAdapter(this));
        mDrawerView.getController().peekDrawer();
        mDrawerView.addOnItemSelectedListener(pos ->
                                              {
                                                  DrawerOptions option = values()[pos];
                                                  Fragment fragment = null;
                                                  switch (option)
                                                  {
                                                      case PROFILE:
                                                          Fragment overlayFragment = mFragmentManager.findFragmentById(R.id.overlayFragmentLayout);
                                                          mFragmentManager.beginTransaction().remove(overlayFragment).commit();
                                                          break;
                                                      case SETTINGS:
                                                          fragment = new SettingsFragment();
                                                          break;
                                                  }
                                                  if (fragment != null)
                                                      mFragmentManager.beginTransaction().replace(R.id.overlayFragmentLayout, fragment).commit();
                                              });

        viewModel.isConnected().observe(this, connected ->
        {
            Fragment newFragment = (connected) ? new ProfileFragment() : new ConnectingFragment();
            mFragmentManager.beginTransaction().replace(R.id.fragmentLayout, newFragment).commit();
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
