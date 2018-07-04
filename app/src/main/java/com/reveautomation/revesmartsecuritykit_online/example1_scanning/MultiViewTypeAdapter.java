package com.reveautomation.revesmartsecuritykit_online.example1_scanning;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reveautomation.revesmartsecuritykit_online.R;
import com.reveautomation.revesmartsecuritykit_online.model.Model;

import java.util.ArrayList;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class MultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Model> dataSet;
    Context mContext;
    int total_types;
    MediaPlayer mPlayer;
    private boolean fabStateVolume = false;

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder {

        TextView txtType;
        CardView cardView;

        public TextTypeViewHolder(View itemView) {
            super(itemView);
            this.txtType = (TextView) itemView.findViewById(R.id.tv_name);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
        }

    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        TextView txtType;
        ImageView image;
        public ImageTypeViewHolder(View itemView) {
            super(itemView);
            this.txtType = (TextView) itemView.findViewById(R.id.tv_name);
            this.image = (ImageView) itemView.findViewById(R.id.background);
        }
    }

    public static class ThreeTypeViewHolder extends RecyclerView.ViewHolder {
        TextView txtType,txtEvent;
        ImageView image;
        public ThreeTypeViewHolder(View itemView) {
            super(itemView);
            this.txtType = (TextView) itemView.findViewById(R.id.tv_name);
            this.txtEvent = (TextView) itemView.findViewById(R.id.tv_event);
            this.image = (ImageView) itemView.findViewById(R.id.background);
        }
    }
    public static class PirTypeViewHolder extends RecyclerView.ViewHolder {
        TextView txtType;
        ImageView image;
        public PirTypeViewHolder(View itemView) {
            super(itemView);
            this.txtType = (TextView) itemView.findViewById(R.id.tv_name);
            this.image = (ImageView) itemView.findViewById(R.id.background);
        }
    }
    public static class CardTypeViewHolder extends RecyclerView.ViewHolder {
        TextView txtType;
        ImageView image;
        public CardTypeViewHolder(View itemView) {
            super(itemView);
            this.txtType = (TextView) itemView.findViewById(R.id.tv_name);
            this.image = (ImageView) itemView.findViewById(R.id.background);
        }
    }
    public static class SoilTypeViewHolder extends RecyclerView.ViewHolder {
        TextView txtType,txtEvent;
        public SoilTypeViewHolder(View itemView) {
            super(itemView);
            this.txtType = (TextView) itemView.findViewById(R.id.tv_name);
            this.txtEvent = (TextView) itemView.findViewById(R.id.tv_event);
        }
    }
    public static class Weather_Type extends RecyclerView.ViewHolder {
        TextView txtType,txtHumi,txtTemp;
        public Weather_Type(View itemView) {
            super(itemView);
            this.txtType = (TextView) itemView.findViewById(R.id.tv_name);
            this.txtHumi = (TextView) itemView.findViewById(R.id.tvhumi);
            this.txtTemp = (TextView) itemView.findViewById(R.id.tvtemp);
        }
    }
    public static class AudioTypeViewHolder extends RecyclerView.ViewHolder {
        TextView txtType;
        FloatingActionButton fab;
        public AudioTypeViewHolder(View itemView) {
            super(itemView);
            this.txtType = (TextView) itemView.findViewById(R.id.tv_name);
            this.fab = (FloatingActionButton) itemView.findViewById(R.id.fab);
        }
    }

    public MultiViewTypeAdapter(ArrayList<Model> data, Context context) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case Model.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_type, parent, false);
                return new TextTypeViewHolder(view);
            case Model.DOOR_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_door, parent, false);
                return new ThreeTypeViewHolder(view);
            case Model.WINDOWTYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_door, parent, false);
                return new ThreeTypeViewHolder(view);
            case Model.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_type, parent, false);
                return new ImageTypeViewHolder(view);
            case Model.PIR_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_pir, parent, false);
                return new ThreeTypeViewHolder(view);
            case Model.CARD_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_icard, parent, false);
                return new ThreeTypeViewHolder(view);
            case Model.SOIL_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_soil, parent, false);
                return new SoilTypeViewHolder(view);
            case Model.AUDIO_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_type, parent, false);
                return new AudioTypeViewHolder(view);
            case Model.WEATHER_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_weather, parent, false);
                return new Weather_Type(view);
        }
        return null;

    }

    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).type) {
            case 0:
                return Model.TEXT_TYPE;
            case 1:
                return Model.DOOR_TYPE;
            case 2:
                return Model.PIR_TYPE;
            case 3:
                return Model.CARD_TYPE;
            case 4:
                return Model.WEATHER_TYPE;
            case 5:
                return Model.SOIL_TYPE;
            case 6:
                return Model.WINDOWTYPE;
            case 10:
                return Model.AUDIO_TYPE;
            case 11:
                return Model.IMAGE_TYPE;

            default:
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        Model object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.type) {
                case Model.TEXT_TYPE:
                    ((TextTypeViewHolder) holder).txtType.setText(object.name);
                    break;
                case Model.IMAGE_TYPE:
                    ((ImageTypeViewHolder) holder).txtType.setText(object.name);
                    ((ImageTypeViewHolder) holder).image.setImageResource(object.image);
                    break;
                case Model.DOOR_TYPE:
                    if (!object.name.equals("")) {
                        ((ThreeTypeViewHolder) holder).txtType.setText(object.name);
                    }
                    ((ThreeTypeViewHolder) holder).txtEvent.setText(object.event);
                    ((ThreeTypeViewHolder) holder).image.setImageResource(object.image);
                    break;
                case Model.WINDOWTYPE:
                    if (!object.name.equals("")) {
                        ((ThreeTypeViewHolder) holder).txtType.setText(object.name);
                    }
                    ((ThreeTypeViewHolder) holder).txtEvent.setText(object.event);
                    ((ThreeTypeViewHolder) holder).image.setImageResource(object.image);
                    break;
                case Model.WEATHER_TYPE:
                    if (!object.name.equals("")) {
                        ((Weather_Type) holder).txtType.setText(object.name);
                    }
                    ((Weather_Type) holder).txtHumi.setText(object.humidity);
                    ((Weather_Type) holder).txtTemp.setText(object.temperature);
                    if(object.humidity == null) {
                        ((Weather_Type) holder).txtHumi.setText("-");
                    }
                    if(object.temperature == null) {
                        ((Weather_Type) holder).txtTemp.setText("-");
                    }
                    break;
                case Model.PIR_TYPE:
                    if (!object.name.equals("")) {
                        ((ThreeTypeViewHolder) holder).txtType.setText(object.name);
                    }
                    ((ThreeTypeViewHolder) holder).txtEvent.setText(object.event);
                    ((ThreeTypeViewHolder) holder).image.setImageResource(object.image);
                    break;
                case Model.CARD_TYPE:
                    if (!object.name.equals("")) {
                        ((ThreeTypeViewHolder) holder).txtType.setText(object.name);
                    }
                    ((ThreeTypeViewHolder) holder).txtEvent.setText(object.event);
                    ((ThreeTypeViewHolder) holder).image.setImageResource(object.image);
                    break;
                case Model.SOIL_TYPE:
                    if (!object.name.equals("")) {
                        ((SoilTypeViewHolder) holder).txtType.setText(object.name);
                    }
                    ((SoilTypeViewHolder) holder).txtEvent.setText(object.event);
                    break;
                case Model.AUDIO_TYPE:
                    ((AudioTypeViewHolder) holder).txtType.setText(object.name);
                    ((AudioTypeViewHolder) holder).fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (fabStateVolume) {
                                if (mPlayer.isPlaying()) {
                                    mPlayer.stop();
                                }
                                ((AudioTypeViewHolder) holder).fab.setImageResource(R.drawable.volume);
                                fabStateVolume = false;
                            } else {
                                mPlayer = MediaPlayer.create(mContext, R.raw.sound);
                                mPlayer.setLooping(true);
                                mPlayer.start();
                                ((AudioTypeViewHolder) holder).fab.setImageResource(R.drawable.mute);
                                fabStateVolume = true;
                            }
                        }
                    });
                    break;
            }
        }
    }
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
