package com.example.androidworkshop9;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import com.example.androidworkshop9.helpers.Constants;

import java.io.IOException;

public class ServerClass extends Thread
{
    private BluetoothServerSocket serverSocket;
    BluetoothAdapter bluetoothAdapter;
    Handler handler;
    SendReceive sendReceive;
    ProgressBar progressBar;

    @SuppressLint("MissingPermission")
    public ServerClass(BluetoothAdapter bluetoothAdapter, Handler handler, SendReceive sendReceive, ProgressBar progressBar){
        this.bluetoothAdapter = bluetoothAdapter;
        this.handler = handler;
        this.sendReceive = sendReceive;
        this.progressBar = progressBar;
        try {
            serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(Constants.APP_NAME, Constants.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run()
    {
        BluetoothSocket socket=null;

        while (socket==null)
        {
            try {
                Message message= Message.obtain();
                message.what=PhotoMainActivity.States.STATE_CONNECTING.ordinal();
                handler.sendMessage(message);

                socket=serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what= PhotoMainActivity.States.STATE_CONNECTION_FAILED.ordinal();
                handler.sendMessage(message);
            }

            if(socket!=null)
            {
                Message message=Message.obtain();
                message.what= PhotoMainActivity.States.STATE_CONNECTED.ordinal();
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket, progressBar, handler);
                sendReceive.start();

                break;
            }
        }
    }
}