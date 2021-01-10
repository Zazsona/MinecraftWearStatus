package com.zazsona.wearstatus.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zazsona.wearstatus.R;
import com.zazsona.wearstatus.viewmodel.MainViewModel;

public class ConnectingFragment extends Fragment
{
    private ConstraintLayout mSearchView;
    private MainViewModel viewModel;

    public ConnectingFragment()
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
        return inflater.inflate(R.layout.fragment_connecting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        mSearchView = view.findViewById(R.id.searchView);
        viewModel.getSettings().observe(this, newSettings ->
        {
            mSearchView.setRotation(newSettings.getRotation());
        });
        super.onViewCreated(view, savedInstanceState);
    }
}