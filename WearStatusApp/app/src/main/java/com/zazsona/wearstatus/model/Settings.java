package com.zazsona.wearstatus.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.zazsona.wearstatus.model.listeners.SettingsUpdateListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;

public class Settings
{
    private static transient final String fileName = "Settings.json";
    private static transient Settings instance;
    private transient Context context;
    private ArrayList<SettingsUpdateListener> listeners;
    @Expose
    private float rotation;

    public static Settings get(Context context)
    {
        if (instance == null)
            instance = new Settings(context);
        return instance;
    }

    private Settings(Context context)
    {
        this.context = context;
        listeners = new ArrayList<>();
        load();
    }

    public void save()
    {
        try
        {
            File file = new File(context.getFilesDir(), fileName);
            if (!file.exists())
            {
                file.createNewFile();
            }
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String json = gson.toJson(this);
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(json);
            pw.close();
            fos.close();
            runListeners();
        }
        catch (IOException e)
        {
            Log.e(this.getClass().getName(), "An error occurred when saving settings.", e);
        }
    }

    public void load()
    {
        try
        {
            File file = new File(context.getFilesDir(), fileName);
            if (file.exists())
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = new String(Files.readAllBytes(file.toPath()));
                Settings settingsFromFile = gson.fromJson(json, Settings.class);

                setRotation(settingsFromFile.getRotation());
            }
            else
            {
                setRotation(0.0f);
            }
        }
        catch (IOException e)
        {
            Log.e(this.getClass().getName(), "An error occurred when restoring settings.", e);
            return;
        }
    }

    public float getRotation()
    {
        return rotation;
    }

    public void setRotation(float rotation)
    {
        this.rotation = rotation;
        save();
    }

    private void runListeners()
    {
        for (SettingsUpdateListener listener : listeners)
            listener.onSettingsUpdated(this);
    }

    public void addListener(SettingsUpdateListener listener)
    {
        listeners.add(listener);
    }

    public void removeListener(SettingsUpdateListener listener)
    {
        listeners.remove(listener);
    }
}
