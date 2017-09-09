package de.hellmann.command_sender.configuration.host;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.hellmann.command_sender.MainActivity;
import de.hellmann.command_sender.R;
import de.hellmann.command_sender.database.DatabaseManager;

/**
 * Created by hellm on 09.09.2017.
 */

public class AddHostActivity extends Activity {

    private DatabaseManager databaseManager;

    private EditText usernameInput;
    private EditText hostInput;
    private EditText sshPortInput;
    private EditText privateKeyPathInput;
    private EditText keyPassphraseInput;
    private EditText passwordInput;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_host);

        databaseManager = new DatabaseManager(getApplicationContext());

        usernameInput = (EditText) findViewById(R.id.editTextUsername);
        hostInput = (EditText) findViewById(R.id.editTextHost);
        sshPortInput = (EditText) findViewById(R.id.editTextSshPort);
        privateKeyPathInput = (EditText) findViewById(R.id.editTextPrivateKeyPath);
        keyPassphraseInput = (EditText) findViewById(R.id.editTextKeyPassphrase);
        passwordInput = (EditText) findViewById(R.id.editTextPassword);
        button = (Button) findViewById(R.id.buttonSaveHost);

        button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                saveHost();

            }

        });

    }

    private void saveHost()
    {

        int id = databaseManager.findNextId("host", "id");
        String username = usernameInput.getText().toString();
        String host = hostInput.getText().toString();
        int sshPort;
        try
        {
            sshPort = Integer.parseInt(sshPortInput.getText().toString());
        }
        catch (NumberFormatException e)
        {
            sshPort = -1;
        }
        String privateKeyPath = privateKeyPathInput.getText().toString();
        String passphrase = keyPassphraseInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (username.isEmpty())
        {
            showMessage("Enter a user name");
            return ;
        }

        if (host.isEmpty() || host.matches("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"))
        {
            showMessage("Enter the IPv4 address of the server");
            return ;
        }

        if (sshPort < 0 || sshPort > 65535)
        {
            showMessage("SSH port must be between 0 and 65535");
            return ;
        }

        databaseManager.executeSql(
                String.format(
                        "INSERT INTO host VALUES (%d, '%s', '%s', %d, '%s', '%s', '%s');",
                        id,
                        username,
                        host,
                        sshPort,
                        privateKeyPath,
                        passphrase,
                        password));

        goToMainActivity();

    }

    private void showMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void goToMainActivity()
    {

        Intent myIntent = new Intent(AddHostActivity.this, MainActivity.class);
        AddHostActivity.this.startActivity(myIntent);

    }

}
