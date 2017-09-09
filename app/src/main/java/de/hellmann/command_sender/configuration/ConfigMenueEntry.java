package de.hellmann.command_sender.configuration;

import android.app.Activity;
import android.app.Application;

/**
 * Created by hellm on 09.09.2017.
 */

public class ConfigMenueEntry
{

    private String text;
    private Class<? extends Activity> clazz;

    public ConfigMenueEntry(String text, Class<? extends Activity> clazz)
    {
        this.text = text;
        this.clazz = clazz;
    }

    public String getText()
    {
        return text;
    }

    public Class<? extends Activity> getClazz()
    {
        return clazz;
    }

}
