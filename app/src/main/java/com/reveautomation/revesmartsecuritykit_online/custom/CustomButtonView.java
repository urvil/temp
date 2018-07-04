package com.reveautomation.revesmartsecuritykit_online.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

public class CustomButtonView extends AppCompatButton {

    public CustomButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomButtonView(Context context) {
        super(context);
    }

}
