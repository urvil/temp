package com.reveautomation.revesmartsecuritykit_online.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

public class CustomRadioButton extends AppCompatRadioButton {

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomRadioButton(Context context) {
        super(context);
    }

}
