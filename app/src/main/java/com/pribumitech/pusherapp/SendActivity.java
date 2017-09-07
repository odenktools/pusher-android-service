package com.pribumitech.pusherapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pribumitech.pusherapp.utils.PusherOdk;
import com.pusher.client.channel.PrivateChannel;

import org.json.JSONException;
import org.json.JSONObject;

public class SendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        ActionBar mActionBar = this.getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("Send Message");

        PusherOdk.getInstance().PusherApp.connect();
        final EditText txtMessage = (EditText) findViewById(R.id.txt_message);
        Button btn = (Button) findViewById(R.id.btn_send);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    if (!txtMessage.getText().toString().isEmpty()) {
                        Toast.makeText(SendActivity.this, "Please enter a message...",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    object.put("text", txtMessage.getText().toString());
                    PrivateChannel privateChannel = PusherOdk.getInstance().
                            PusherApp.getPrivateChannel(ApplicationLoader.CHANNEL_NAME);
                    privateChannel.trigger(ApplicationLoader.EVENT_NAME, object.toString());
                    txtMessage.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
