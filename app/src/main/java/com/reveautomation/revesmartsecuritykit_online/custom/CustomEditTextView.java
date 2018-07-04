package com.reveautomation.revesmartsecuritykit_online.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class CustomEditTextView extends AppCompatEditText {

    public CustomEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomEditTextView(Context context) {
        super(context);
    }

}
