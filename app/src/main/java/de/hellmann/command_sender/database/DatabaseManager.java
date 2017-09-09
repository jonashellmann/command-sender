package de.hellmann.command_sender.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hellm on 09.09.2017.
 */

public class DatabaseManager
{

    private static final String DATABASE_NAME = "SSH";

    private SQLiteDatabase database;

    public DatabaseManager(Context context)
    {
        database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    public void executeSql(String sql)
    {
        database.execSQL(sql);
    }

    public Cursor runQuery(String query)
    {
        return database.rawQuery(query, null);
    }

    public int findNextId(String database, String idColumn)
    {
        Cursor cursor = runQuery("SELECT MAX(" + idColumn + ") AS id FROM " + database + ";");

        if (cursor.moveToFirst())
        {
            return cursor.getInt(cursor.getColumnIndex("id"));
        }

        return 0;
    }

}
