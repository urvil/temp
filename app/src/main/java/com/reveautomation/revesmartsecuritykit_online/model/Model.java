package com.reveautomation.revesmartsecuritykit_online.model;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class Model {

    public static final int TEXT_TYPE=0;
    public static final int DOOR_TYPE=1;
    public static final int PIR_TYPE=2;
    public static final int CARD_TYPE=3;
    public static final int WEATHER_TYPE=4;
    public static final int SOIL_TYPE=5;
    public static final int WINDOWTYPE=6;
    public static final int AUDIO_TYPE=10;
    public static final int IMAGE_TYPE=11;

    public int type;
    public int image;
    public String event;
    public String name;
    public String humidity, temperature;

    public Model(int type,String name, int image)
    {
        this.type=type;
        this.image=image;
        this.name=name;
    }
    public Model(int type,String event, String name, int image)
    {
        this.type=type;
        this.image=image;
        this.event = event;
        this.name=name;
    }
    public Model(int type, String name, String humidity,String temperature)
    {
        this.type=type;
        this.temperature=temperature;
        this.event = event;
        this.name=name;
        this.humidity=humidity;
        this.image=image;
    }

}
