package ru.e2r7hn07fl47.botcontroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    static SharedPreferences sPref;
    Spinner botSelect;
    TextView statusTextView;
    Button changeStatusButton;
    Button enableAllButton;
    Button disableAllButton;
    ArrayList<String> activeBotsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sPref = getSharedPreferences("ssh_data", Context.MODE_PRIVATE);
        boolean hasData = sPref.getBoolean("hasData", false);

        if (!hasData) {
            startActivity(new Intent(MainActivity.this, ServerLoginActivity.class));
            restart();
        }
        botSelect = findViewById(R.id.botSpinner);
        statusTextView = findViewById(R.id.statusTextView);
        changeStatusButton = findViewById(R.id.changeStatusButton);

        enableAllButton = findViewById(R.id.enableAllButton);
        disableAllButton = findViewById(R.id.disableAllButton);

        enableAllButton.setBackgroundColor(getResources().getColor(R.color.dark_green));
        disableAllButton.setBackgroundColor(getResources().getColor(R.color.dark_red));

        System.setProperty("user.home", getApplicationContext().getApplicationInfo().dataDir);

        SshUtils ssh = new SshUtils();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Integer> botList = ssh.getBotList();
                activeBotsList = ssh.getActiveBotList();

                if (botList.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EmptyBotListDialogFragment fr = new EmptyBotListDialogFragment();
                            FragmentManager manager = getSupportFragmentManager();
                            fr.show(manager, "myDialog");
                        }
                    });
                    return;
                }

                Collections.sort(botList);

                ArrayList<String> botStrList = new ArrayList<>();
                for (int i : botList) {
                    botStrList.add("Bot " + i);
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, botStrList){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView listItem = view.findViewById(android.R.id.text1);
                        listItem.setTextColor(Color.WHITE);
                        return view;
                    }
                };

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        botSelect.setAdapter(adapter);
                        changeStatusButton.setEnabled(true);

                        if (activeBotsList.isEmpty() || (activeBotsList.size() == 1 && activeBotsList.get(0).equals(""))) {
                            enableAllButton.setEnabled(true);
                            enableAllButton.setBackgroundColor(getResources().getColor(R.color.green));
                        } else {
                            disableAllButton.setEnabled(true);
                            disableAllButton.setBackgroundColor(getResources().getColor(R.color.red));
                        }
                    }
                });
            }
        }).start();

        botSelect.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View item, int position, long id) {
                String selectedBot = ((TextView) item).getText().toString().split(" ")[1];
                updateBotsStatus(selectedBot);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatusButton.setEnabled(false);
                String selectedBot = ((String) botSelect.getSelectedItem()).split(" ")[1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (activeBotsList.contains(selectedBot)) {
                            ssh.disableBot(selectedBot);
                            activeBotsList.remove(selectedBot);
                        } else {
                            ssh.enableBot(selectedBot);
                            activeBotsList.add(selectedBot);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateBotsStatus(selectedBot);
                                changeStatusButton.setEnabled(true);
                            }
                        });
                    }
                }).start();
            }
        });

        disableAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllButton.setEnabled(false);
                disableAllButton.setBackgroundColor(getResources().getColor(R.color.dark_red));
                String selectedBot = ((String) botSelect.getSelectedItem()).split(" ")[1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ssh.disableAllBots();
                        activeBotsList = new ArrayList<>();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateBotsStatus(selectedBot);
                                //enableAllButton.setEnabled(true);
                                //enableAllButton.setBackgroundColor(GREEN);
                            }
                        });
                    }
                }).start();

            }
        });

        enableAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.toast_long_method, Toast.LENGTH_LONG).show();
                enableAllButton.setEnabled(false);
                enableAllButton.setBackgroundColor(getResources().getColor(R.color.dark_green));
                botSelect.setEnabled(false);
                changeStatusButton.setEnabled(false);
                changeStatusButton.setBackgroundColor(getResources().getColor(R.color.dark_green));
                String selectedBot = ((String) botSelect.getSelectedItem()).split(" ")[1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ssh.enableAllBots();
                        activeBotsList = ssh.getActiveBotList();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                botSelect.setEnabled(true);
                                changeStatusButton.setEnabled(true);
                                updateBotsStatus(selectedBot);
                                //disableAllButton.setEnabled(true);
                                //disableAllButton.setBackgroundColor(RED);
                            }
                        });
                    }
                }).start();

            }
        });
    }


    private void updateBotsStatus(String selectedBot) {
        if (activeBotsList.isEmpty() || (activeBotsList.size() == 1 && activeBotsList.get(0).equals(""))) {
            enableAllButton.setEnabled(true);
            enableAllButton.setBackgroundColor(getResources().getColor(R.color.green));
            disableAllButton.setEnabled(false);
            disableAllButton.setBackgroundColor(getResources().getColor(R.color.dark_red));
        } else {
            disableAllButton.setEnabled(true);
            disableAllButton.setBackgroundColor(getResources().getColor(R.color.red));
            enableAllButton.setEnabled(false);
            enableAllButton.setBackgroundColor(getResources().getColor(R.color.dark_green));
        }

        String status = "";
        Resources res = getApplicationContext().getResources();
        if (activeBotsList.contains(selectedBot)) {
            status = res.getString(R.string.text_status) + res.getString(R.string.text_status_on);
            changeStatusButton.setBackgroundColor(getResources().getColor(R.color.red));
            changeStatusButton.setText(R.string.button_disable);
        } else {
            status = res.getString(R.string.text_status) + res.getString(R.string.text_status_off);
            changeStatusButton.setBackgroundColor(getResources().getColor(R.color.green));
            changeStatusButton.setText(R.string.button_enable);
        }
        statusTextView.setText(status);
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
                e.commit();
                restart();
                return true;
            case (R.id.exitActionMain):
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void restart() {
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }
}