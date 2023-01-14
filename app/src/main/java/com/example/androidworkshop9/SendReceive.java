package com.example.androidworkshop9;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SendReceive extends Thread
{
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private ProgressBar progressBar;
    private Handler handler;

    public SendReceive (BluetoothSocket socket, ProgressBar progressBar, Handler handler)
    {
        this.progressBar = progressBar;
        bluetoothSocket=socket;
        this.handler = handler;
        InputStream tempIn=null;

        OutputStream tempOut=null;

        try {
            tempIn= bluetoothSocket.getInputStream();
            tempOut= bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputStream=  tempIn;
        outputStream= tempOut;
    }


    public void run()
    {

        byte[] buffer = null;
        int numberOfBytes = 0;
        int index=0;
        boolean flag = true;


        while(true)
        {
            if(flag)
            {
                try {
                    byte[] temp = new byte[inputStream.available()];
                    if(inputStream.read(temp)>0)
                    {
                        numberOfBytes=Integer.parseInt(new String(temp, StandardCharsets.UTF_8));
                        buffer=new byte[numberOfBytes];
                        flag=false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {

                    progressBar.setMax(numberOfBytes);
                    progressBar.setProgress(index);

                    int x =inputStream.available();
                    System.out.println("inputStream.available() = " + x);
                    byte[] data=new byte[x];
                    int numbers=inputStream.read(data);
                   /* System.out.println("Numbers: " +numbers);
                    System.out.println("numberOfBytes = " + numberOfBytes);
                    System.out.println("buffer = " +buffer);
                    System.out.println("flag = " +flag);
                    System.out.println("index = " + index);*/

                    /*
                        data - source array, przychodzace dane
                        srcPos - pozycja startowa source array
                        dest - docelowa tablica
                        destPos - startowa pozycja tablicy docelowej
                        lenght - jest liczba elementów tablicy do skopiowania


                        kopiowanie spowodowałoby dostęp do danych poza granicami tablicy.
                     */



                    System.arraycopy(data,0,buffer,index,numbers);
                    index=index+numbers;

                    if(index == numberOfBytes)
                    {
                        progressBar.setProgress(numberOfBytes);
                        handler.obtainMessage(PhotoMainActivity.States.STATE_MESSAGE_RECEIVED.ordinal(),numberOfBytes,-1,buffer).sendToTarget();
                        flag = true;
                        buffer = null;
                        //inputStream.reset();
                        index = 0;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void write(byte[] bytes)
    {
        try {
            outputStream.write(bytes);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}