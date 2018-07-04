package com.reveautomation.revesmartsecuritykit_online.alluiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.reveautomation.revesmartsecuritykit_online.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectProductActivity extends AppCompatActivity {

    @BindView(R.id.imgDoor)
    ImageView imgDoor;
    @BindView(R.id.img_Pir)
    ImageView imgPir;
    @BindView(R.id.img_Card)
    ImageView imgcard;
    @BindView(R.id.img_Ht)
    ImageView imght;
    @BindView(R.id.imgWindow)
    ImageView imgwindow;
    @BindView(R.id.img_Soil)
    ImageView imgsoil;
    @BindView(R.id.img_Panic)
    ImageView imgpanic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.imgDoor)
    public void onDoorClick(){
        nextActivity(1);
    }
    @OnClick(R.id.img_Pir)
    public void onPirClick(){
        nextActivity(2);
    }
    @OnClick(R.id.img_Card)
    public void onCardClick(){
        nextActivity(3);
    }
    @OnClick(R.id.img_Ht)
    public void onHtClick(){
        nextActivity(4);
    }
    @OnClick(R.id.img_Soil)
    public void onSoilClick(){
        nextActivity(5);
    }
    @OnClick(R.id.imgWindow)
    public void onWindowClick(){
        nextActivity(6);
    }
    @OnClick(R.id.img_Panic)
    public void onPanicClick(){
        nextActivity(7);
    }

    public void nextActivity(int Type){
        Intent intent = new Intent(SelectProductActivity.this, ConfigureSensorActivity.class);
        intent.putExtra("Type",Type);
        startActivity(intent);
        finish();
    }
}
