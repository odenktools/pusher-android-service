package com.pribumitech.pusherapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pribumitech.pusherapp.services.NetworkInfoReceiver;
import com.pribumitech.pusherapp.utils.PusherOdk;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * https://github.com/pusher/pusher-test-android
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NetworkInfoReceiver.NetworkListener {

    private TextView netStatus;
    private Button btnSend = null;
    private Button btnSendServer = null;
    private Button btnService = null;
    private TextView txtMessage = null;

    private static Gson gson = new Gson();
    private NetworkInfoReceiver networkInfoReceiver = null;

    @Override
    protected void onStop() {
        unregisterReceiver();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    void unregisterReceiver() {
        networkInfoReceiver.unregister(MainActivity.this);
    }

    void registerReceiver() {
        final IntentFilter filterInet = new IntentFilter();
        filterInet.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filterInet.addCategory("com.pribumitech.pusherapp.ApplicationLoader");
        if (networkInfoReceiver == null) {
            networkInfoReceiver = new NetworkInfoReceiver();
            networkInfoReceiver.register(MainActivity.this, filterInet);
            networkInfoReceiver.setNetworkOnChange(MainActivity.this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.netStatus = (TextView) findViewById(R.id.net_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //------------------- NETWORK LISTENERS

        //------------------- NETWORK LISTENERS

        this.btnSend = (Button) findViewById(R.id.btn_send);
        this.btnSendServer = (Button) findViewById(R.id.btn_send_server);
        this.btnService = (Button) findViewById(R.id.btn_service);
        this.txtMessage = (TextView) findViewById(R.id.txt_message);

        /*
          todo "allow publish" from client
          This method only work if your "PUSHER setting" "allow publish" from client
          See your pusher dashboard settings
         */
        this.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerOnlyAllow();
            }
        });

        this.btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnService.getText().toString().equals("START")) {
                    ApplicationLoader.startPusherService();
                    btnService.setText("STOP");
                } else {
                    ApplicationLoader.stopPushService();
                    btnService.setText("START");
                }
            }
        });

        this.btnSendServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trigger();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Direct trigger from android client
     */
    private void triggerOnlyAllow() {
        if (!txtMessage.getText().toString().isEmpty()) {

            /*JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("text", txtMessage.getText().toString());
            String output = gson.toJson(jsonObject);*/

            PusherOdk.getInstance()
                    .PusherApp
                    .getPrivateChannel(ApplicationLoader.CHANNEL_NAME)
                    .trigger(ApplicationLoader.EVENT_NAME, txtMessage.getText().toString());

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Message send successfuly\r\ncheck other side apps OR pusher dashboard");
            builder.setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            txtMessage.setText("");
                        }
                    }).create().show();
        } else {
            Toast.makeText(MainActivity.this, "Please enter a message",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * send trigger to server side
     */
    private void trigger() {

        if (!txtMessage.getText().toString().isEmpty()) {
            //Build json message
            /*JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("text", txtMessage.getText().toString());
            String output = gson.toJson(jsonObject);*/

            final OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("channel_name", ApplicationLoader.CHANNEL_NAME)
                    .add("event_name", ApplicationLoader.EVENT_NAME)
                    .add("socket_id", PusherOdk.getInstance().getConnectionId())
                    .add("message", txtMessage.getText().toString())
                    //.add("message", output)
                    .build();

            Request request = new Request.Builder()
                    .url(ApplicationLoader.PUSHER_END_POINT + ApplicationLoader.TRIGGER_END_POINT)
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    String body = e.getMessage();
                    Log.d("ERROR_REQUEST", body);
                }

                @Override
                public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String body = response.body().string();
                                final Map authResponseMap = gson.fromJson(body, Map.class);
                                final String text = (String) authResponseMap.get("message");

                                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("SERVER_CALLBACK ==> [ " + text + " ]" + "\r\n\r\ncheck other side apps OR pusher dashboard");
                                builder.setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                txtMessage.setText("");
                                            }
                                        }).create().show();
                            } catch (IOException e) {
                                //Log.d("ERROR_PARSE", e.getMessage());
                            }
                        }
                    });
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Please enter a message...",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_login) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_send_msg) {
            Intent i = new Intent(MainActivity.this, SendActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_receive_msg) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    public void updateNetStatus(String connectionType) {

        final boolean connected = connectionType.length() > 0;

        final String text = connected ? "Connected (" + connectionType + ")" : "Disconnected";
        final int bgResource = connected ? R.drawable.rect_green : R.drawable.rect_red;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                netStatus.setBackgroundDrawable(getResources().getDrawable(bgResource));
                netStatus.setText(text);
                netStatus.invalidate();
                btnSend.setEnabled(connected);
            }
        });
    }
}
