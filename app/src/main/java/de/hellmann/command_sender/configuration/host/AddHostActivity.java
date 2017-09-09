package de.hellmann.command_sender.configuration.host;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        databaseManager.executeSql(
                String.format(
                        "INSERT INTO host VALUES (%d, '%s', '%s', %d, '%s', '%s', '%s');",
                        databaseManager.findNextId("host", "id"),
                        usernameInput.getText().toString(),
                        hostInput.getText().toString(),
                        Integer.parseInt(sshPortInput.getText().toString()),
                        privateKeyPathInput.getText().toString(),
                        keyPassphraseInput.getText().toString(),
                        passwordInput.getText().toString()));

        goToMainActivity();

    }

    private void goToMainActivity()
    {

        Intent myIntent = new Intent(AddHostActivity.this, MainActivity.class);
        AddHostActivity.this.startActivity(myIntent);

    }

}
