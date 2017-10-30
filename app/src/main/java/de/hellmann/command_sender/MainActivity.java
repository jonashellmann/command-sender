package de.hellmann.command_sender;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hellmann.command_sender.configuration.ConfigurationActivity;
import de.hellmann.command_sender.database.DatabaseManager;
import de.hellmann.command_sender.ssh.domain.CommandConfiguration;
import de.hellmann.command_sender.ssh.domain.HostConfiguration;
import de.hellmann.command_sender.ssh.SshCommander;

// TODO Scrollable output view

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_INTERNET = 0;

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

        textView.setMovementMethod(new ScrollingMovementMethod());

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent myIntent = new Intent(MainActivity.this, ConfigurationActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        getPermissionToWriteToExternalStorage();

        createOrOpenDatabase();
        createListView();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    private void getPermissionToWriteToExternalStorage()
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(), "Permission granted to write on external storage", Toast.LENGTH_LONG).show();

                }
            }
        }
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
                    ssh.sendCommand(command, hostConfiguration);
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : lines)
            {
                stringBuilder.append(s + "\n");
            }
            textView.setText(stringBuilder.toString());
            Log.i("State", "No problem");
        }
        catch(Exception ex){
            textView.setText(ex.getMessage());
            Log.i("State", "Problem");
            ex.printStackTrace();
        }
    }

    private void readCommandConfiguration()
    {

        commandConfigurations = new ArrayList<>();

        Cursor commandCursor = databaseManager.runQuery("SELECT * FROM command");

        int commandIndex = commandCursor.getColumnIndex("command");
        int nameIndex = commandCursor.getColumnIndex("name");
        int hostConfigurationIdIndex = commandCursor.getColumnIndex("hostconfiguration");

        if (commandCursor.moveToFirst())
        {

            while(!commandCursor.isAfterLast())
            {

                commandConfigurations.add(
                        new CommandConfiguration(
                                commandCursor.getString(commandIndex),
                                commandCursor.getString(nameIndex),
                                findHostById(commandCursor.getInt(hostConfigurationIdIndex))));

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
