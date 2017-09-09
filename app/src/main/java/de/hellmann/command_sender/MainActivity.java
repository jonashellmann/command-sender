package de.hellmann.command_sender;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hellmann.command_sender.ssh.CommandConfiguration;
import de.hellmann.command_sender.ssh.HostConfiguration;
import de.hellmann.command_sender.ssh.SshCommander;

public class MainActivity extends AppCompatActivity {

    private List<HostConfiguration> hostConfigurations;
    private List<CommandConfiguration> commandConfigurations;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        establishConnectionToDatabase();
        readHostConfiguration();
        createListView();

    }

    private void createListView()
    {

        final ListView buttonListView = (ListView) findViewById(R.id.buttonlistView);

        readCommandConfiguration();
        List<String> list = createButtonListFromCommands();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        buttonListView.setAdapter(adapter);
        buttonListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

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
        final TextView outputTextView = (TextView) findViewById(R.id.outputTextView);

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
            outputTextView.setText(stringBuilder.toString());
        }
        catch(Exception ex){
            outputTextView.setText(ex.getMessage());
        }
    }

    private void readHostConfiguration()
    {
        // TODO: Read from configuration file
    }

    private void readCommandConfiguration() // TODO: Read from configuration file
    {

        commandConfigurations = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM command", null);

        int commandIndex = cursor.getColumnIndex("command");
        int nameIndex = cursor.getColumnIndex("name");
        int hostConfigurationIdIndex = cursor.getColumnIndex("hostconfiguration_id");

        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                commandConfigurations.add(
                        new CommandConfiguration(
                                cursor.getString(commandIndex),
                                cursor.getString(nameIndex),
                                null)); // TODO Read from other table
                cursor.moveToNext();
            }
        }
        cursor.close();

    }

    private void establishConnectionToDatabase()
    {

        database = openOrCreateDatabase("SSH", MODE_PRIVATE, null);

        database.execSQL("CREATE TABLE IF NOT EXISTS command (id INT(4), command VARCHAR, name VARCHAR, hostconfiguration_id INT(4))");
        database.execSQL("CREATE TABLE IF NOT EXISTS host (id INT(4), username VARCHAR, host VARCHAR, sshPort INT(5), privateKeyPath VARCHAR, keyPassphrase VARCHAR, password VARCHAR)");

    }

}
