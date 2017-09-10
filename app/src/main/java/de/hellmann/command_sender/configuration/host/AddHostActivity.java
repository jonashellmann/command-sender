package de.hellmann.command_sender.configuration.host;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import de.hellmann.command_sender.MainActivity;
import de.hellmann.command_sender.R;
import de.hellmann.command_sender.database.DatabaseManager;

/**
 * Created by hellm on 09.09.2017.
 */

//TODO Spinner to choose key file

public class AddHostActivity extends Activity {

    private DatabaseManager databaseManager;

    private EditText usernameInput;
    private EditText hostInput;
    private EditText sshPortInput;
    private RadioButton radioButtonPassword;
    private RadioButton radioButtonKey;
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
        radioButtonKey = (RadioButton) findViewById(R.id.radioButtonKey);
        radioButtonPassword = (RadioButton) findViewById(R.id.radioButtonPassword);
        privateKeyPathInput = (EditText) findViewById(R.id.editTextPrivateKeyPath);
        keyPassphraseInput = (EditText) findViewById(R.id.editTextKeyPassphrase);
        passwordInput = (EditText) findViewById(R.id.editTextPassword);
        button = (Button) findViewById(R.id.buttonSaveHost);

        registerListeners();

    }

    private void registerListeners()
    {

        button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                saveHost();
            }

        });

        final TextView textViewPrivateKeyPath = (TextView) findViewById(R.id.textViewPrivateKeyPath);
        final TextView textViewKeyPassphrase = (TextView) findViewById(R.id.textViewKeyPassphrase);
        final TextView textViewPassword = (TextView) findViewById(R.id.textViewPassword);

        radioButtonPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                textViewPrivateKeyPath.setVisibility(View.INVISIBLE);
                privateKeyPathInput.setVisibility(View.INVISIBLE);
                textViewKeyPassphrase.setVisibility(View.INVISIBLE);
                keyPassphraseInput.setVisibility(View.INVISIBLE);

                passwordInput.setVisibility(View.VISIBLE);
                textViewPassword.setVisibility(View.VISIBLE);
            }

        });

        radioButtonKey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                textViewPrivateKeyPath.setVisibility(View.VISIBLE);
                privateKeyPathInput.setVisibility(View.VISIBLE);
                textViewKeyPassphrase.setVisibility(View.VISIBLE);
                keyPassphraseInput.setVisibility(View.VISIBLE);

                passwordInput.setVisibility(View.INVISIBLE);
                textViewPassword.setVisibility(View.INVISIBLE);
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
        File privateKeyDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "command-sender");
        File privateKeyFile = new File(privateKeyDirectory, privateKeyPathInput.getText().toString());
        String privateKeyPath = privateKeyFile.toString();
        String passphrase = keyPassphraseInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (checkInput(
                username,
                host,
                sshPort,
                privateKeyPath,
                passphrase,
                password,
                radioButtonPassword.isChecked(),
                radioButtonKey.isChecked()))
        {

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

            if(hostSuccessfullyCreated(id))
            {
                showMessage("Host successfully created");
                goToMainActivity();
                return ;
            }

            showMessage("Error while creating host");

        }

    }

    private boolean checkInput(
            String username,
            String host,
            int sshPort,
            String privateKeyPath,
            String keyPassphrase,
            String password,
            boolean isPassword,
            boolean isKey)
    {
        if (username.isEmpty())
        {
            showMessage("Please enter a user name");
            return false;
        }

        //TODO: Check IPv4

        if (sshPort < 0 || sshPort > 65535)
        {
            showMessage("Please enter a port between 0 and 65535");
            return false;
        }

        if (isKey)
        {

            if (privateKeyPath.isEmpty())
            {
                showMessage("Please enter the path pointing to the private key");
                return false;
            }

            if(keyPassphrase.isEmpty())
            {
                showMessage("Please enter the key passphrase");
                return false;
            }

        }

        if (isPassword && password.isEmpty())
        {
            showMessage("Please enter a password");
            return false;
        }

        return true;
    }

    private boolean hostSuccessfullyCreated(int hostId)
    {
        return databaseManager.runQuery("SELECT * FROM host WHERE id = " + hostId).moveToFirst();
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
