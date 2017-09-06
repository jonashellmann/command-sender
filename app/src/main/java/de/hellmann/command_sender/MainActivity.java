package de.hellmann.command_sender;

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

import de.hellmann.command_sender.ssh.SshCommander;

public class MainActivity extends AppCompatActivity {

    private static final String COMMAND = "";
    private static final String USERNAME = "";
    private static final String HOST = "";
    private static final int SSH_PORT = 0;
    private static final String PRIVATE_KEY_PATH = "";
    private static final String KEY_PASSPHRASE = "";

    private Map<Integer, String> commandToButton = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createListView();
    }

    private void createListView()
    {
        final ListView buttonListView = (ListView) findViewById(R.id.buttonlistView);

        ArrayList<String> list = new ArrayList<>();
        list.add("Echo 'Hallo'");
        list.add("Echo folder content");

        mapButtonsWithCommands();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        buttonListView.setAdapter(adapter);
        buttonListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                sendCommand(commandToButton.get(position));
            }

        });
    }

    private void mapButtonsWithCommands() // TODO: Read from configuration file
    {
        commandToButton.put(0, "echo 'Hello World'");
        commandToButton.put(1, "ls -la");
    }

    private void sendCommand(String command)
    {
        final TextView outputTextView = (TextView) findViewById(R.id.outputTextView);

        SshCommander ssh = new SshCommander();
        try{
            List<String> lines =
                    ssh.sendCommandWithKeyAuthentication(
                            COMMAND,
                            USERNAME,
                            HOST,
                            SSH_PORT,
                            PRIVATE_KEY_PATH,
                            KEY_PASSPHRASE);
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
}
