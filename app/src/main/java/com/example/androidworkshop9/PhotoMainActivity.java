package com.example.androidworkshop9;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidworkshop9.helpers.Constants;
import com.example.androidworkshop9.helpers.SavePhotoHelper;
import com.example.androidworkshop9.ui.SetSettingsActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class PhotoMainActivity extends AppCompatActivity {

    public enum States {
        STATE_LISTENING,
        STATE_CONNECTING,
        STATE_CONNECTED,
        STATE_CONNECTION_FAILED,
        STATE_MESSAGE_RECEIVED,
        PHOTO_REQUEST_CODE,
        REQUEST_CODE_SETTING_PAGE,
        REQUEST_ENABLE_BLUETOOTH;
    }


    Button btnListen, btnSend, btnListDevices, btnGalery;
    ListView listView;
    TextView status;
    ImageView imageView;
    BluetoothAdapter bluetoothAdapter;
    ProgressBar progressBar;
    BluetoothDevice[] btArray;
    ImageButton imageButton, imageButtonSettings;

    SendReceive sendReceive;

    Bitmap _bitmap;
    Bitmap _bitmapReceived;
    //int QUALITY = 10;

    int QUALITY_SEND;
    int QUALITY_SAVE;
    boolean isPngforSave;
    boolean isPngforSend;
    int subArraySize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_photo_main);
        findViewByIdes();
        init();
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();


        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,States.REQUEST_ENABLE_BLUETOOTH.ordinal());
        }

        implementListeners();
    }

    private void init(){
        QUALITY_SEND = 100;
        QUALITY_SAVE = 100;
        isPngforSave = true;
        isPngforSend = true;
        subArraySize = 400;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 222:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try{
                        SavePhotoHelper savePhotoHelper = new SavePhotoHelper(QUALITY_SAVE, isPngforSave);
                        savePhotoHelper.saveImage(_bitmapReceived, getApplicationContext(), Constants.APP_NAME);
                        Toast.makeText(this.getApplicationContext(), "Pomyślnie zapisano obraz", Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(this.getApplicationContext(), "Odmowa", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } else {
                    Toast.makeText(this.getApplicationContext(), "Odmowa", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK && data != null){
//            Uri selectedImage = data.getData();
//            imageView.setImageURI(selectedImage);
//        }
        if (requestCode == States.PHOTO_REQUEST_CODE.ordinal()){
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    // Get the URI of the selected file
                    final Uri uri = data.getData();
                    useImage(uri);
                }
            }
        }

        if (requestCode == States.REQUEST_CODE_SETTING_PAGE.ordinal()){
            if(resultCode == RESULT_OK){
                if(data != null){
                    QUALITY_SAVE = (int) data.getSerializableExtra("valueForSave");
                    QUALITY_SEND = (int) data.getSerializableExtra("valueForSend");
                    isPngforSave = (boolean) data.getSerializableExtra("isPNGForSave");
                    isPngforSend = (boolean) data.getSerializableExtra("isPNGForSend");
                    subArraySize = (int) data.getSerializableExtra("sizeOfPackage");
                }
            }
        }

    }
    void useImage(Uri uri)
    {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            _bitmap = bitmap;
            //use the bitmap as you like
            imageView.setImageBitmap(bitmap);
        }catch (IOException e){}
    }

    private void implementListeners() {
        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SetSettingsActivity.class);
                intent.putExtra("currentValueOfQualitySend", QUALITY_SEND);
                intent.putExtra("currentValueOfQualitySave", QUALITY_SAVE);
                intent.putExtra("currentValueOfSizeOfPackage", subArraySize);
                intent.putExtra("currentIsPngforSave", isPngforSave);
                intent.putExtra("currentIsPngforSend", isPngforSend);
                startActivityForResult(intent, States.REQUEST_CODE_SETTING_PAGE.ordinal());
            }
        });

        btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, States.PHOTO_REQUEST_CODE.ordinal());
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(_bitmapReceived == null)
                {
                    Toast.makeText(PhotoMainActivity.this, "Nie ma zdjęcia do zapisu", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    SavePhotoHelper savePhotoHelper = new SavePhotoHelper(QUALITY_SAVE, isPngforSave);
                    try {
                        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);
                        } else {
                            savePhotoHelper.saveImage(_bitmapReceived, getApplicationContext(), Constants.APP_NAME);
                            Toast.makeText(PhotoMainActivity.this, "Zapisano zdjęcie, sprawdź swoją galerie.", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception ex){
                        Toast.makeText(PhotoMainActivity.this, "Nie zapisano zdjęcia, sprawdź uprawnienia aplikacji, może wymaga nadania uprawnień. Jeśli nie, funkcja niedostępna, przykro nam :(", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        btnListDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                @SuppressLint("MissingPermission") Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
                String[] strings=new String[bt.size()];
                btArray=new BluetoothDevice[bt.size()];
                int index=0;

                if( bt.size()>0)
                {
                    for(BluetoothDevice device : bt)
                    {
                        btArray[index]= device;
                        strings[index]=device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });



        btnListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass=new ServerClass(bluetoothAdapter, handler, sendReceive, progressBar);
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass=new ClientClass(btArray[i], progressBar, handler, sendReceive);
                clientClass.start();

                status.setText("Connecting");
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(status.getText() != "Connected"){
                    Toast.makeText(PhotoMainActivity.this, "Nie ma połączenia z urządzeniem, nie wyślę", Toast.LENGTH_LONG).show();
                    return;
                }

                if(_bitmap == null){
                    Toast.makeText(PhotoMainActivity.this, "Nie wybrano zdjęcia", Toast.LENGTH_LONG).show();
                    return;
                }


                Bitmap bitmap = _bitmap;
                ByteArrayOutputStream stream=new ByteArrayOutputStream();

                if(isPngforSend){
                    bitmap.compress(Bitmap.CompressFormat.PNG,QUALITY_SEND,stream);
                }else{
                    bitmap.compress(Bitmap.CompressFormat.JPEG,QUALITY_SEND,stream);
                }

                byte[] imageBytes=stream.toByteArray();

                int lenghtPhoto = imageBytes.length;
                sendReceive.write(String.valueOf(lenghtPhoto).getBytes());

                for(int i=0;i<imageBytes.length;i+=subArraySize){
                    byte[] tempArray;
                    tempArray= Arrays.copyOfRange(imageBytes,i,Math.min(imageBytes.length,i+subArraySize));
                    System.out.println(i);
                    sendReceive.write(tempArray);
                }
            }
        });
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            States stan = States.values()[msg.what];
            switch (stan)
            {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    Bitmap bitmap= BitmapFactory.decodeByteArray(readBuff,0,msg.arg1);
                    _bitmapReceived = bitmap;
                    imageView.setImageBitmap(bitmap);
                    break;
            }
            return true;
        }
    });

    private void findViewByIdes() {
        btnGalery = findViewById(R.id.buttonGalery);
        btnListen =(Button) findViewById(R.id.listen);
        btnSend =(Button) findViewById(R.id.send);
        listView=(ListView) findViewById(R.id.listview);
        imageView=(ImageView) findViewById(R.id.imageView);
        status=(TextView) findViewById(R.id.status);
        progressBar = findViewById(R.id.progressBar1);
        imageButton = findViewById(R.id.imageButton1);
        btnListDevices =(Button) findViewById(R.id.listDevices);
        imageButtonSettings = findViewById(R.id.imageButtonSettings);
    }
}