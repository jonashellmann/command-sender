package de.hellmann.command_sender;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hellmann.command_sender.database.DatabaseManager;
import de.hellmann.command_sender.ssh.SshCommander;
import de.hellmann.command_sender.ssh.domain.CommandConfiguration;
import de.hellmann.command_sender.ssh.domain.HostConfiguration;

/**
 * Created by hellm on 17.05.2018.
 */

public class LazyAdapter extends BaseAdapter
{

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    private DatabaseManager databaseManager;
    private List<CommandConfiguration> commandConfigurations;
    private TextView textView;

    public LazyAdapter(
            final Activity activity,
            final ArrayList<HashMap<String, String>> data,
            final DatabaseManager databaseManager,
            final List<CommandConfiguration> commandConfigurations,
            final TextView textView)
    {
        this.activity = activity;
        this.data = data;
        this.databaseManager = databaseManager;
        this.commandConfigurations = commandConfigurations;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.textView = textView;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(
            final int position,
            final View convertView,
            final ViewGroup parent)
    {
        View vi = convertView;
        if(convertView == null) {
            vi = inflater.inflate(R.layout.list_row, null);
        }

        TextView command = (TextView)vi.findViewById(R.id.command);
        TextView info = (TextView)vi.findViewById(R.id.info);

        HashMap<String, String> cmd = new HashMap<>();
        cmd = data.get(position);

        command.setText(cmd.get("command"));
        info.setText(cmd.get("info"));

        TextView executeView = (TextView) vi.findViewById(R.id.command);
        executeView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final CommandConfiguration commandConfiguration = commandConfigurations.get(position);
                sendCommand(
                        commandConfiguration.getCommand(),
                        commandConfiguration.getHostConfiguration());
            }
        });

        Button deleteBtn = (Button) vi.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final CommandConfiguration commandConfiguration = commandConfigurations.get(position);
                databaseManager.deleteCommand(commandConfiguration.getId());
            }
        });

        return vi;
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

}
