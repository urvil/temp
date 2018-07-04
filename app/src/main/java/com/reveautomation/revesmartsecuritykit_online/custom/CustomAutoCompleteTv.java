package com.reveautomation.revesmartsecuritykit_online.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;

public class CustomAutoCompleteTv extends AppCompatAutoCompleteTextView {

    public CustomAutoCompleteTv(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomAutoCompleteTv(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomAutoCompleteTv(Context context) {
        super(context);
    }

}
