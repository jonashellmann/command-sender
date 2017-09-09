package de.hellmann.command_sender.configuration.command;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import de.hellmann.command_sender.MainActivity;
import de.hellmann.command_sender.R;
import de.hellmann.command_sender.database.DatabaseManager;

/**
 * Created by hellm on 09.09.2017.
 */

public class AddCommandActivity extends Activity
{

    private DatabaseManager databaseManager;

    private EditText commandInput;
    private EditText displayNameInput;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_command);

        databaseManager = new DatabaseManager(getApplicationContext());

        commandInput = (EditText) findViewById(R.id.editText);
        displayNameInput = (EditText) findViewById(R.id.editText2);
        spinner = (Spinner) findViewById(R.id.spinner);
        Button button = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCommand();
            }
        });

    }

    public void saveCommand()
    {
        databaseManager.executeSql(
                String.format(
                        "INSERT INTO command VALUES (%d, '%s', '%s', 0);",
                        databaseManager.findNextId("command", "id"),
                        commandInput.getText().toString(),
                        displayNameInput.getText().toString()));

        //TODO: Show Toast with success or error

        goToMainActivity();

    }

    private void goToMainActivity()
    {

        Intent myIntent = new Intent(AddCommandActivity.this, MainActivity.class);
        AddCommandActivity.this.startActivity(myIntent);

    }

}
