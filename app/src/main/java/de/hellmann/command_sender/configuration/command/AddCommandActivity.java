package de.hellmann.command_sender.configuration.command;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import de.hellmann.command_sender.MainActivity;
import de.hellmann.command_sender.R;
import de.hellmann.command_sender.database.DatabaseManager;

/**
 * Created by hellm on 09.09.2017.
 */

public class AddCommandActivity extends Activity
{

    private DatabaseManager databaseManager;

    private List<Integer> list;

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

        populateSpinner();

    }

    public void saveCommand()
    {
        databaseManager.executeSql(
                String.format(
                        "INSERT INTO command VALUES (%d, '%s', '%s', %s);",
                        databaseManager.findNextId("command", "id"),
                        commandInput.getText().toString(),
                        displayNameInput.getText().toString(),
                        list.get(spinner.getSelectedItemPosition()).toString()));

        //TODO: Show Toast with success or error

        goToMainActivity();

    }

    private void populateSpinner()
    {

        List<String> spinnerList = new ArrayList<>();
        list = new ArrayList<>();
        Cursor cursor = databaseManager.runQuery("SELECT * FROM host");

        if (cursor.moveToFirst())
        {

            while(!cursor.isAfterLast())
            {

                spinnerList.add(
                        cursor.getString(cursor.getColumnIndex("username"))
                                + "@"
                                + cursor.getString(cursor.getColumnIndex("host")));
                list.add(cursor.getInt(cursor.getColumnIndex("id")));

                cursor.moveToNext();

            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void goToMainActivity()
    {

        Intent myIntent = new Intent(AddCommandActivity.this, MainActivity.class);
        AddCommandActivity.this.startActivity(myIntent);

    }

}
