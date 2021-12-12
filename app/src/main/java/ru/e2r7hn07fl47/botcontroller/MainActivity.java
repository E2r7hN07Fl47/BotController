package ru.e2r7hn07fl47.botcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    static SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sPref = getSharedPreferences("ssh_data", Context.MODE_PRIVATE);
        boolean hasData = sPref.getBoolean("hasData", false);

        if (!hasData) {
            startActivity(new Intent(MainActivity.this, ServerLoginActivity.class));
        } else {
            String login = sPref.getString("login", "");
            String password = sPref.getString("password", "");
            String server = sPref.getString("server", "");
            int port = sPref.getInt("port", 0);
            Log.d("TEST", login);
            Log.d("TEST", password);
            Log.d("TEST", server);
            Log.d("TEST", String.valueOf(port));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.resetActionMain):
                Editor e = sPref.edit();
                e.putBoolean("hasData", false);
                e.apply();
                recreate();
                return true;
            case (R.id.exitActionMain):
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}