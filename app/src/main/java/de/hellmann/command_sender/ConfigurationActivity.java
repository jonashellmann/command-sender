package de.hellmann.command_sender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.hellmann.command_sender.configuration.ConfigMenueEntry;
import de.hellmann.command_sender.ssh.CommandConfiguration;

/**
 * Created by hellm on 09.09.2017.
 */

public class ConfigurationActivity extends Activity {

    private List<ConfigMenueEntry> entries;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        listView = (ListView) findViewById(R.id.listView);

        fillListView();

    }

    private void fillListView()
    {

        entries = Arrays.asList(
                new ConfigMenueEntry("Add new host", null),
                new ConfigMenueEntry("Add new command", null));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getTextsOfEntries());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                openActivity(entries.get(position).getClazz());
            }

        });

    }

    private void openActivity(Class<? extends Activity> clazz)
    {

        Intent myIntent = new Intent(ConfigurationActivity.this, clazz);
        ConfigurationActivity.this.startActivity(myIntent);

    }

    private List<String> getTextsOfEntries()
    {

        List<String> texts = new ArrayList<>();

        for(ConfigMenueEntry entry : entries)
        {
            texts.add(entry.getText());
        }

        return texts;

    }

}
