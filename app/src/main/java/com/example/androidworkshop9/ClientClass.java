package com.example.androidworkshop9;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import com.example.androidworkshop9.helpers.Constants;

import java.io.IOException;


public class ClientClass extends Thread
{
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private Handler handler;
    private ProgressBar progressBar;
    private SendReceive sendReceive;

    @SuppressLint("MissingPermission")
    public ClientClass (BluetoothDevice device1, ProgressBar progressBar, Handler handler, SendReceive sendReceive)
    {
        device=device1;
        this.progressBar = progressBar;
        this.handler = handler;
        this.sendReceive = sendReceive;
        try {
            socket=device.createRfcommSocketToServiceRecord(Constants.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void run()
    {
        try {
            socket.connect();
            Message message=Message.obtain();
            message.what= PhotoMainActivity.States.STATE_CONNECTED.ordinal();
            handler.sendMessage(message);

            sendReceive=new SendReceive(socket, progressBar, handler);
            sendReceive.start();

        } catch (IOException e) {
            e.printStackTrace();
            Message message=Message.obtain();
            message.what= PhotoMainActivity.States.STATE_CONNECTION_FAILED.ordinal();
            handler.sendMessage(message);
        }
    }
}
