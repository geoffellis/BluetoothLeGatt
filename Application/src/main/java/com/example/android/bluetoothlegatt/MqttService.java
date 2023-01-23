package com.example.android.bluetoothlegatt;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttService extends Service {
    private static final String TAG = "MqttService";
    private MqttAsyncClient client;
    private final String brokerUrl = "tcp://192.168.0.163:1883";
    private final String clientId = "JavaClient";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttAsyncClient(brokerUrl, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            connOpts.setMaxInflight(20);
            connOpts.setAutomaticReconnect(true);

            Log.i(TAG, "Connecting to broker: " + brokerUrl);
            client.connect(connOpts, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "Connected");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e(TAG, "Error connecting to broker: " + exception.getMessage(), exception);
                    }
                });
        } catch (MqttException e) {
            Log.e(TAG, "Error: " + e.getMessage(), e);
        }
    }

    private final IBinder mBinder = new MqttService.LocalBinder();
    public class LocalBinder extends Binder {
        MqttService getService() { return MqttService.this;}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // To ensure that resources are cleaned up properly. Disconnect is
        // invoked when the UI is disconnected from the Service.
        //disconnect();
        return super.onUnbind(intent);
    }

    public void publish(String topic, MqttMessage message) {
        try {
            if (client != null && client.isConnected()) {
                IMqttDeliveryToken token = client.publish(topic, message);
                token.waitForCompletion();
                Log.i(TAG, "Message published to topic: " + topic);
            } else {
                Log.e(TAG, "Client is not connected. Message not published.");
                if(client!=null)
                    client.reconnect();
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error publishing message: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (client != null) {
                client.disconnect().waitForCompletion();
                Log.i(TAG, "Disconnected from broker: " + brokerUrl);
                client.close(true);
                Log.i(TAG, "Close called with force");
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error disconnecting from broker: " + e.getMessage());
        } finally {
            client = null;
        }
    }

    @Override
    public void onDestroy() {
        disconnect();
        super.onDestroy();
    }
}
