package com.reveautomation.revesmartsecuritykit_online.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.reveautomation.revesmartsecuritykit_online.R;


public class CustomFontHelper {

    /**
     * Sets a font on a textview based on the custom com.my.package:font
     * attribute If the custom font attribute isn't found in the attributes
     * nothing happens
     *
     * @param textview
     * @param context
     * @param attrs
     * @author
     * @version 1.0
     * @date
     */
    public static void setCustomFont(TextView textview, Context context,
                                     AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                 R.styleable.ManagerAPPTextView);
        String font = a.getString(R.styleable.ManagerAPPTextView_textview_font_name);
        setCustomFont(textview, font, context);
        a.recycle();
    }

    /**
     * Sets a font on a button based on the custom com.my.package:font attribute
     * If the custom font attribute isn't found in the attributes nothing
     * happens
     *
     * @param button
     * @param context
     * @param attrs
     * @author
     * @version 1.0
     * @date
     */

//	public static void setCustomFont(Button button, Context context,
//			AttributeSet attrs) {
//		TypedArray a = context.obtainStyledAttributes(attrs,
//				R.styleable.TuisyButtonView);
//		String font = a.getString(R.styleable.TuisyButtonView_button_font_name);
//		setCustomFont(button, font, context);
//		a.recycle();
//	}

    /**
     * Sets a font on a switch based on the custom com.my.package:font attribute
     * If the custom font attribute isn't found in the attributes nothing
     * happens
     *
     * @param button
     * @param context
     * @param attrs
     * @author
     * @version 1.0
     * @date
     */

//	public static void setCustomFont(Switch switch1, Context context,
//			AttributeSet attrs) {
//		TypedArray a = context.obtainStyledAttributes(attrs,
//				R.styleable.TuisySwitchView);
//		String font = a.getString(R.styleable.TuisySwitchView_switch_font_name);
//		setCustomFont(switch1, font, context);
//		a.recycle();
//	}

    /**
     * Sets a font on a textview
     *
     * @param textview
     * @param font
     * @param context
     */
    public static void setCustomFont(TextView textview, String font,
                                     Context context) {
        if (font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, context);
        if (tf != null) {
            textview.setTypeface(tf);
        }
    }

    /**
     * Sets a font on a button
     *
     * @param button
     * @param font
     * @param context
     */
    public static void setCustomFont(Button button, String font, Context context) {
        if (font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, context);
        if (tf != null) {
            button.setTypeface(tf);
        }
    }


    public static void setCustomFont(Switch switch1, String font,
                                     Context context) {
        if (font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, context);
        if (tf != null) {
            switch1.setTypeface(tf);
        }
    }

    /**
     * Sets a font on a edit text based on the custom com.my.package:font
     * attribute If the custom font attribute isn't found in the attributes
     * nothing happens
     *
     * @param edittext
     * @param context
     * @param attrs
     * @author
     * @version 1.0
     * @date
     */
//	public static void setCustomFont(EditText editText, Context context,
//			AttributeSet attrs) {
//		TypedArray a = context.obtainStyledAttributes(attrs,
//				R.styleable.TuisyEditTextView);
//		String font = a.getString(R.styleable.TuisyEditTextView_edit_text_font_name);
//		setCustomFont(editText, font, context);
//		a.recycle();
//	}

}
