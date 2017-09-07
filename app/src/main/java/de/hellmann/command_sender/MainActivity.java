package de.hellmann.command_sender;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hellmann.command_sender.ssh.CommandConfiguration;
import de.hellmann.command_sender.ssh.HostConfiguration;
import de.hellmann.command_sender.ssh.SshCommander;

public class MainActivity extends AppCompatActivity {

    private List<HostConfiguration> hostConfigurations;
    private List<CommandConfiguration> commandConfigurations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readHostConfiguration();
        createListView();
        /*File file = new File(getFilesDir(), "test.txt");
        final TextView outputTextView = (TextView) findViewById(R.id.outputTextView);
        outputTextView.setText(file.toString());*/
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
        commandConfigurations.add(new CommandConfiguration("echo 'Hello World'", "Print Hello World", null));
        commandConfigurations.add(new CommandConfiguration("ls -la", "Show directory content", null));
    }

}
