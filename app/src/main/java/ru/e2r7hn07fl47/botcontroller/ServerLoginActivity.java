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

        Button saveButton = findViewById(R.id.saveButton);
        EditText loginEdit = findViewById(R.id.loginEditText);
        EditText passwordEdit = findViewById(R.id.passwordEditText);
        EditText addressEdit = findViewById(R.id.addressEditText);
        EditText portEdit = findViewById(R.id.portEditText);

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
                String address = addressEdit.getText().toString();
                if (address.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.toast_no_address, Toast.LENGTH_LONG).show();
                    return;
                }

                String portString = portEdit.getText().toString();
                if (portString.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.toast_no_port, Toast.LENGTH_LONG).show();
                    return;
                }
                int port = Integer.parseInt(portString);

                Editor e = MainActivity.sPref.edit();
                e.putString("login", login);
                e.putString("password", password);
                e.putString("address", address);
                e.putInt("port", port);
                e.putBoolean("hasData", true);
                e.commit();

                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}