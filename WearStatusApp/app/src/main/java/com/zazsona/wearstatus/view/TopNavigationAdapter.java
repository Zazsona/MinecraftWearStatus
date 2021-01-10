package com.zazsona.wearstatus.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import androidx.wear.widget.drawer.WearableNavigationDrawerView;

import com.zazsona.wearstatus.R;

public class TopNavigationAdapter extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter
{
    private Activity activity;

    public TopNavigationAdapter(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public CharSequence getItemText(int pos)
    {
        DrawerOptions option = DrawerOptions.values()[pos];
        switch (option)
        {
            case PROFILE:
                return "Profile";
            case SETTINGS:
                return "Settings";
            default:
                return "Unknown";
        }
    }

    @Override
    public Drawable getItemDrawable(int pos)
    {
        DrawerOptions option = DrawerOptions.values()[pos];
        switch (option)
        {
            case PROFILE:
                return activity.getDrawable(R.drawable.ic_full_sad);
            case SETTINGS:
                return activity.getDrawable(R.drawable.ic_cc_settings_button_center);
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return DrawerOptions.values().length;
    }

    public enum DrawerOptions
    {
        PROFILE,
        SETTINGS
    }
}
