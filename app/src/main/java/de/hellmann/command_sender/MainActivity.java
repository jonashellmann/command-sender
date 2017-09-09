package de.hellmann.command_sender;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hellmann.command_sender.configuration.ConfigurationActivity;
import de.hellmann.command_sender.database.DatabaseManager;
import de.hellmann.command_sender.ssh.domain.CommandConfiguration;
import de.hellmann.command_sender.ssh.domain.HostConfiguration;
import de.hellmann.command_sender.ssh.SshCommander;

public class MainActivity extends AppCompatActivity {

    private List<CommandConfiguration> commandConfigurations;
    private DatabaseManager databaseManager;
    private TextView textView;
    private ListView listView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.buttonlistView);
        textView = (TextView) findViewById(R.id.outputTextView);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ConfigurationActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        createOrOpenDatabase();
        createListView();

    }

    private void createListView()
    {

        readCommandConfiguration();
        List<String> list = createButtonListFromCommands();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final CommandConfiguration commandConfiguration = commandConfigurations.get(position);

                sendCommand(
                        commandConfiguration.getCommand(),
                        commandConfiguration.getHostConfiguration());
            }

        });

    }

    private List<String> createButtonListFromCommands()
    {

        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < commandConfigurations.size(); i++){
            list.add(commandConfigurations.get(i).getName());
        }
        return list;

    }

    private void sendCommand(String command, HostConfiguration hostConfiguration)
    {

        SshCommander ssh = new SshCommander();
        try{
            List<String> lines =
                    ssh.sendCommandWithKeyAuthentication(
                            command,
                            hostConfiguration);
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : lines)
            {
                stringBuilder.append(s + "\n");
            }
            textView.setText(stringBuilder.toString());
        }
        catch(Exception ex){
            textView.setText(ex.getMessage());
        }
    }

    private void readCommandConfiguration() // TODO: Read from configuration file
    {

        commandConfigurations = new ArrayList<>();

        Cursor commandCursor = databaseManager.runQuery("SELECT * FROM command");

        int commandIndex = commandCursor.getColumnIndex("command");
        int nameIndex = commandCursor.getColumnIndex("name");
        int hostConfigurationIdIndex = commandCursor.getColumnIndex("hostconfiguration_id");

        if (commandCursor.moveToFirst())
        {

            while(!commandCursor.isAfterLast())
            {

                commandConfigurations.add(
                        new CommandConfiguration(
                                commandCursor.getString(commandIndex),
                                commandCursor.getString(nameIndex),
                                //findHostById(commandCursor.getInt(hostConfigurationIdIndex))));
                                null));

                commandCursor.moveToNext();

            }
        }

        commandCursor.close();

        if(commandConfigurations.isEmpty())
        {
            textView.setText("There are no commands configured yet :(");
        }

    }

    private HostConfiguration findHostById(int hostId)
    {
        HostConfiguration hostConfiguration = null;
        Cursor hostCursor = databaseManager.runQuery("SELECT * FROM host WHERE id=" + hostId);

        if(hostCursor.moveToFirst())
        {
            hostConfiguration =
                    new HostConfiguration(
                            hostCursor.getString(hostCursor.getColumnIndex("username")),
                            hostCursor.getString(hostCursor.getColumnIndex("host")),
                            hostCursor.getInt(hostCursor.getColumnIndex("sshPort")),
                            hostCursor.getString(hostCursor.getColumnIndex("privateKeyPath")),
                            hostCursor.getString(hostCursor.getColumnIndex("keyPassphrase")),
                            hostCursor.getString(hostCursor.getColumnIndex("password")));
        }

        return hostConfiguration;
    }

    private void createOrOpenDatabase()
    {

        databaseManager = new DatabaseManager(getApplicationContext());

        databaseManager.executeSql("CREATE TABLE IF NOT EXISTS host (id INT(4) NOT NULL PRIMARY KEY, username VARCHAR NOT NULL, host VARCHAR NOT NULL, sshPort INT(5) NOT NULL, privateKeyPath VARCHAR, keyPassphrase VARCHAR, password VARCHAR)");
        databaseManager.executeSql("CREATE TABLE IF NOT EXISTS command (id INT(4) NOT NULL PRIMARY KEY, command VARCHAR NOT NULL, name VARCHAR NOT NULL, hostconfiguration INT(4) NOT NULL, FOREIGN KEY(hostconfiguration) REFERENCES host(id))");

    }

}
