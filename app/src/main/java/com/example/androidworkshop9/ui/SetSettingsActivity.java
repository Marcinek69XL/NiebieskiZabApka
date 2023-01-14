package com.example.androidworkshop9.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidworkshop9.R;

public class SetSettingsActivity extends AppCompatActivity {

    Button buttonZatwierdz;
    Switch switchSend, switchSave, switchAdvanced;
    RadioGroup radioGroupSend, radioGroupSave, radioGroupAdvanced;
    TextView textViewAdvanced;
    RadioButton buttonMINSave, buttonMIDSave, buttonHIGHSave, buttonMAXSave;
    RadioButton buttonMINSend, buttonMIDSend, buttonHIGHSend, buttonMAXSend;
    RadioButton button200, button400, button800, button1600, button3200;

    static final int MAX_VALUE = 100;
    static final int MIN_VALUE = 10;
    static final int MID_VALUE = 50;
    static final int HIGH_VALUE = 75;

    int SELECTED_VALUE_SAVE;
    int SELECTED_VALUE_SEND;
    boolean isSelectedPngSave;
    boolean isSelectedPngSend;
    int ADVANCED_VALUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_set_settings);

        init();
        bindValuesFromPhotoMain();
        implementListeners();
        hideSaveComponens();
        hideSendComponens();
        hideAdvancedComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isSelectedPngSave){
            visibleSaveComponens();
        }
        if(!isSelectedPngSend){
            visibleSendComponens();
        }
    }

    private void implementListeners() {
        switchSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchSave.isChecked()){
                    visibleSaveComponens();
                    isSelectedPngSave = false;
                }
                else{
                    hideSaveComponens();
                    isSelectedPngSave = true;
                }
            }
        });

        radioGroupSend.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // get selected radio button from radioGroup
                int selectedId = radioGroupSend.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton selectedRadioButton = (RadioButton)findViewById(selectedId);

                if(selectedRadioButton.getText().toString().equals("Min")){
                    SELECTED_VALUE_SEND = MIN_VALUE;
                }
                else if(selectedRadioButton.getText().toString().equals("Średnia")){
                    SELECTED_VALUE_SEND = MID_VALUE;
                }
                else if(selectedRadioButton.getText().toString().equals("Wysoka")){
                    SELECTED_VALUE_SEND = HIGH_VALUE;
                }
                else if(selectedRadioButton.getText().toString().equals("Max")){
                    SELECTED_VALUE_SEND = MAX_VALUE;
                }
            }
        });

        radioGroupAdvanced.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // get selected radio button from radioGroup
                int selectedId = radioGroupAdvanced.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton selectedRadioButton = (RadioButton)findViewById(selectedId);
                ADVANCED_VALUE =  Integer.parseInt((String) selectedRadioButton.getText());

            }
        });

        radioGroupSave.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // get selected radio button from radioGroup
                int selectedId = radioGroupSave.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton selectedRadioButton = (RadioButton)findViewById(selectedId);

                if(selectedRadioButton.getText().toString().equals("Min")){
                    SELECTED_VALUE_SAVE = MIN_VALUE;
                }
                else if(selectedRadioButton.getText().toString().equals("Średnia")){
                    SELECTED_VALUE_SAVE = MID_VALUE;
                }
                else if(selectedRadioButton.getText().toString().equals("Wysoka")){
                    SELECTED_VALUE_SAVE = HIGH_VALUE;
                }
                else if(selectedRadioButton.getText().toString().equals("Max")){
                    SELECTED_VALUE_SAVE = MAX_VALUE;
                }
            }
        });

        switchAdvanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchAdvanced.isChecked()){
                    visibleAdvancedComponents();
                }
                else{
                    hideAdvancedComponents();
                }
            }
        });

        switchSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchSend.isChecked()){
                    isSelectedPngSend = false;
                    visibleSendComponens();
                }
                else{
                    hideSendComponens();
                    isSelectedPngSend = true;
                }
            }
        });


        buttonZatwierdz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("valueForSave", SELECTED_VALUE_SAVE);
                intent.putExtra("valueForSend", SELECTED_VALUE_SEND);
                intent.putExtra("isPNGForSave", isSelectedPngSave);
                intent.putExtra("isPNGForSend", isSelectedPngSend);
                intent.putExtra("sizeOfPackage", ADVANCED_VALUE);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void visibleSaveComponens(){
        radioGroupSave.setVisibility(View.VISIBLE);
    }
    private void hideSaveComponens(){
        radioGroupSave.setVisibility(View.INVISIBLE);
    }
    private void visibleSendComponens(){
        radioGroupSend.setVisibility(View.VISIBLE);
    }
    private void hideSendComponens(){
        radioGroupSend.setVisibility(View.INVISIBLE);
    }
    private void visibleAdvancedComponents(){
        radioGroupAdvanced.setVisibility(View.VISIBLE);
        textViewAdvanced.setVisibility(View.VISIBLE);
    }
    private void hideAdvancedComponents(){
        radioGroupAdvanced.setVisibility(View.GONE);
        textViewAdvanced.setVisibility(View.GONE);
    }

    private void init(){
        SELECTED_VALUE_SAVE = (int) getIntent().getSerializableExtra("currentValueOfQualitySave");
        SELECTED_VALUE_SEND = (int) getIntent().getSerializableExtra("currentValueOfQualitySend");
        ADVANCED_VALUE = (int) getIntent().getSerializableExtra("currentValueOfSizeOfPackage");
        isSelectedPngSave = (boolean) getIntent().getSerializableExtra("currentIsPngforSave");
        isSelectedPngSend = (boolean) getIntent().getSerializableExtra("currentIsPngforSend");
        buttonZatwierdz = findViewById(R.id.buttonZatwierdz);
        switchSend = findViewById(R.id.switchSend);
        switchSave= findViewById(R.id.switchSave);
        radioGroupSend = findViewById(R.id.radioGroupSend);
        radioGroupSave = findViewById(R.id.radioGroupSave);
        switchAdvanced = findViewById(R.id.switchAdvanced);
        radioGroupAdvanced = findViewById(R.id.radioGroupAdvanced);
        textViewAdvanced = findViewById(R.id.textViewAdvanced);
        buttonMINSave = findViewById(R.id.radioButtonMinSave);
        buttonMIDSave = findViewById(R.id.radioButtonMidSave);
        buttonHIGHSave = findViewById(R.id.radioButtonGoodSave);
        buttonMAXSave = findViewById(R.id.radioButtonMaxSave);
        buttonMINSend = findViewById(R.id.radioButtonMinSend);
        buttonMIDSend = findViewById(R.id.radioButtonMidSend);
        buttonHIGHSend = findViewById(R.id.radioButtonGoodSend);
        buttonMAXSend = findViewById(R.id.radioButtonMaxSend);
        button200 = findViewById(R.id.radioButton200);
        button400 = findViewById(R.id.radioButton400);
        button800 = findViewById(R.id.radioButton800);
        button1600 = findViewById(R.id.radioButton1600);
        button3200 = findViewById(R.id.radioButton3200);
    }

    private void bindValuesFromPhotoMain(){
        if(isSelectedPngSave){
            switchSave.setChecked(false);
        }else{
            switchSave.setChecked(true);
            radioGroupSave.setVisibility(View.VISIBLE);
        }

        if(isSelectedPngSend){
            switchSend.setChecked(false);
        }else{
            switchSend.setChecked(true);
            radioGroupSend.setVisibility(View.VISIBLE);
        }

        switch (SELECTED_VALUE_SAVE){
            case MIN_VALUE: buttonMINSave.setChecked(true); break;
            case MID_VALUE: buttonMIDSave.setChecked(true); break;
            case HIGH_VALUE: buttonHIGHSave.setChecked(true); break;
            case MAX_VALUE: buttonMAXSave.setChecked(true); break;

        }
        switch (SELECTED_VALUE_SEND){
            case MIN_VALUE: buttonMINSend.setChecked(true); break;
            case MID_VALUE: buttonMIDSend.setChecked(true); break;
            case HIGH_VALUE: buttonHIGHSend.setChecked(true); break;
            case MAX_VALUE: buttonMAXSend.setChecked(true); break;
        }

        //Toast.makeText(this, String.valueOf(ADVANCED_VALUE), Toast.LENGTH_SHORT).show();
        switch (ADVANCED_VALUE){
            case 200: button200.setChecked(true); break;
            case 400: button400.setChecked(true); break;
            case 800: button800.setChecked(true); break;
            case 1600: button1600.setChecked(true); break;
            case 3200: button3200.setChecked(true); break;
        }
    }
}