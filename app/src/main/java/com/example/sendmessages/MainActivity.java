package com.example.sendmessages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.messages.Messages;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient = null;
    public static final String TAG = "MyDataMap....";
    public static final String WEARABLE_DATA_PATH = "/wearable/data/path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(Wearable.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        googleApiClient = builder.build();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    public void sendMessages() {
        if(googleApiClient.isConnected()) {
            String message = ((TextView) findViewById(R.id.text)).getText().toString();
            if(message == null || message.equalsIgnoreCase("")) {
                message = "Hello world";
            }

            new SendMessagesToDataLayer(WEARABLE_DATA_PATH, message).start();

        } else {

        }
    }

    public class SendMessagesToDataLayer extends Thread{
        String path;
        String message;

        public SendMessagesToDataLayer(String path, String message) {
            this.path = path;
            this.message = message;
        }

        @Override
        public void run() {
            NodeApi.GetConnectedNodesResult nodeList = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
            for(Node node: nodeList.getNodes()){
                MessageApi.SendMessageResult messageResult = Wearable.MessageApi.sendMessage(
                        googleApiClient,
                        node.getId(),
                        path,
                        message.getBytes()
                ).await();
                if (messageResult.getStatus().isSuccess()) {
                    Log.v(TAG, "Message: Successfully sento  to: " + node.getDisplayName());
                    Log.v(TAG, "Message: Node id is" + getId());
                    Log.v(TAG, "Message: Node size is " + nodeList.getNodes().size());
                } else {
                    Log.v(TAG, "Error while sending Message");
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}