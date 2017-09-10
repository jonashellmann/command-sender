package de.hellmann.command_sender.configuration.host;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import de.hellmann.command_sender.MainActivity;
import de.hellmann.command_sender.R;

/**
 * Created by hellm on 10.09.2017.
 */

public class AddKeyActivity extends Activity
{

    private EditText filenameInput;
    private EditText privateKeyInput;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_key);

        filenameInput = (EditText) findViewById(R.id.editTextFilename);
        privateKeyInput = (EditText) findViewById(R.id.editTextPrivateKey);
        button = (Button) findViewById(R.id.buttonSaveKey);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveKey();
            }
        });

    }

    private void saveKey()
    {

        String filename = filenameInput.getText().toString();
        String privateKey = privateKeyInput.getText().toString();

        if(inputValid(filename, privateKey))
        {

            if(saveKey(filename, privateKey))
            {
                showMessage("Key successfully saved");
            }
            else
            {
                showMessage("Error while saving key");
            }

        }

    }

    private boolean saveKey(String filename, String privateKey)
    {
        FileOutputStream fileOutputStream;

        try
        {
            fileOutputStream = openFileOutput(filename, MODE_PRIVATE);
            fileOutputStream.write(privateKey.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();

            if (keyFileCreated(filename))
            {
                return true;
            }

            return false;
        }
        catch (IOException exception)
        {
            return false;
        }
    }

    private boolean inputValid(String filename, String privateKey)
    {
        if(filename.isEmpty())
        {
            showMessage("Please enter a filename");
            return false;
        }

        if(privateKey.isEmpty())
        {
            showMessage("Please enter a private key");
            return false;
        }

        //TODO Check for open SSH format

        return true;
    }

    private boolean keyFileCreated(String filename)
    {

        try{
            FileInputStream fileInputStream = openFileInput(filename);
            fileInputStream.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }

    }

    private void showMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void goToMainActivity()
    {

        Intent myIntent = new Intent(AddKeyActivity.this, MainActivity.class);
        AddKeyActivity.this.startActivity(myIntent);

    }

}
