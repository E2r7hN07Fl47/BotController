package ru.e2r7hn07fl47.botcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ServerLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_login);

        Button saveButton = (Button) findViewById(R.id.saveButton);
        EditText loginEdit = (EditText) findViewById(R.id.loginEditText);
        EditText passwordEdit = (EditText) findViewById(R.id.passwordEditText);
        EditText serverEdit = (EditText) findViewById(R.id.serverEditText);
        EditText portEdit = (EditText) findViewById(R.id.portEditText);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginEdit.getText().toString();
                if (login.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.toast_no_login, Toast.LENGTH_LONG).show();
                    return;
                }

                String password = passwordEdit.getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.toast_no_password, Toast.LENGTH_LONG).show();
                    return;
                }
                String server = serverEdit.getText().toString();
                if (server.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.toast_no_login, Toast.LENGTH_LONG).show();
                    return;
                }

                String portString = portEdit.getText().toString();
                if (portString.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.toast_no_password, Toast.LENGTH_LONG).show();
                    return;
                }
                int port = Integer.parseInt(portString);

                Editor e = MainActivity.sPref.edit();
                e.putString("login", login);
                e.putString("password", password);
                e.putString("server", server);
                e.putInt("port", port);
                e.putBoolean("hasData", true);
                e.apply();

                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}